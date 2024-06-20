package com.android.myshelf.data.repositories

import com.android.myshelf.data.local.dbos.Book
import javax.inject.Inject

class SearchRepository
@Inject
constructor(
    private val bookRepository: BookRepository,
) {

    suspend fun searchBooks(
        query: String,
    ): List<Book> {
        val books = bookRepository.getBooks()
        return books.filter { it.doesMatchSearchQuery(query) }
    }
}