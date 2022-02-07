package com.sedat.englishvocabularybook.viewmodel

import android.app.Application
import android.content.Context
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.repo.ImageRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ImageRepositoryInterface,
    @ApplicationContext application: Context
): BaseViewModel(application as Application) {

    private val words = MutableLiveData<List<Word>>()
    val wordList: LiveData<List<Word>>
        get() = words
    fun getWords() {  //: Flow<List<Word>> = repository.getWords()
        launch {
            val words_ = repository.getWords()
            words_.collectLatest {
                words.value = it
            }
        }
    }

    fun getWordsInSorted(): Flow<List<Word>> = repository.getWordsInSorted()

    fun deleteWord(word: Word){
        launch {
            repository.deleteWord(word)
        }
    }

    val wordCount = MutableLiveData<Int>()
    fun getWordCount(){
        launch {
            val count = repository.getWordCount()
            wordCount.postValue(count)
        }
    }

    fun searchWord(query: Editable?){
        launch {
            if(query.isNullOrBlank()){
                getWords()
            }else{
                repository.searchWord("*$query*").let {
                    words.value = it
                }
            }
        }
    }

}