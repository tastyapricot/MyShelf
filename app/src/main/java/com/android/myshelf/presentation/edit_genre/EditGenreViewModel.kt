package com.android.myshelf.presentation.edit_genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.repositories.GenreRepository
import com.android.myshelf.data.validators.new_genre.ValidateGenreName
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
class EditGenreViewModel
@Inject
constructor(
    private val genreRepository: GenreRepository,
    private val validateGenreName: ValidateGenreName,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditGenreContract.Intent, EditGenreContract.State, EditGenreContract.Label>() {

    private val genre = Screen.EditGenre.from(savedStateHandle).genre

    init {
        viewModelScope.launch {
            updateState {
                state.value.copy(genreNameFieldState = state.value.genreNameFieldState.copy(text = genre.name))
            }
        }
    }

    override fun setInitialState(): EditGenreContract.State {
        return EditGenreContract.State()
    }

    override fun onIntent(intent: EditGenreContract.Intent) {
        when (intent) {
            EditGenreContract.Intent.Back -> publishLabel(EditGenreContract.Label.Back)
            EditGenreContract.Intent.Save -> onSaveGenre()
            is EditGenreContract.Intent.OnGenreNameChange -> onGenreNameChange(intent)
            EditGenreContract.Intent.Delete -> onDelete()
        }
    }

    private fun onDelete() {
        viewModelScope.launch {
            genreRepository.deleteGenre(genre)
            publishLabel(EditGenreContract.Label.Back)
        }
    }

    private fun onGenreNameChange(intent: EditGenreContract.Intent.OnGenreNameChange) {
        updateState {
            state.value.copy(genreNameFieldState = state.value.genreNameFieldState.copy(text = intent.text))
        }
    }

    private fun onSaveGenre() {
        val genreName = state.value.genreNameFieldState.text
        val genreNameValidationResult = validateGenreName(genreName)

        val hasError = listOf(
            genreNameValidationResult
        ).any { !it.successful }

        if (hasError) {
            updateState {
                state.value.copy(
                    genreNameFieldState = state.value.genreNameFieldState.copy(
                        error = genreNameValidationResult.errorMessage
                    )
                )
            }

            return
        }

        viewModelScope.launch {
            genreRepository.upsertGenre(genre.copy(name = genreName))
            publishLabel(EditGenreContract.Label.Save)
        }
    }
}


class EditGenreContract {
    sealed interface Intent : BaseIntent {
        data object Back : Intent
        data object Save : Intent
        data object Delete : Intent
        data class OnGenreNameChange(val text: String) : Intent
    }

    data class State(
        val genreNameFieldState: TextFieldState = TextFieldState(),
    ) : BaseState

    sealed interface Label : BaseLabel {
        data object Back : Label
        data object Save : Label
    }
}