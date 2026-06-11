package com.example.moodit.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodit.R
import com.example.moodit.ui.theme.MooditTheme
import kotlinx.coroutines.delay

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun SplashScreen(navController: NavController) {
    MooditTheme {
        val isDark = isSystemInDarkTheme()

        // Float animation for mascot character
        val infiniteTransition = rememberInfiniteTransition(label = "floating_animation")
        val floatOffset by infiniteTransition.animateFloat(
            initialValue = -12f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1300,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "float_offset"
        )

        // Dynamically scale and fade the shadow beneath the mascot
        val shadowScale = 0.85f + (floatOffset + 12f) / 24f * 0.3f
        val shadowAlpha = 0.1f + (floatOffset + 12f) / 24f * 0.15f

        // Transition to the main screen after exactly 1.0 second (1000ms)
        LaunchedEffect(Unit) {
            delay(1000)
            navController.navigate("main") {
                popUpTo("splash") {
                    inclusive = true
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top spacer
                Spacer(modifier = Modifier.weight(1.2f))

                // Mascot & Shadow
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.today_character),
                        contentDescription = "Moodit Mascot",
                        modifier = Modifier
                            .size(150.dp)
                            .offset(y = floatOffset.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Soft organic shadow matching floating movement and theme background
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(10.dp)
                            .graphicsLayer(
                                scaleX = shadowScale,
                                scaleY = shadowScale
                            )
                            .background(
                                color = if (isDark) {
                                    Color(0xFF0C0A10).copy(alpha = shadowAlpha)
                                } else {
                                    Color(0xFF5E4E80).copy(alpha = shadowAlpha)
                                },
                                shape = CircleShape
                            )
                    )
                }

                // Middle spacer
                Spacer(modifier = Modifier.weight(0.9f))

                // Logo, Slogan (without progress bar)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.moodit_logo),
                        contentDescription = "Moodit Logo",
                        modifier = Modifier
                            .width(145.dp)
                            .height(52.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "오늘의 소비에 담긴 나의 마음",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                // Bottom spacer
                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}
