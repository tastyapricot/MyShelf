package com.android.myshelf.data.local.dbos

import com.android.myshelf.R

class DefaultGenre
private constructor(
    val id: Int,
    val nameRes: Int,
) {
    companion object {
        private val THRILLER = DefaultGenre(
            id = 1,
            nameRes = R.string.thriller,
        )

        private val ROMANCE = DefaultGenre(
            id = 2,
            nameRes = R.string.romance,
        )

        private val FANTASY = DefaultGenre(
            id = 3,
            nameRes = R.string.fantasy,
        )

        private val SCIENCE = DefaultGenre(
            id = 4,
            nameRes = R.string.science,
        )

        private val DETECTIVE = DefaultGenre(
            id = 5,
            nameRes = R.string.detective,
        )

        private val FICTION = DefaultGenre(
            id = 6,
            nameRes = R.string.fiction,
        )

        private val HORROR = DefaultGenre(
            id = 7,
            nameRes = R.string.horror,
        )

        private val BIOGRAPHY = DefaultGenre(
            id = 8,
            nameRes = R.string.biography,
        )

        private val HISTORY = DefaultGenre(
            id = 9,
            nameRes = R.string.history,
        )

        private val ADVENTURE = DefaultGenre(
            id = 10,
            nameRes = R.string.adventure,
        )

        val DEFAULT_GENRES = listOf(
            THRILLER,
            ROMANCE,
            FANTASY,
            SCIENCE,
            DETECTIVE,
            FICTION,
            HORROR,
            BIOGRAPHY,
            HISTORY,
            ADVENTURE
        )
    }
}