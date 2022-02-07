package com.sedat.englishvocabularybook.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.sedat.englishvocabularybook.R
import com.sedat.englishvocabularybook.databinding.FragmentPracticeBinding
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.viewmodel.PracticeViewModel
import java.util.*

class PracticeFragment : Fragment() {

    private var fragmentBinding: FragmentPracticeBinding ?= null
    private val binding get() = fragmentBinding!!

    private lateinit var viewModel: PracticeViewModel

    private var oldWord: Word ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(PracticeViewModel::class.java)

        var word: Word ?= null
        var isClicked: Boolean = false

        arguments?.let {
            word = PracticeFragmentArgs.fromBundle(it).word
            isClicked = PracticeFragmentArgs.fromBundle(it).isClicked

            if(isClicked){
                binding.helpButton.isEnabled = false
                binding.language2.isEnabled = false
            }

            word?.let { word ->
                bind(word)
            }
        }

        if(!isClicked){
            oldWord = getRandomWord()
            if(oldWord != null)
                bindFromRandomWord(oldWord!!)
        }

        binding.nextWordButton.setOnClickListener {
            binding.helpButton.isEnabled = true
            binding.language2.isEnabled = true
            if(oldWord != null){
                if(isClicked){
                    clearUI()
                    bindFromRandomWord(oldWord!!)
                    isClicked = false
                }else{
                    if(oldWord!!.tr == binding.language2.text.toString().capitalize(Locale.ROOT) ||
                            oldWord!!.en == binding.language2.text.toString().capitalize(Locale.ROOT)
                    ){
                        //get new word
                        clearUI()
                        oldWord = getRandomWord()
                        bindFromRandomWord(oldWord!!)
                        binding.language2.setTextColor(Color.BLACK)
                        binding.language2.setHintTextColor(Color.BLACK)
                    }else if(binding.language2.text.isEmpty()){
                        binding.language2.setHintTextColor(Color.RED)
                    }
                    else{
                        //isEquals is false
                        binding.language2.setTextColor(Color.RED)
                    }
                }
            }else{
                clearUI()
                oldWord = getRandomWord()
                bindFromRandomWord(oldWord!!)
                isClicked = false
            }
        }

        binding.helpButton.setOnClickListener {
            binding.language2.setTextColor(Color.BLACK)
            if(oldWord != null){
                if(oldWord!!.en == binding.language1.text)
                    binding.language2.setText(oldWord!!.tr)
                else
                    binding.language2.setText(oldWord!!.en)
            }
        }

        binding.language2.addTextChangedListener {
            it?.let {
                if(it.toString().isEmpty()){
                    binding.language2.setTextColor(Color.BLACK)
                }
            }
        }
    }

    private fun getRandomWord(): Word{
        return viewModel.getRandomWord()
    }

    private fun bind(word: Word){
        if(word.imageBitmap != null)
            binding.imageViewPractice.setImageBitmap(word.imageBitmap)
        binding.language1.text = word.en
        binding.language2.setText(word.tr)
        binding.sentenceText.text = word.sentence
    }

    private fun bindFromRandomWord(word: Word){
        if(word.imageBitmap != null)
            binding.imageViewPractice.setImageBitmap(word.imageBitmap)

        val randomNum = (0..2).random()
        if(randomNum == 0){
            binding.language1.text = word.en
        }else{
            binding.language1.text = word.tr
        }
        binding.sentenceText.text = word.sentence
    }

    private fun clearUI(){
        binding.language1.text = ""
        binding.language2.setText("")
        binding.sentenceText.text = ""
        binding.imageViewPractice.setImageResource(R.drawable.add_image_128)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}