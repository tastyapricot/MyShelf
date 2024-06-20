package com.android.myshelf.presentation.new_genre

import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.repositories.GenreRepository
import com.android.myshelf.data.validators.new_genre.ValidateGenreName
import com.android.myshelf.presentation.shared.models.BaseIntent
import com.android.myshelf.presentation.shared.models.BaseLabel
import com.android.myshelf.presentation.shared.models.BaseState
import com.android.myshelf.presentation.shared.models.BaseViewModel
import com.android.myshelf.presentation.shared.models.TextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGenreViewModel
@Inject
constructor(
    private val genreRepository: GenreRepository,
    private val validateGenreName: ValidateGenreName,
) : BaseViewModel<NewGenreContract.Intent, NewGenreContract.State, NewGenreContract.Label>() {

    override fun setInitialState(): NewGenreContract.State {
        return NewGenreContract.State()
    }

    override fun onIntent(intent: NewGenreContract.Intent) {
        when (intent) {
            NewGenreContract.Intent.Back -> publishLabel(NewGenreContract.Label.Close)
            NewGenreContract.Intent.Create -> onAddGenre()
            is NewGenreContract.Intent.OnGenreNameChange -> onGenreNameChange(intent)
        }
    }

    private fun onGenreNameChange(intent: NewGenreContract.Intent.OnGenreNameChange) {
        updateState {
            state.value.copy(genreNameFieldState = state.value.genreNameFieldState.copy(text = intent.text))
        }
    }

    private fun onAddGenre() {
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
            genreRepository.addGenre(genreName.trim())
            publishLabel(NewGenreContract.Label.Create)
        }
    }
}


class NewGenreContract {
    sealed interface Intent : BaseIntent {
        data object Back : Intent
        data object Create : Intent
        data class OnGenreNameChange(val text: String) : Intent
    }

    data class State(
        val genreNameFieldState: TextFieldState = TextFieldState(),
    ) : BaseState

    sealed interface Label : BaseLabel {
        data object Close : Label
        data object Create : Label
    }
}