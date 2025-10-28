package com.gagandeep.dynamicuistudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gagandeep.dynamicuistudio.designsystem.DynamicUiStudioTheme
import com.gagandeep.dynamicuistudio.feature.renderer.RendererScreen

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
