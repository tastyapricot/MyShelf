package com.android.myshelf.presentation.genre

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.myshelf.R
import com.android.myshelf.presentation.shared.components.BookItemView
import com.android.myshelf.presentation.shared.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun GenreScreen(
    navController: NavController,
) {
    val viewModel: GenreViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.labels.onEach { label ->
            when (label) {
                GenreContract.Label.Back -> navController.popBackStack()
                is GenreContract.Label.AddBook -> navController.navigate(
                    Screen.NewBook(label.genre)
                )

                is GenreContract.Label.EditBook -> navController.navigate(
                    Screen.EditBook(label.book)
                )

                is GenreContract.Label.EditGenre -> navController.navigate(
                    Screen.EditGenre(label.genre)
                )
            }
        }
            .launchIn(this)
    }

    GenreScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreScreen(
    state: GenreContract.State,
    onIntent: (GenreContract.Intent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.genreName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(GenreContract.Intent.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(GenreContract.Intent.EditGenre) }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit_genre),
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButtonView(onIntent)
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            LazyColumn {
                items(state.books) { book ->
                    BookItemView(
                        onBookClick = { onIntent(GenreContract.Intent.EditBook(book)) },
                        book = book
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingActionButtonView(
    onIntent: (GenreContract.Intent) -> Unit,
) {
    FloatingActionButton(
        onClick = { onIntent(GenreContract.Intent.AddBook) },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_book),
        )
    }
}
