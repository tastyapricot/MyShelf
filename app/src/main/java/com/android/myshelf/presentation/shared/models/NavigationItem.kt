package com.android.myshelf.presentation.shared.models

import androidx.compose.ui.graphics.vector.ImageVector
import com.android.myshelf.presentation.shared.navigation.Screen

data class NavigationItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)