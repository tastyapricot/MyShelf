package com.android.myshelf.presentation.select_genre

import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.data.repositories.GenreRepository
import com.android.myshelf.presentation.shared.models.BaseIntent
import com.android.myshelf.presentation.shared.models.BaseLabel
import com.android.myshelf.presentation.shared.models.BaseState
import com.android.myshelf.presentation.shared.models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectGenreViewModel
@Inject
constructor(
    private val genreRepository: GenreRepository,
) : BaseViewModel<SelectGenreContract.Intent, SelectGenreContract.State, SelectGenreContract.Label>() {

    init {
        viewModelScope.launch {
            genreRepository.observeGenres()
                .collect { genres ->
                    if (genres.isEmpty()) {
                        updateState { state.value.copy(isEmpty = true) }
                    } else {
                        updateState { state.value.copy(genres = genres) }
                    }
                }

        }
    }

    override fun setInitialState(): SelectGenreContract.State {
        return SelectGenreContract.State()
    }


    override fun onIntent(intent: SelectGenreContract.Intent) {
        when (intent) {
            is SelectGenreContract.Intent.SelectGenre -> publishLabel(
                SelectGenreContract.Label.SelectGenre(
                    intent.genre
                )
            )

            SelectGenreContract.Intent.AddGenre -> publishLabel(SelectGenreContract.Label.AddGenre)
            is SelectGenreContract.Intent.DeleteGenre -> onDeleteGenre(intent)
            SelectGenreContract.Intent.Back -> publishLabel(SelectGenreContract.Label.Back)
        }
    }

    private fun onDeleteGenre(intent: SelectGenreContract.Intent.DeleteGenre) {
        viewModelScope.launch { genreRepository.deleteGenre(intent.genre) }
    }
}

class SelectGenreContract {
    sealed interface Intent : BaseIntent {
        data class SelectGenre(val genre: Genre) : Intent
        data class DeleteGenre(val genre: Genre) : Intent
        data object AddGenre : Intent
        data object Back : Intent
    }

    data class State(
        val genres: List<Genre> = emptyList(),
        val isEmpty: Boolean = false,
    ) : BaseState

    sealed interface Label : BaseLabel {
        data class SelectGenre(val genre: Genre) : Label
        data object AddGenre : Label
        data object Back : Label
    }
}