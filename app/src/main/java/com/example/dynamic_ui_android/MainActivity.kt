package com.example.dynamic_ui_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dynamic_ui_android.designsystem.DynamicUiStudioTheme
import com.example.dynamic_ui_android.feature.renderer.RendererScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DynamicUiStudioTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "renderer"
                ) {
                    composable("renderer") {
                        RendererScreen()
                    }
                }
            }
        }
    }
}
