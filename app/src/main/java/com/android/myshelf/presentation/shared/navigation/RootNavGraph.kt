package com.android.myshelf.presentation.shared.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.myshelf.presentation.edit_book.EditBookScreen
import com.android.myshelf.presentation.edit_genre.EditGenreScreen
import com.android.myshelf.presentation.genre.GenreScreen
import com.android.myshelf.presentation.genres.GenresScreen
import com.android.myshelf.presentation.home.HomeScreen
import com.android.myshelf.presentation.new_book.NewBookScreen
import com.android.myshelf.presentation.new_genre.NewGenreScreen
import com.android.myshelf.presentation.search.SearchScreen
import com.android.myshelf.presentation.select_genre.SelectGenreScreen

@Composable
fun RootNavGraph(
    navHostController: NavHostController,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
        popEnterTransition = {
            EnterTransition.None
        },
        popExitTransition = {
            ExitTransition.None
        }
    ) {
        composable<Screen.Home> {
            HomeScreen(navHostController)
        }
        composable<Screen.Search> {
            SearchScreen(navHostController)
        }
        composable<Screen.Genres> {
            GenresScreen(navHostController)
        }
        composable<Screen.GenreBooks>(
            typeMap = Screen.GenreBooks.typeMap
        ) {
            GenreScreen(navHostController)
        }
        composable<Screen.NewGenre> {
            NewGenreScreen(navHostController)
        }
        composable<Screen.EditGenre>(
            typeMap = Screen.EditGenre.typeMap
        ) {
            EditGenreScreen(navHostController)
        }
        composable<Screen.NewBook>(
            typeMap = Screen.NewBook.typeMap
        ) {
            NewBookScreen(navHostController, it)
        }
        composable<Screen.EditBook>(
            typeMap = Screen.EditBook.typeMap
        ) {
            EditBookScreen(navHostController, it)
        }
        composable<Screen.SelectGenre> {
            SelectGenreScreen(navHostController)
        }
    }
}
