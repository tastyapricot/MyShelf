package com.android.myshelf.data.local.dbos

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.myshelf.data.local.db.LibraryDatabase.Companion.BOOKS_TABLE_NAME
import kotlinx.serialization.Serializable

@Entity(tableName = BOOKS_TABLE_NAME)
@Serializable
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val author: String,
    val description: String,
    val genreId: Int?,
) {

    fun doesMatchSearchQuery(searchQuery: String): Boolean {
        val normalizedQuery = searchQuery.trim().lowercase()
        return title.matchesQuery(normalizedQuery) || author.matchesQuery(normalizedQuery)
    }

    private fun String.matchesQuery(query: String): Boolean {
        val normalizedStr = this.trim().lowercase()
        return containsWithTolerance(normalizedStr, query) ||
                startsWith(query) ||
                containsAllWords(normalizedStr, query)
    }

    private fun containsWithTolerance(str: String, query: String, tolerance: Int = 1): Boolean {
        val words = str.split(" ")
        val queries = query.split(" ")

        return words.any { word ->
            queries.any { q ->
                levenshteinDistance(word, q) <= tolerance
            }
        }
    }

    private fun containsAllWords(normalizedStr: String, query: String): Boolean {
        val wordsInTitle = normalizedStr.split(" ")
        val wordsInQuery = query.split(" ")

        return wordsInQuery.all { word ->
            wordsInTitle.any { it.startsWith(word) || it.contains(word) }
        }
    }

    private fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        if (lhsLength == 0) return rhsLength
        if (rhsLength == 0) return lhsLength

        val dp = Array(lhsLength + 1) { IntArray(rhsLength + 1) }

        for (i in 0..lhsLength) {
            for (j in 0..rhsLength) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + cost
                    )
                }
            }
        }

        return dp[lhsLength][rhsLength]
    }
}
