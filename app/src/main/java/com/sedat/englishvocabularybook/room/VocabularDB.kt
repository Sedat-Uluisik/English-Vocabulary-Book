package com.sedat.englishvocabularybook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sedat.englishvocabularybook.Converters
import com.sedat.englishvocabularybook.model.Word
import com.sedat.englishvocabularybook.model.WordFTS

@Database(entities = [Word::class, WordFTS::class], exportSchema = false, version = 1)
@TypeConverters(Converters::class)
abstract class VocabularDB: RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao

    companion object{
        @Volatile
        private var instance: VocabularDB ?= null

        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: Room.databaseBuilder(
                context,
                VocabularDB::class.java,
                "vocabulary_db"
            ).build().also {
                instance = it
            }
        }

    }

}