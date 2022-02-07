package com.sedat.englishvocabularybook.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.sedat.englishvocabularybook.R
import com.sedat.englishvocabularybook.databinding.FragmentInsertWordBinding
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.viewmodel.InsertWordViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class InsertWordFragment(
        private val glide: RequestManager
) : Fragment() {

    private var fragmentBinding: FragmentInsertWordBinding ?= null
    private val binding get() = fragmentBinding!!

    private lateinit var viewModel: InsertWordViewModel

    private var selectedPicture: Uri ?= null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private var isUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentInsertWordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(InsertWordViewModel::class.java)

        var word: Word ?= null
        arguments?.let {
            isUpdate = InsertWordFragmentArgs.fromBundle(it).isUpdate
            word = InsertWordFragmentArgs.fromBundle(it).word

            if(isUpdate){
                word?.let { word ->
                    bindFromUpdate(word)
                }
            }
        }

        binding.imageViewSelect.setOnClickListener {
            showDialog(view)
        }

        binding.saveButton.setOnClickListener {
            if(isUpdate){
                word?.let {
                    updateWord(it)
                    clearUI()
                }
            }
            else{
                insertWord()
                clearUI()
            }
        }

        observe()
    }

    private fun observe(){
        viewModel.selectedImageBitmap.observe(viewLifecycleOwner, Observer {
            it?.let {
                glide.load(it).into(binding.imageViewSelect)
            }
        })
    }

    private fun bindFromUpdate(word: Word){
        if(word.imageBitmap != null)
            binding.imageViewSelect.setImageBitmap(word.imageBitmap)
        binding.enEdittext.setText(word.en)
        binding.trEdittext.setText(word.tr)
        binding.sentenceEdittext.setText(word.sentence)
        binding.saveButton.setText(R.string.update)
    }

    private fun updateWord(word: Word){
        val en = binding.enEdittext.text.toString().capitalize(Locale.ROOT)
        val tr = binding.trEdittext.text.toString().capitalize(Locale.ROOT)
        val sentence = binding.sentenceEdittext.text.toString().capitalize(Locale.ROOT)
        if(en.isNotEmpty() && tr.isNotEmpty()){
            word.en = en
            word.tr = tr
            word.sentence = sentence
            viewModel.updateWord(word)
            clearUI()
        }else
            Toast.makeText(requireContext(), getString(R.string.error_enter_word), Toast.LENGTH_LONG).show()
    }

    private fun insertWord(){
        val en = binding.enEdittext.text.toString().capitalize(Locale.ROOT)
        val tr = binding.trEdittext.text.toString().capitalize(Locale.ROOT)
        val sentence = binding.sentenceEdittext.text.toString().capitalize(Locale.ROOT)

        val isSave = viewModel.insertWord(en, tr, sentence)
        if(isSave) {
            clearUI()
        }
    }

    private fun clearUI(){
        binding.enEdittext.setText("")
        binding.trEdittext.setText("")
        binding.sentenceEdittext.setText("")
        binding.imageViewSelect.setImageResource(R.drawable.add_image_128)
        binding.saveButton.setText(R.string.save)
        isUpdate = false
    }

    private fun showDialog(view: View){  //Select image from gallery or internet
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.custom_alert_dialog, null)
        val alertDialog = AlertDialog.Builder(view.context)
        alertDialog.setCancelable(true)
        alertDialog.setView(layout)

        val dialog: AlertDialog = alertDialog.create()
        if(dialog.window != null)  //Set background transparent
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()

        layout.findViewById<Button>(R.id.Gallery).setOnClickListener {
            selectImageFromGallery(view)
            dialog.dismiss()
        }
        layout.findViewById<Button>(R.id.Internet).setOnClickListener {
            val action = InsertWordFragmentDirections.actionInsertWordFragmentToSearchImageFragment()
            Navigation.findNavController(view).navigate(action)
            dialog.dismiss()
        }
    }

    private fun selectImageFromGallery(view: View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view, getString(R.string.permission_needed_for_gallery), Snackbar.LENGTH_LONG).setAction(getString(R.string.give_permission),
                    View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult =result.data
                if(intentFromResult != null){
                    selectedPicture = intentFromResult.data

                    try {
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedPicture!!)
                            var imageBitmap = ImageDecoder.decodeBitmap(source)
                            imageBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)  //sdk>=28 de bitmap uygulanırken hata vermemesi için.
                            binding.imageViewSelect.setImageBitmap(imageBitmap)
                            viewModel.setSelectedImage(imageBitmap)
                        }else{
                            val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedPicture)
                            glide.load(imageBitmap).into(binding.imageViewSelect)
                            viewModel.setSelectedImage(imageBitmap)
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
        ){
            if(it){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                //permission denied
                Toast.makeText(requireContext(), getString(R.string.permission_needed), Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
        viewModel.clearData(false)
    }


}