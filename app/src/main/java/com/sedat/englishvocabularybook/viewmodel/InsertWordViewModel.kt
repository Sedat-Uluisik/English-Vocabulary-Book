package com.sedat.englishvocabularybook.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sedat.englishvocabularybook.R
import com.sedat.englishvocabularybook.model.ImageResponse
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.repo.ImageRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsertWordViewModel @Inject constructor(
        private val imageRepository: ImageRepositoryInterface,
        @ApplicationContext private val application: Context
): BaseViewModel(application as Application) {

    private val disposable = CompositeDisposable()

    private var searchedImages = MutableLiveData<ImageResponse>()
    val images: LiveData<ImageResponse>
        get() = searchedImages

    fun searchImage(query: String){
        disposable.add(
            imageRepository.searchImage(query)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object :DisposableSingleObserver<ImageResponse>(){
                        override fun onSuccess(t: ImageResponse) {
                            searchedImages.value = t
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                    })
        )
    }

    private var selectedImage = MutableLiveData<Bitmap>(null)
    val selectedImageBitmap: LiveData<Bitmap>
        get() = selectedImage
    fun setSelectedImage(bitmap: Bitmap){
        selectedImage.postValue(bitmap)
    }

    private fun insertWord(word: Word){
        launch {
            imageRepository.insertWord(word)
        }
    }

    fun insertWord(en: String, tr: String, sentence: String): Boolean{

        return if(en.isNotEmpty() && tr.isNotEmpty()){
            val word = Word(
                    0,
                    en,
                    tr,
                    selectedImageBitmap.value,
                    if(sentence.isNotEmpty()) sentence else "---"
            )
            insertWord(word)
            selectedImage.postValue(null)
            Toast.makeText(application, application.getString(R.string.saved), Toast.LENGTH_SHORT).show()
            true
        }else{
            Toast.makeText(application, application.getString(R.string.error_enter_word), Toast.LENGTH_SHORT).show()
            false
        }

    }

    fun updateWord(word: Word){
        launch {
            if(selectedImage.value != null && selectedImageBitmap.value != null)
                word.imageBitmap = selectedImage.value

            word.sentence = if(word.sentence.isNotEmpty()) word.sentence else "---"

            imageRepository.updateWord(word)
            Toast.makeText(application, application.getString(R.string.updated), Toast.LENGTH_SHORT).show()
        }
    }

    fun clearData(isOnlyImages: Boolean){
        if(isOnlyImages){
            searchedImages.postValue(null)
            return
        }
        selectedImage.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}