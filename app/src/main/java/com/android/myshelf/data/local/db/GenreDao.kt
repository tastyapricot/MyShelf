package com.android.myshelf.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.android.myshelf.data.local.db.LibraryDatabase.Companion.GENRES_TABLE_NAME
import com.android.myshelf.data.local.dbos.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Query("SELECT * FROM $GENRES_TABLE_NAME")
    fun observeGenres(): Flow<List<Genre>>

    @Upsert
    suspend fun upsertGenre(genre: Genre)

    @Delete
    suspend fun deleteGenre(genre: Genre)

    @Query("SELECT * FROM $GENRES_TABLE_NAME WHERE id = :id")
    suspend fun getGenreById(id: Int): Genre

    @Query("SELECT * FROM $GENRES_TABLE_NAME WHERE id = :id")
    fun observeGenreById(id: Int): Flow<Genre>
}