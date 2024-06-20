package com.android.myshelf.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.android.myshelf.data.local.db.LibraryDatabase.Companion.BOOKS_TABLE_NAME
import com.android.myshelf.data.local.dbos.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM $BOOKS_TABLE_NAME")
    fun observeBooks(): Flow<List<Book>>

    @Query("SELECT * FROM $BOOKS_TABLE_NAME")
    suspend fun getBooks(): List<Book>

    @Query("SELECT * FROM $BOOKS_TABLE_NAME WHERE genreId = :genreId")
    fun observeBooksByGenre(genreId: Int): Flow<List<Book>>

    @Upsert
    suspend fun upsertBook(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}