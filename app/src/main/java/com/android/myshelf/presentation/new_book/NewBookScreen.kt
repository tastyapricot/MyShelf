package com.android.myshelf.presentation.new_book

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.myshelf.R
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.presentation.shared.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun NewBookScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
) {
    val viewModel: NewBookViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        navBackStackEntry.savedStateHandle.get<Genre>(Screen.NewBook.KEY_GENRE)
            ?.let { genre -> viewModel.onIntent(NewBookContract.Intent.OnGenreSelected(genre)) }

        viewModel.labels.onEach { label ->
            when (label) {
                NewBookContract.Label.Close -> navController.popBackStack()
                NewBookContract.Label.Create -> navController.popBackStack()
                NewBookContract.Label.SelectGenre -> navController.navigate(Screen.SelectGenre)
            }
        }
            .launchIn(this)
    }

    NewBookScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewBookScreen(
    state: NewBookContract.State,
    onIntent: (NewBookContract.Intent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.new_book),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(NewBookContract.Intent.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(NewBookContract.Intent.Create) }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.save),
                        )
                    }
                },
            )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp
                    )
            ) {
                OutlinedTextField(
                    value = state.titleFieldState.text,
                    onValueChange = { onIntent(NewBookContract.Intent.OnTitleChange(it)) },
                    label = { Text(text = stringResource(id = R.string.book_title_label)) },
                    isError = state.titleFieldState.error != null,
                    singleLine = true,
                    supportingText = {
                        if (state.titleFieldState.error != null) {
                            Text(
                                text = state.titleFieldState.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                OutlinedTextField(
                    value = state.authorFieldState.text,
                    onValueChange = { onIntent(NewBookContract.Intent.OnAuthorChange(it)) },
                    label = { Text(text = stringResource(id = R.string.book_author_label)) },
                    isError = state.authorFieldState.error != null,
                    singleLine = true,
                    supportingText = {
                        if (state.authorFieldState.error != null) {
                            Text(
                                text = state.authorFieldState.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                OutlinedTextField(
                    value = state.descriptionFieldState.text,
                    onValueChange = { onIntent(NewBookContract.Intent.OnDescriptionChange(it)) },
                    label = { Text(text = stringResource(id = R.string.book_description_label)) },
                    isError = state.descriptionFieldState.error != null,
                    maxLines = 10,
                    supportingText = {
                        if (state.descriptionFieldState.error != null) {
                            Text(
                                text = state.descriptionFieldState.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                OutlinedTextField(
                    value = state.genreState.genre?.name
                        ?: stringResource(id = R.string.select_genre),
                    onValueChange = { },
                    readOnly = true,
                    isError = state.genreState.error != null,
                    singleLine = true,
                    label = {
                        if (state.genreState.genre != null) {
                            Text(text = stringResource(id = R.string.select_genre_label))
                        }
                    },
                    supportingText = {
                        if (state.genreState.error != null) {
                            Text(
                                text = stringResource(R.string.genre_empty_error),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                            contentDescription = stringResource(R.string.select_genre),
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) {
                                        onIntent(NewBookContract.Intent.SelectGenre)
                                    }
                                }
                            }
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}