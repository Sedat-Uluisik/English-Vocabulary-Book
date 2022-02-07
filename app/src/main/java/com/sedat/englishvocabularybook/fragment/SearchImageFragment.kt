package com.sedat.englishvocabularybook.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sedat.englishvocabularybook.adapter.SearchImageAdapter
import com.sedat.englishvocabularybook.databinding.FragmentSearchImageBinding
import com.sedat.englishvocabularybook.model.ImageResponse
import com.sedat.englishvocabularybook.viewmodel.InsertWordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchImageFragment @Inject constructor(
    private val searchImageAdapter: SearchImageAdapter
) : Fragment() {

    private var fragmentBinding: FragmentSearchImageBinding ?= null
    private val binding get() = fragmentBinding!!

    private lateinit var viewModel: InsertWordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentSearchImageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(InsertWordViewModel::class.java)

        binding.recyclerSearch.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerSearch.adapter = searchImageAdapter

        var job: Job ?= null
        binding.searchEditText.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(800)
                it?.let {
                    if(it.toString().isNotEmpty()){
                        viewModel.searchImage(it.toString())
                    }else
                        clearData()
                } ?: clearData()
            }
        }

        searchImageAdapter.imageClick {
            findNavController().popBackStack()
            viewModel.setSelectedImage(it)
        }

        observe()
    }

    private fun observe(){
        viewModel.images.observe(viewLifecycleOwner, Observer {
            it?.let {
                searchImageAdapter.images = it.hits.map { imageResult ->
                    imageResult.previewURL
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearData()
    }

    private fun clearData(){
        viewModel.clearData(true)
        searchImageAdapter.images = listOf()
    }
}