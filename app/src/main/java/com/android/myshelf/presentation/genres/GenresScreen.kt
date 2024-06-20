package com.android.myshelf.presentation.genres

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.myshelf.R
import com.android.myshelf.data.local.dbos.Genre
import com.android.myshelf.presentation.shared.components.ModalDrawerSheetView
import com.android.myshelf.presentation.shared.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun GenresScreen(
    navController: NavController,
) {
    val viewModel: GenresViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.labels.onEach { label ->
            when (label) {
                GenresContract.Label.AddGenre -> navController.navigate(Screen.NewGenre)
                is GenresContract.Label.OpenGenre -> navController.navigate(Screen.GenreBooks(label.genre))
                is GenresContract.Label.Navigate -> {
                    navController.navigate(label.screen) {
                        popUpTo(label.screen) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
            .launchIn(this)
    }

    GenresScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenresScreen(
    state: GenresContract.State,
    onIntent: (GenresContract.Intent) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        drawerState.close()
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheetView(
                currentScreen = Screen.Genres,
                drawerState = drawerState,
                onNavigationItemClick = { onIntent(GenresContract.Intent.Navigate(it)) }
            )
        },
        drawerState = drawerState
    ) {
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.genres),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu),
                            )
                        }
                    },
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
                    items(state.genres) { genre ->
                        GenreItemView(
                            onIntent = onIntent,
                            genre = genre
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreItemView(
    onIntent: (GenresContract.Intent) -> Unit,
    genre: Genre,
) {
    ListItem(
        headlineContent = {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Filled.Category,
                contentDescription = stringResource(R.string.genre),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIntent(GenresContract.Intent.OpenGenre(genre)) },
    )
}

@Composable
private fun FloatingActionButtonView(
    onIntent: (GenresContract.Intent) -> Unit,
) {
    FloatingActionButton(
        onClick = { onIntent(GenresContract.Intent.AddGenre) },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_genre),
        )
    }
}