package com.sedat.englishvocabularybook.repo

import android.content.Context
import android.widget.Toast
import com.sedat.englishvocabularybook.R
import com.sedat.englishvocabularybook.api.ImageApi
import com.sedat.englishvocabularybook.model.ImageResponse
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.model.WordFTS
import com.sedat.englishvocabularybook.room.VocabularDB
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageRepository @Inject constructor(
        private val imageApi: ImageApi,
        @ApplicationContext private val context: Context
): ImageRepositoryInterface {

    val dao = VocabularDB(context).vocabularyDao()

    override fun searchImage(query: String): Single<ImageResponse> {
        return imageApi.SearchImage(query)
    }

    override suspend fun insertWord(word: Word) {
        dao.insertWord(word)
    }

    override fun getWords(): Flow<List<Word>> {
        return dao.getWords()
    }

    override fun getWordsInSorted(): Flow<List<Word>> {
        return dao.getWordsInSorted()
    }

    override suspend fun getWordCount(): Int {
        return dao.getWordCount()
    }

    override suspend fun getRandomWord(): Word {
        return dao.getRandomWord()
    }

    override suspend fun searchWord(query: String): List<Word> {
        return dao.searchWord(query)
    }

    override suspend fun deleteWord(word: Word) {
        dao.deleteWord(word)
        Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
    }

    override suspend fun updateWord(word: Word) {
        dao.updateWord(word)
    }

}