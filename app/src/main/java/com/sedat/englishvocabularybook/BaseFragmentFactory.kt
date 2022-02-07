package com.sedat.englishvocabularybook

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.bumptech.glide.RequestManager
import com.sedat.englishvocabularybook.adapter.SearchImageAdapter
import com.sedat.englishvocabularybook.fragment.HomeFragment
import com.sedat.englishvocabularybook.fragment.InsertWordFragment
import com.sedat.englishvocabularybook.fragment.SearchImageFragment
import javax.inject.Inject

class BaseFragmentFactory @Inject constructor(
    private val searchImageAdapter: SearchImageAdapter,
    private val glide: RequestManager
): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            InsertWordFragment::class.java.name -> InsertWordFragment(glide)
            SearchImageFragment::class.java.name -> SearchImageFragment(searchImageAdapter)
            HomeFragment::class.java.name -> HomeFragment()

            else -> super.instantiate(classLoader, className)
        }
    }
}