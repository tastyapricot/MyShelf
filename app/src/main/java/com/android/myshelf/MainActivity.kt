package com.android.myshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.android.myshelf.presentation.shared.navigation.RootNavGraph
import com.android.myshelf.presentation.shared.utils.isLight
import com.android.myshelf.presentation.ui.theme.MyShelfTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyShelfTheme {
                val systemUiController = rememberSystemUiController()

                val statusBarContainerColor = Color.Transparent
                val isDarkIcons = MaterialTheme.colorScheme.isLight()

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = statusBarContainerColor,
                        darkIcons = isDarkIcons
                    )
                }

                val navHostController = rememberNavController()
                RootNavGraph(
                    navHostController = navHostController,
                )
            }
        }
    }
}