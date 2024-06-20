package com.android.myshelf.data.repositories

import com.android.myshelf.data.local.db.BookDao
import com.android.myshelf.data.local.dbos.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookRepository
@Inject
constructor(
    private val bookDao: BookDao,
) {

    fun observeBooks(): Flow<List<Book>> {
        return bookDao.observeBooks()
    }

    suspend fun getBooks(): List<Book> {
        return bookDao.getBooks()
    }

    fun observeBooksByGenre(
        genreId: Int,
    ): Flow<List<Book>> {
        return bookDao.observeBooksByGenre(genreId)
    }

    suspend fun upsertBook(book: Book) {
        bookDao.upsertBook(book)
    }

    suspend fun addBook(book: Book) {
        bookDao.addBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }
}