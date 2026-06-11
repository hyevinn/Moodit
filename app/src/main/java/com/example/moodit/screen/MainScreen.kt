package com.example.moodit.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import com.example.moodit.data.DataStoreManager
import com.example.moodit.data.DEFAULT_ANALYSIS
import androidx.navigation.NavController
import com.example.moodit.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.PlatformTextStyle

@Composable
fun MainScreen(navController: NavController) {

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val recentAnalysisState by dataStoreManager.recentAnalysisFlow.collectAsState(initial = null)
    val recentAnalysis = recentAnalysisState ?: DEFAULT_ANALYSIS
    val insightState by dataStoreManager.insightFlow.collectAsState(initial = null)
    val insight = insightState ?: "오늘의 소비에는 오늘의 감정이 담겨 있어요."
    val displayInsight = if (insight.startsWith("\"") && insight.endsWith("\"")) {
        insight
    } else {
        "\"$insight\""
    }

    val emoji = when (recentAnalysis.reason) {
        "자기만족" -> "🎁"
        "스트레스 해소" -> "💨"
        "유행 영향" -> "✨"
        else -> "✔"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // 스크롤 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // 로고
            Image(
                painter = painterResource(id = R.drawable.moodit_logo),
                contentDescription = null,

                modifier = Modifier
                    .width(145.dp)
                    .height(52.dp)
            )

            // 인사 문구
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "안녕하세요 👋",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                style = LocalTextStyle.current.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "오늘도 현명한 소비 습관을 만들어봐요",
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Image(
                    painter = painterResource(id = R.drawable.today_character),
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .offset(y = (-4).dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 오늘의 한 줄 카드
            Card(
                shape = RoundedCornerShape(24.dp),

                border = BorderStroke(
                    1.5.dp,
                    Color(0xFFD6BEFF)
                ),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5EEFF)
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {

                    Text(
                        text = "✨ 최근 소비 인사이트",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = displayInsight,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            }

            // 최근 분석 결과
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "최근 분석 결과",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 최근 분석 카드
            Card(
                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(
                    1.dp,
                    Color(0xFFD6BEFF)
                ),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F3FF)
                ),

                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Card(
                        modifier = Modifier.size(84.dp),   // 크기 고정
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color(0xFFD6BEFF)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 34.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Column {

                        Text(
                            text = "주요 소비 유형",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = recentAnalysis.resultType,

                            fontSize = 24.sp,

                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = recentAnalysis.description,

                            fontSize = 14.sp,

                            color = Color.DarkGray
                        )
                    }
                }
            }

            // 최근 소비 정보 카드
            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(20.dp),

                border = BorderStroke(
                    1.dp,
                    Color(0xFFD6BEFF)
                ),

                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F3FF)
                ),

                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "최근 선택한 소비 정보",

                        fontSize = 18.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 카테고리
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "🛍  카테고리",
                            fontSize = 17.sp
                        )

                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFEDE2FF),
                                contentColor = Color(0xFF8E5DFF)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFB388FF))
                        ) {

                            Text(recentAnalysis.category)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 금액대
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "💰  금액대",
                            fontSize = 17.sp
                        )

                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFEDE2FF),
                                contentColor = Color(0xFF8E5DFF)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFB388FF))
                        ) {

                            Text(recentAnalysis.amount)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 소비 이유
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "❓  소비 이유",
                            fontSize = 17.sp
                        )

                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFEDE2FF),
                                contentColor = Color(0xFF8E5DFF)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFB388FF))
                        ) {

                            Text(recentAnalysis.reason)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 하단 고정 버튼
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.96f else 1f,
            label = "scale"
        )
        val baseColor = Color(0xFFC8AFFF)
        val buttonColor = if (isPressed) lerp(baseColor, Color.Black, 0.15f) else baseColor

        Button(
            onClick = {

                navController.navigate("input")
            },

            interactionSource = interactionSource,

            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
                .height(62.dp),

            shape = RoundedCornerShape(18.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            )
        ) {

            Text(
                text = "새 소비 분석하기",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}