package com.example.moodit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.moodit.screen.InputScreen
import com.example.moodit.screen.LoadingScreen
import com.example.moodit.screen.MainScreen
import com.example.moodit.screen.ResultScreen
import com.example.moodit.ui.theme.MooditTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {

            MooditTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {

                // 메인 화면
                composable("main") {

                    MainScreen(navController)
                }

                // 소비 입력 화면
                composable("input") {

                    InputScreen(navController)
                }

                // 로딩 화면
                composable("loading") {
                    LoadingScreen(navController = navController)
                }

                // 결과 화면
                composable("result") {
                    ResultScreen(navController = navController)
                }
            }
        }
    }
}
}