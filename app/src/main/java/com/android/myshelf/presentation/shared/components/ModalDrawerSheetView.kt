package com.android.myshelf.presentation.shared.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.myshelf.R
import com.android.myshelf.presentation.shared.models.NavigationItem
import com.android.myshelf.presentation.shared.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun ModalDrawerSheetView(
    currentScreen: Screen,
    drawerState: DrawerState,
    onNavigationItemClick: (Screen) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val navigationItems = listOf(
        NavigationItem(
            screen = Screen.Home,
            title = stringResource(id = R.string.books),
            icon = Icons.Outlined.Book,
            selectedIcon = Icons.Filled.Book
        ),
        NavigationItem(
            screen = Screen.Genres,
            title = stringResource(id = R.string.genres),
            icon = Icons.Outlined.Category,
            selectedIcon = Icons.Filled.Category
        ),
    )

    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))

        navigationItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(text = item.title) },
                selected = item.screen == currentScreen,
                onClick = {
                    scope.launch {
                        drawerState.close()
                        if (item.screen != currentScreen) {
                            onNavigationItemClick(item.screen)
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (item.screen == currentScreen) item.selectedIcon else item.icon,
                        contentDescription = item.title
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}