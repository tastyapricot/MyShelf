package com.android.myshelf.data.local.dbos

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.myshelf.data.local.db.LibraryDatabase.Companion.GENRES_TABLE_NAME
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = GENRES_TABLE_NAME)
@Serializable
@Parcelize
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
) : Parcelable {
    fun getContentValues(): ContentValues {
        return ContentValues().apply {
            put("id", id)
            put("name", name)
        }
    }
}