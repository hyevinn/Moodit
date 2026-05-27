package com.example.moodit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.moodit.screen.InputScreen
import com.example.moodit.screen.LoadingScreen
import com.example.moodit.screen.MainScreen
import com.example.moodit.screen.ResultScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

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
                composable(
                    "loading/{category}/{amount}/{reason}/{memo}"
                ) {

                    LoadingScreen(
                        navController = navController,

                        category =
                            it.arguments?.getString("category") ?: "",

                        amount =
                            it.arguments?.getString("amount") ?: "",

                        reason =
                            it.arguments?.getString("reason") ?: "",

                        memo =
                            it.arguments?.getString("memo") ?: ""
                    )
                }

                // 결과 화면
                composable(
                    "result/{category}/{amount}/{reason}/{memo}"
                ) {

                    ResultScreen(

                        navController = navController,

                        category =
                            it.arguments?.getString("category") ?: "",

                        amount =
                            it.arguments?.getString("amount") ?: "",

                        reason =
                            it.arguments?.getString("reason") ?: "",

                        memo =
                            it.arguments?.getString("memo") ?: ""
                    )
                }
            }
        }
    }
}