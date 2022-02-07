package com.sedat.englishvocabularybook.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Word")
data class Word(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val rowid: Int,
    @ColumnInfo(name = "en")
    var en: String,
    @ColumnInfo(name = "tr")
    var tr: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var imageBitmap: Bitmap? = null,
    @ColumnInfo(name = "sentence")
    var sentence: String
): Serializable

@Entity(tableName = "Word_fts")
@Fts4(contentEntity = Word::class)
data class WordFTS(
        @ColumnInfo(name = "en")
        val en: String,
        @ColumnInfo(name = "tr")
        val tr: String,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val imageBitmap: Bitmap? = null,
        @ColumnInfo(name = "sentence")
        val sentence: String
)
