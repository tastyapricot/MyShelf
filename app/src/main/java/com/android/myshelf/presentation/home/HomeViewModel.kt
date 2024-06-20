package com.android.myshelf.presentation.home

import androidx.lifecycle.viewModelScope
import com.android.myshelf.data.local.dbos.Book
import com.android.myshelf.data.repositories.BookRepository
import com.android.myshelf.presentation.shared.models.BaseIntent
import com.android.myshelf.presentation.shared.models.BaseLabel
import com.android.myshelf.presentation.shared.models.BaseState
import com.android.myshelf.presentation.shared.models.BaseViewModel
import com.android.myshelf.presentation.shared.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val bookRepository: BookRepository,
) : BaseViewModel<HomeContract.Intent, HomeContract.State, HomeContract.Label>() {

    init {
        viewModelScope.launch {
            bookRepository.observeBooks()
                .collect { books ->
                    updateState { state.value.copy(books = books) }
                }
        }
    }

    override fun setInitialState(): HomeContract.State {
        return HomeContract.State()
    }


    override fun onIntent(intent: HomeContract.Intent) {
        when (intent) {
            HomeContract.Intent.OpenSearch -> publishLabel(HomeContract.Label.OpenSearch)
            HomeContract.Intent.AddBook -> publishLabel(HomeContract.Label.AddBook)
            is HomeContract.Intent.EditBook -> publishLabel(HomeContract.Label.EditBook(intent.book))
            is HomeContract.Intent.DeleteBook -> onDeleteBook(intent)
            is HomeContract.Intent.Navigate -> publishLabel(HomeContract.Label.Navigate(intent.screen))
        }
    }

    private fun onDeleteBook(intent: HomeContract.Intent.DeleteBook) {
        viewModelScope.launch { bookRepository.deleteBook(intent.book) }
    }
}


class HomeContract {
    sealed interface Intent : BaseIntent {
        data object OpenSearch : Intent
        data object AddBook : Intent
        data class EditBook(val book: Book) : Intent
        data class DeleteBook(val book: Book) : Intent
        data class Navigate(val screen: Screen) : Intent
    }

    data class State(
        val books: List<Book> = emptyList(),
    ) : BaseState

    sealed interface Label : BaseLabel {
        data object OpenSearch : Label
        data class EditBook(val book: Book) : Label
        data object AddBook : Label
        data class Navigate(val screen: Screen) : Label
    }
}