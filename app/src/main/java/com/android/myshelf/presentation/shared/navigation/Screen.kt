package com.android.myshelf.presentation.shared.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.presentation.shared.utils.serializableType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Genres : Screen

    @Serializable
    data class GenreBooks(val genre: Genre) : Screen {
        companion object {
            val typeMap = mapOf(typeOf<Genre>() to serializableType<Genre>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<GenreBooks>(typeMap)
        }
    }

    @Serializable
    data object NewGenre : Screen

    @Serializable
    data class EditGenre(val genre: Genre) : Screen {

        companion object {
            val typeMap = mapOf(typeOf<Genre>() to serializableType<Genre>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<EditGenre>(typeMap)
        }
    }

    @Serializable
    data class NewBook(val genre: Genre? = null) : Screen {

        companion object {
            const val KEY_GENRE = "genre"

            val typeMap =
                mapOf(typeOf<Genre?>() to serializableType<Genre?>(isNullableAllowed = true))

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<NewBook>(typeMap)
        }
    }

    @Serializable
    data class EditBook(val book: Book) : Screen {

        companion object {
            val typeMap = mapOf(typeOf<Book>() to serializableType<Book>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<EditBook>(typeMap)
        }
    }

    @Serializable
    data object SelectGenre : Screen
}