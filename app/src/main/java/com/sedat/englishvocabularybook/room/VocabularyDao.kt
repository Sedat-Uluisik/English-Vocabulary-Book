package com.sedat.englishvocabularybook.room

import androidx.room.*
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.model.WordFTS
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Query("SELECT * FROM Word")
    fun getWords(): Flow<List<Word>>

    @Query("SELECT COUNT(en) FROM Word")
    suspend fun getWordCount(): Int

    @Query("SELECT * FROM Word ORDER BY en ASC")
    fun getWordsInSorted(): Flow<List<Word>>

    @Query("SELECT * FROM Word ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word

    @Query("SELECT * FROM Word JOIN Word_fts ON Word.en = Word_fts.en OR Word.tr = Word_fts.tr OR Word.sentence = Word_fts.sentence WHERE Word_fts MATCH:query")
    suspend fun searchWord(query: String): List<Word>

    @Delete
    suspend fun deleteWord(word: Word)

    @Update
    suspend fun updateWord(word: Word)
}