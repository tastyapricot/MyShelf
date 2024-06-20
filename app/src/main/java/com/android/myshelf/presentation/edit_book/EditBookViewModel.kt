package com.android.myshelf.presentation.edit_book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.data.repositories.BookRepository
import com.android.myshelf.data.repositories.GenreRepository
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
class EditBookViewModel
@Inject
constructor(
    private val genreRepository: GenreRepository,
    private val bookRepository: BookRepository,
    private val validateTitle: ValidateTitle,
    private val validateAuthor: ValidateAuthor,
    private val validateDescription: ValidateDescription,
    private val validateGenre: ValidateGenre,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<EditBookContract.Intent, EditBookContract.State, EditBookContract.Label>() {

    private val book: Book = Screen.EditBook.from(savedStateHandle).book

    init {
        viewModelScope.launch {
            if (book.genreId != null) {
                val genre = genreRepository.getGenre(book.genreId)
                updateState { state.value.copy(genreState = state.value.genreState.copy(genre = genre)) }
            }
        }
        viewModelScope.launch {
            updateState { state.value.copy(titleFieldState = state.value.titleFieldState.copy(text = book.title)) }
            updateState { state.value.copy(authorFieldState = state.value.authorFieldState.copy(text = book.author)) }
            updateState {
                state.value.copy(
                    descriptionFieldState = state.value.descriptionFieldState.copy(
                        text = book.description
                    )
                )
            }
        }
    }


    override fun setInitialState(): EditBookContract.State {
        return EditBookContract.State()
    }

    override fun onIntent(intent: EditBookContract.Intent) {
        when (intent) {
            EditBookContract.Intent.Back -> publishLabel(EditBookContract.Label.Back)
            EditBookContract.Intent.Save -> onSave()
            is EditBookContract.Intent.OnTitleChange -> onTitleChange(intent)
            is EditBookContract.Intent.OnAuthorChange -> onAuthorChange(intent)
            is EditBookContract.Intent.OnDescriptionChange -> onDescriptionChange(intent)
            is EditBookContract.Intent.OnGenreSelected -> onGenreSelected(intent)
            EditBookContract.Intent.SelectGenre -> publishLabel(EditBookContract.Label.SelectGenre)
            EditBookContract.Intent.Delete -> onDelete()
        }
    }

    private fun onDelete() {
        viewModelScope.launch {
            bookRepository.deleteBook(book)
            publishLabel(EditBookContract.Label.Back)
        }
    }


    private fun onTitleChange(intent: EditBookContract.Intent.OnTitleChange) {
        updateState {
            state.value.copy(titleFieldState = state.value.titleFieldState.copy(text = intent.text))
        }
    }

    private fun onAuthorChange(intent: EditBookContract.Intent.OnAuthorChange) {
        updateState {
            state.value.copy(authorFieldState = state.value.authorFieldState.copy(text = intent.text))
        }
    }

    private fun onDescriptionChange(intent: EditBookContract.Intent.OnDescriptionChange) {
        updateState {
            state.value.copy(descriptionFieldState = state.value.descriptionFieldState.copy(text = intent.text))
        }
    }

    private fun onGenreSelected(intent: EditBookContract.Intent.OnGenreSelected) {
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

    private fun onSave() {
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
            bookRepository.upsertBook(
                book.copy(
                    title = title,
                    author = author,
                    description = description,
                    genreId = genre?.id
                )
            )
            publishLabel(EditBookContract.Label.Save)
        }
    }
}


class EditBookContract {
    sealed interface Intent : BaseIntent {
        data object Back : Intent
        data object Save : Intent
        data class OnTitleChange(val text: String) : Intent
        data class OnAuthorChange(val text: String) : Intent
        data class OnDescriptionChange(val text: String) : Intent
        data class OnGenreSelected(val genre: Genre) : Intent
        data object SelectGenre : Intent
        data object Delete : Intent
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
        data object Back : Label
        data object Save : Label
        data object SelectGenre : Label
    }
}