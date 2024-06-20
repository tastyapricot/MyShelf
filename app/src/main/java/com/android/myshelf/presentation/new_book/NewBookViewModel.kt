package com.android.myshelf.presentation.new_book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.data.repositories.BookRepository
import com.android.myshelf.data.validators.book.ValidateAuthor
import com.android.myshelf.data.validators.book.ValidateDescription
import com.android.myshelf.data.validators.book.ValidateTitle
import com.android.myshelf.data.validators.new_book.ValidateGenre
import com.android.myshelf.presentation.shared.models.BaseIntent
import com.android.myshelf.presentation.shared.models.BaseLabel
import com.android.myshelf.presentation.shared.models.BaseState
import com.android.myshelf.presentation.shared.models.BaseViewModel
import com.android.myshelf.presentation.shared.models.TextFieldState
import com.android.myshelf.presentation.shared.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewBookViewModel
@Inject
constructor(
    private val bookRepository: BookRepository,
    private val validateTitle: ValidateTitle,
    private val validateAuthor: ValidateAuthor,
    private val validateDescription: ValidateDescription,
    private val validateGenre: ValidateGenre,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<NewBookContract.Intent, NewBookContract.State, NewBookContract.Label>() {

    private val genre: Genre? = Screen.NewBook.from(savedStateHandle).genre

    init {
        updateState { state.value.copy(genreState = state.value.genreState.copy(genre = genre)) }
    }

    override fun setInitialState(): NewBookContract.State {
        return NewBookContract.State()
    }

    override fun onIntent(intent: NewBookContract.Intent) {
        when (intent) {
            NewBookContract.Intent.Back -> publishLabel(NewBookContract.Label.Close)
            NewBookContract.Intent.Create -> onCreateBook()
            is NewBookContract.Intent.OnTitleChange -> onTitleChange(intent)
            is NewBookContract.Intent.OnAuthorChange -> onAuthorChange(intent)
            is NewBookContract.Intent.OnDescriptionChange -> onDescriptionChange(intent)
            is NewBookContract.Intent.OnGenreSelected -> onGenreSelected(intent)
            NewBookContract.Intent.SelectGenre -> publishLabel(NewBookContract.Label.SelectGenre)
        }
    }


    private fun onTitleChange(intent: NewBookContract.Intent.OnTitleChange) {
        updateState {
            state.value.copy(titleFieldState = state.value.titleFieldState.copy(text = intent.text))
        }
    }

    private fun onAuthorChange(intent: NewBookContract.Intent.OnAuthorChange) {
        updateState {
            state.value.copy(authorFieldState = state.value.authorFieldState.copy(text = intent.text))
        }
    }

    private fun onDescriptionChange(intent: NewBookContract.Intent.OnDescriptionChange) {
        updateState {
            state.value.copy(descriptionFieldState = state.value.descriptionFieldState.copy(text = intent.text))
        }
    }

    private fun onGenreSelected(intent: NewBookContract.Intent.OnGenreSelected) {
        viewModelScope.launch {
            updateState {
                state.value.copy(
                    genreState = state.value.genreState.copy(
                        genre = intent.genre
                    )
                )
            }
        }
    }

    private fun onCreateBook() {
        val title = state.value.titleFieldState.text
        val author = state.value.authorFieldState.text
        val description = state.value.descriptionFieldState.text
        val genre = state.value.genreState.genre

        val titleValidationResult = validateTitle(title)
        val authorValidationResult = validateAuthor(author)
        val descriptionValidationResult = validateDescription(description)
        val genreValidationResult = validateGenre(genre)

        val hasError = listOf(
            titleValidationResult,
            authorValidationResult,
            descriptionValidationResult,
            genreValidationResult
        )
            .any { !it.successful }

        if (hasError) {
            updateState {
                state.value.copy(
                    titleFieldState = state.value.titleFieldState.copy(
                        error = titleValidationResult.errorMessage
                    ),
                    authorFieldState = state.value.authorFieldState.copy(
                        error = authorValidationResult.errorMessage
                    ),
                    descriptionFieldState = state.value.descriptionFieldState.copy(
                        error = descriptionValidationResult.errorMessage
                    ),
                    genreState = state.value.genreState.copy(
                        error = genreValidationResult.errorMessage
                    )
                )
            }

            return
        }

        viewModelScope.launch {
            bookRepository.addBook(
                Book(
                    id = 0,
                    title = title,
                    author = author,
                    description = description,
                    genreId = state.value.genreState.genre?.id,
                )
            )
            publishLabel(NewBookContract.Label.Create)
        }
    }
}


class NewBookContract {
    sealed interface Intent : BaseIntent {
        data object Back : Intent
        data object Create : Intent
        data class OnTitleChange(val text: String) : Intent
        data class OnAuthorChange(val text: String) : Intent
        data class OnDescriptionChange(val text: String) : Intent
        data class OnGenreSelected(val genre: Genre) : Intent
        data object SelectGenre : Intent
    }

    data class State(
        val titleFieldState: TextFieldState = TextFieldState(),
        val authorFieldState: TextFieldState = TextFieldState(),
        val descriptionFieldState: TextFieldState = TextFieldState(),
        val genreState: GenreState = GenreState(),
    ) : BaseState {

        data class GenreState(
            val genre: Genre? = null,
            val error: String? = null,
        )
    }

    sealed interface Label : BaseLabel {
        data object Close : Label
        data object Create : Label
        data object SelectGenre : Label
    }
}