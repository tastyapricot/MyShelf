package com.android.myshelf.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.myshelf.R
import com.android.myshelf.presentation.shared.components.BookItemView
import com.android.myshelf.presentation.shared.components.ModalDrawerSheetView
import com.android.myshelf.presentation.shared.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navController: NavController,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.labels.onEach { label ->
            when (label) {
                HomeContract.Label.AddBook -> navController.navigate(Screen.NewBook())
                is HomeContract.Label.EditBook -> navController.navigate(Screen.EditBook(label.book))
                HomeContract.Label.OpenSearch -> navController.navigate(Screen.Search)
                is HomeContract.Label.Navigate -> {
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

    HomeScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun HomeScreen(
    state: HomeContract.State,
    onIntent: (HomeContract.Intent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheetView(
                currentScreen = Screen.Home,
                drawerState = drawerState,
                onNavigationItemClick = { onIntent(HomeContract.Intent.Navigate(it)) }
            )
        },
        drawerState = drawerState
    ) {
        val scope = rememberCoroutineScope()

        Scaffold(
            floatingActionButton = {
                FloatingActionButtonView(onIntent)
            },
            floatingActionButtonPosition = FabPosition.End,
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SearchBarView(
                        onIntent = onIntent,
                        onMenuClick = { scope.launch { drawerState.open() } },
                    )

                    LazyColumn {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(id = R.string.books),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .padding(
                                        horizontal = 16.dp,
                                    )
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        items(state.books) { book ->
                            BookItemView(
                                onBookClick = { onIntent(HomeContract.Intent.EditBook(book)) },
                                book = book
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingActionButtonView(
    onIntent: (HomeContract.Intent) -> Unit,
) {
    FloatingActionButton(
        onClick = { onIntent(HomeContract.Intent.AddBook) },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_book),
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchBarView(
    onIntent: (HomeContract.Intent) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBar(
        query = "",
        onQueryChange = { },
        onSearch = { },
        active = false,
        enabled = false,
        onActiveChange = { },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            IconButton(onClick = { onMenuClick() }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp,
                top = 8.dp
            )
            .clickable { onIntent(HomeContract.Intent.OpenSearch) }
            .then(modifier)
    ) {
    }
}