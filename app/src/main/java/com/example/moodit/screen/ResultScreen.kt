package com.example.moodit.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodit.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun ResultScreen(
    navController: NavController,
    category: String,
    amount: String,
    reason: String,
    memo: String
) {

    val decodedCategory = URLDecoder.decode(
        category,
        StandardCharsets.UTF_8.toString()
    )

    val decodedAmount = URLDecoder.decode(
        amount,
        StandardCharsets.UTF_8.toString()
    )

    val decodedReason = URLDecoder.decode(
        reason,
        StandardCharsets.UTF_8.toString()
    )

    val decodedMemo =
        if (memo == "empty") {
            ""
        } else {
            URLDecoder.decode(
                memo,
                StandardCharsets.UTF_8.toString()
            )
        }

    // Gemini AI 결과
    var aiReport by remember {
        mutableStateOf("AI가 소비 패턴을 분석중이에요...")
    }

    // 소비 유형 카드용 변수
    val resultType: String
    val description: String
    val keywords: List<String>
    val cardColor: Color

    when (decodedReason) {

        "자기만족" -> {

            resultType = "자기보상형 소비"

            description =
                "만족감과 동기부여를 중요하게 생각하는 소비 성향이에요."

            keywords = listOf(
                "#자기보상",
                "#동기부여",
                "#만족소비"
            )

            cardColor = Color(0xFFF5EEFF)
        }

        "스트레스 해소" -> {

            resultType = "감정해소형 소비"

            description =
                "감정 해소와 스트레스 완화를 위해 \n 소비하는 소비 성향이에요."

            keywords = listOf(
                "#감정소비",
                "#스트레스해소",
                "#기분전환"
            )

            cardColor = Color(0xFFF8EEFF)
        }

        "유행 영향" -> {

            resultType = "트렌드추종형 소비"

            description =
                "유행과 분위기에 영향을 받는 소비 성향이에요."

            keywords = listOf(
                "#유행소비",
                "#트렌드",
                "#추천템"
            )

            cardColor = Color(0xFFFFF2F7)
        }

        else -> {

            resultType = "실용중심형 소비"

            description =
                "필요성과 효율성을 우선적으로 고려하는\n소비 성향이에요."

            keywords = listOf(
                "#실용소비",
                "#가성비",
                "#효율중심"
            )

            cardColor = Color(0xFFEFFAF3)
        }
    }

    // Gemini API 호출
    LaunchedEffect(Unit) {

        try {

            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )

            val prompt = """
                사용자의 소비 패턴을 분석해주세요.

                카테고리: $decodedCategory
                소비 금액: $decodedAmount
                소비 이유: $decodedReason
                메모: $decodedMemo

                너무 길지 않게 5~6줄 정도로 자연스럽게 분석해주세요.
            """.trimIndent()

            val response = generativeModel.generateContent(
                prompt
            )

            aiReport =
                response.text ?: "분석 결과를 불러오지 못했어요."

        } catch (e: Exception) {

            aiReport =
                e.message ?: "AI 분석 오류"

            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Spacer(modifier = Modifier.height(18.dp))

        // 상단 바
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "소비 분석 결과",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 결과 카드
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp),

            border = BorderStroke(
                1.dp,
                Color(0xFFC8AFFF)
            ),

            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "당신의 소비 유형은",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = resultType,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // AI 리포트 카드
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp),

            border = BorderStroke(
                1.dp,
                Color(0xFFC8AFFF)
            ),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "AI 소비 분석 리포트",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = aiReport,
                    fontSize = 15.sp,
                    lineHeight = 25.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 키워드 카드
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp),

            border = BorderStroke(
                1.dp,
                Color(0xFFC8AFFF)
            ),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "소비 성향 키워드",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    keywords.forEach { keyword ->

                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFF1E6FF),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                )
                        ) {

                            Text(
                                text = keyword,
                                color = Color(0xFF7E57C2),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 사용자 입력 정보 카드
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp),

            border = BorderStroke(
                1.dp,
                Color(0xFFC8AFFF)
            ),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "입력한 소비 정보",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "카테고리 : $decodedCategory",
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "소비 금액 : $decodedAmount",
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "소비 이유 : $decodedReason",
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // 메모 카드
        if (decodedMemo.isNotBlank()) {

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(22.dp),

                border = BorderStroke(
                    1.dp,
                    Color(0xFFC8AFFF)
                ),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "오늘의 소비 한마디",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = decodedMemo,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}