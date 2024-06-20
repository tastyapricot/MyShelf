package com.android.myshelf.presentation.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.data.repositories.BookRepository
import com.android.myshelf.data.repositories.GenreRepository
import com.android.myshelf.presentation.shared.models.BaseIntent
import com.android.myshelf.presentation.shared.models.BaseLabel
import com.android.myshelf.presentation.shared.models.BaseState
import com.android.myshelf.presentation.shared.models.BaseViewModel
import com.android.myshelf.presentation.shared.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel
@Inject
constructor(
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<GenreContract.Intent, GenreContract.State, GenreContract.Label>() {

    private val genre = Screen.GenreBooks.from(savedStateHandle).genre

    init {
        viewModelScope.launch {
            genreRepository.observeGenre(genre.id)
                .collect { genre ->
                    if (genre != null) {
                        updateState { state.value.copy(genreName = genre.name) }
                    } else {
                        publishLabel(GenreContract.Label.Back)
                    }
                }
        }

        viewModelScope.launch {
            bookRepository.observeBooksByGenre(genre.id)
                .collect { books ->
                    updateState { state.value.copy(books = books) }
                }
        }
    }

    override fun setInitialState(): GenreContract.State {
        return GenreContract.State()
    }


    override fun onIntent(intent: GenreContract.Intent) {
        when (intent) {
            GenreContract.Intent.Back -> publishLabel(GenreContract.Label.Back)
            is GenreContract.Intent.AddBook -> publishLabel(GenreContract.Label.AddBook(genre))
            is GenreContract.Intent.EditBook -> publishLabel(GenreContract.Label.EditBook(intent.book))
            GenreContract.Intent.EditGenre -> publishLabel(GenreContract.Label.EditGenre(genre))
        }
    }
}

class GenreContract {
    sealed interface Intent : BaseIntent {
        data object Back : Intent
        data object EditGenre : Intent
        data object AddBook : Intent
        data class EditBook(val book: Book) : Intent
    }

    data class State(
        val genreName: String = "",
        val books: List<Book> = emptyList(),
    ) : BaseState

    sealed interface Label : BaseLabel {
        data object Back : Label
        data class EditGenre(val genre: Genre) : Label
        data class EditBook(val book: Book) : Label
        data class AddBook(val genre: Genre) : Label
    }
}