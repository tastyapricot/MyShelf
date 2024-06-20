package com.android.myshelf.presentation.search

import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.repositories.SearchRepository
import com.android.myshelf.presentation.shared.models.BaseIntent
import com.android.myshelf.presentation.shared.models.BaseLabel
import com.android.myshelf.presentation.shared.models.BaseState
import com.android.myshelf.presentation.shared.models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val searchRepository: SearchRepository,
) : BaseViewModel<SearchContract.Intent, SearchContract.State, SearchContract.Label>() {

    override fun setInitialState(): SearchContract.State {
        return SearchContract.State()
    }

    override fun onIntent(intent: SearchContract.Intent) {
        when (intent) {
            is SearchContract.Intent.Back -> publishLabel(SearchContract.Label.Back)
            is SearchContract.Intent.EditBook -> publishLabel(SearchContract.Label.EditBook(intent.book))
            is SearchContract.Intent.OnSearchQueryChange -> onSearchQueryChange(intent)
        }
    }

    private fun onSearchQueryChange(intent: SearchContract.Intent.OnSearchQueryChange) {
        updateState { state.value.copy(query = intent.query) }

        viewModelScope.launch {
            val books = searchRepository.searchBooks(intent.query)
            updateState { state.value.copy(results = books) }
        }
    }
}

class SearchContract {
    sealed interface Intent : BaseIntent {
        data object Back : Intent
        data class EditBook(val book: Book) : Intent
        data class OnSearchQueryChange(val query: String) : Intent
    }

    data class State(
        val isSearching: Boolean = true,
        val query: String = "",
        val results: List<Book> = emptyList(),
    ) : BaseState

    sealed interface Label : BaseLabel {
        data object Back : Label
        data class EditBook(val book: Book) : Label
    }
}