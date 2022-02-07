package com.sedat.englishvocabularybook.viewmodel

import android.app.Application
import android.content.Context
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.repo.ImageRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
        private val repository: ImageRepositoryInterface,
        @ApplicationContext private val context: Context
): BaseViewModel(context as Application) {

    fun getRandomWord(): Word = runBlocking {
        repository.getRandomWord()
    }

}