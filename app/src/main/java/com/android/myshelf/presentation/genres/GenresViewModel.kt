package com.android.myshelf.presentation.genres

import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Genre
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
class GenresViewModel
@Inject
constructor(
    private val genreRepository: GenreRepository,
) : BaseViewModel<GenresContract.Intent, GenresContract.State, GenresContract.Label>() {

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

    override fun setInitialState(): GenresContract.State {
        return GenresContract.State()
    }


    override fun onIntent(intent: GenresContract.Intent) {
        when (intent) {
            is GenresContract.Intent.OpenGenre -> publishLabel(GenresContract.Label.OpenGenre(intent.genre))
            GenresContract.Intent.AddGenre -> publishLabel(GenresContract.Label.AddGenre)
            is GenresContract.Intent.DeleteGenre -> onDeleteGenre(intent)
            is GenresContract.Intent.Navigate -> publishLabel(GenresContract.Label.Navigate(intent.screen))
        }
    }

    private fun onDeleteGenre(intent: GenresContract.Intent.DeleteGenre) {
        viewModelScope.launch { genreRepository.deleteGenre(intent.genre) }
    }
}

class GenresContract {
    sealed interface Intent : BaseIntent {
        data class OpenGenre(val genre: Genre) : Intent
        data class DeleteGenre(val genre: Genre) : Intent
        data object AddGenre : Intent
        data class Navigate(val screen: Screen) : Intent
    }

    data class State(
        val genres: List<Genre> = emptyList(),
        val isEmpty: Boolean = false,
    ) : BaseState

    sealed interface Label : BaseLabel {
        data class OpenGenre(val genre: Genre) : Label
        data object AddGenre : Label
        data class Navigate(val screen: Screen) : Label
    }
}