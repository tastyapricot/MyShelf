package com.android.myshelf.data.repositories

import com.android.myshelf.data.local.db.GenreDao
import com.android.myshelf.data.local.dbos.Genre
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenreRepository
@Inject
constructor(
    private val genreDao: GenreDao,
) {
    fun observeGenres(): Flow<List<Genre>> {
        return genreDao.observeGenres()
    }

    suspend fun addGenre(
        genreName: String,
    ) {
        genreDao.upsertGenre(
            Genre(
                id = 0,
                name = genreName
            )
        )
    }

    suspend fun upsertGenre(
        genre: Genre,
    ) {
        genreDao.upsertGenre(genre)
    }

    suspend fun deleteGenre(
        genre: Genre,
    ) {
        genreDao.deleteGenre(genre)
    }

    suspend fun getGenre(id: Int): Genre {
        return genreDao.getGenreById(id)
    }

    fun observeGenre(id: Int): Flow<Genre> {
        return genreDao.observeGenreById(id)
    }
}