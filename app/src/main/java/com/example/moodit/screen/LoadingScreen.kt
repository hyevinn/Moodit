package com.example.moodit.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodit.R
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoadingScreen(
    navController: NavController,
    category: String,
    amount: String,
    reason: String,
    memo: String
) {

    // 캐릭터 애니메이션
    val infiniteTransition = rememberInfiniteTransition(
        label = "character_animation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,

        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "character_scale"
    )

    LaunchedEffect(Unit) {

        // route 안전 처리
        val encodedCategory = URLEncoder.encode(
            category,
            StandardCharsets.UTF_8.toString()
        )

        val encodedAmount = URLEncoder.encode(
            amount,
            StandardCharsets.UTF_8.toString()
        )

        val encodedReason = URLEncoder.encode(
            reason,
            StandardCharsets.UTF_8.toString()
        )

        val encodedMemo =
            if (memo.isBlank()) {
                "empty"
            } else {
                URLEncoder.encode(
                    memo,
                    StandardCharsets.UTF_8.toString()
                )
            }

        delay(2500)

        navController.navigate(
            "result/$encodedCategory/$encodedAmount/$encodedReason/$encodedMemo"
        ) {

            popUpTo(
                "loading/{category}/{amount}/{reason}/{memo}"
            ) {
                inclusive = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4FA)),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.today_character
            ),

            contentDescription = null,

            modifier = Modifier
                .size(130.dp)
                .scale(scale)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "AI가 소비 패턴을 분석중이에요...",

            fontSize = 20.sp,

            fontWeight = FontWeight.Bold,

            color = Color(0xFF6E54B5)
        )

        Spacer(modifier = Modifier.height(18.dp))

        CircularProgressIndicator(
            color = Color(0xFFC8AFFF)
        )
    }
}