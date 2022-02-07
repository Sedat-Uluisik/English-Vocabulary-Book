package com.sedat.englishvocabularybook.repo

import com.sedat.englishvocabularybook.model.ImageResponse
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.model.WordFTS
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface ImageRepositoryInterface {
    fun searchImage(query: String): Single<ImageResponse>

    suspend fun insertWord(word: Word)

    fun getWords(): Flow<List<Word>>

    fun getWordsInSorted(): Flow<List<Word>>

    suspend fun getWordCount(): Int

    suspend fun getRandomWord(): Word

    suspend fun searchWord(query: String): List<Word>

    suspend fun deleteWord(word: Word)

    suspend fun updateWord(word: Word)
}