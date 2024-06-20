package com.android.myshelf.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.myshelf.R
import com.android.myshelf.presentation.shared.components.BookItemView
import com.android.myshelf.presentation.shared.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SearchScreen(
    navController: NavController,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.labels.onEach { label ->
            when (label) {
                SearchContract.Label.Back -> navController.popBackStack()
                is SearchContract.Label.EditBook -> navController.navigate(Screen.EditBook(label.book))
            }
        }
            .launchIn(this)
    }

    SearchScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun SearchScreen(
    state: SearchContract.State,
    onIntent: (SearchContract.Intent) -> Unit,
) {
    SearchBarView(
        state = state,
        onIntent = onIntent,
    )
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
)
private fun SearchBarView(
    state: SearchContract.State,
    onIntent: (SearchContract.Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        onIntent(SearchContract.Intent.OnSearchQueryChange(state.query))
    }

    SearchBar(
        query = state.query,
        onQueryChange = { onIntent(SearchContract.Intent.OnSearchQueryChange(it)) },
        onSearch = { },
        active = state.isSearching,
        onActiveChange = { },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            IconButton(onClick = { onIntent(SearchContract.Intent.Back) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

        },
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .then(modifier)
    ) {
        LazyColumn {
            items(state.results) { book ->
                BookItemView(
                    onBookClick = { onIntent(SearchContract.Intent.EditBook(book)) },
                    book = book
                )
            }
        }
    }
}