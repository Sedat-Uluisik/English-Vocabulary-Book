package com.sedat.englishvocabularybook.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedat.englishvocabularybook.R
import com.sedat.englishvocabularybook.adapter.HomeAdapter
import com.sedat.englishvocabularybook.databinding.FragmentHomeBinding
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Method
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var fragmentBinding: FragmentHomeBinding ?= null
    private val binding get() = fragmentBinding!!

    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        viewModel.getWordCount()
        viewModel.getWords()

        binding.recyclerHome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHome.adapter = homeAdapter
        ItemTouchHelper(swipeCallBack).attachToRecyclerView(binding.recyclerHome)

        binding.sortButton.setOnClickListener {
            lifecycle.coroutineScope.launch {
                viewModel.getWordsInSorted().collectLatest {
                    homeAdapter.words = it
                }
            }
        }

        homeAdapter.setOnItemClick {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPracticeFragment(it, true))
        }
        homeAdapter.setOnMoreBtnClick { word, view_ ->
            showPopupMenu(word, view_)
        }

        var job: Job ?= null
        binding.searchHome.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(300)
                viewModel.searchWord(it)
            }
        }

        observe()
    }

    private fun observe(){
        /*lifecycle.coroutineScope.launch {
            viewModel.getWords().collectLatest {
                homeAdapter.words = it
            }
        }*/
        viewModel.wordList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                homeAdapter.words = it
            }
        })
        viewModel.wordCount.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            binding.wordCount.text = "$it  ${getString(R.string.words)} "
        })
    }

    private val swipeCallBack = object :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val layoutPosition = viewHolder.layoutPosition
            val selectedWord = homeAdapter.words[layoutPosition]

            viewModel.deleteWord(selectedWord)
            viewModel.getWordCount()
        }

    }

    private fun showPopupMenu(word: Word, view: View){
        val popup = PopupMenu(view.context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.update ->{
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToInsertWordFragment(word, true))
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }

        //popup menüdeki ikonların görünmesi için kullanıldı.

        try {
            val fields = popup.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popup]
                    val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            popup.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}