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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import com.example.moodit.data.DataStoreManager
import com.example.moodit.ui.theme.MooditTheme
import com.example.moodit.ui.theme.SharedLocationHolder
import com.example.moodit.BuildConfig
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.style.TextOverflow
import com.example.moodit.model.SharedConsumptionHolder
import com.example.moodit.model.ConsumptionData

@Composable
fun ResultScreen(
    navController: NavController
) {
    MooditTheme {
        val isDark = isSystemInDarkTheme()
        val locationStr = SharedLocationHolder.location

        // 다건 소비 목록 연동
        val consumptionList = remember { SharedConsumptionHolder.list }
        
        val totalAmount = consumptionList.sumOf { it.amount.toLongOrNull() ?: 0L }
        val totalAmountFormatted = java.text.DecimalFormat("#,###").format(totalAmount)

        // 가장 빈번한 카테고리
        val mainCategory = if (consumptionList.isNotEmpty()) {
            consumptionList.groupBy { it.category }
                .maxByOrNull { it.value.size }?.key ?: "기타"
        } else {
            "기타"
        }

        // 가장 빈번한 이유
        val mainReason = if (consumptionList.isNotEmpty()) {
            consumptionList.groupBy { it.reason }
                .maxByOrNull { it.value.size }?.key ?: "필요 소비"
        } else {
            "필요 소비"
        }

        val uniqueCategoriesCount = consumptionList.map { it.category }.distinct().size
        val categoriesText = if (uniqueCategoriesCount > 1) {
            "$mainCategory 외 ${uniqueCategoriesCount - 1}건"
        } else {
            mainCategory
        }

        val combinedMemo = consumptionList.mapNotNull { it.memo.takeIf { m -> m.isNotBlank() } }
            .joinToString(", ")

        // AI 결과
        var aiReport by remember {
            mutableStateOf("AI가 소비 패턴을 분석중이에요...")
        }

        // 소비 유형 카드용 변수
        val resultType: String
        val description: String
        val keywords: List<String>
        val cardColor: Color

        when (mainReason) {
            "자기만족" -> {
                resultType = "자기보상형 소비"
                description = "만족감과 동기부여를 중요하게 생각하는 \n 소비 성향이에요."
                keywords = listOf("#자기보상", "#동기부여", "#만족소비")
                cardColor = if (isDark) Color(0xFF7A68A1) else Color(0xFFF5EEFF)
            }

            "스트레스 해소" -> {
                resultType = "감정해소형 소비"
                description = "감정 해소와 스트레스 완화를 위해 \n 소비하는 소비 성향이에요."
                // 요구사항에 맞춰 키워드 변경: #감정소비, #기분환기, #기분전환
                keywords = listOf("#감정소비", "#기분환기", "#기분전환")
                cardColor = if (isDark) Color(0xFF8A70B3) else Color(0xFFF8EEFF)
            }

            "유행 영향" -> {
                resultType = "트렌드추종형 소비"
                description = "유행과 분위기에 영향을 받는 소비 성향이에요."
                keywords = listOf("#유행소비", "#트렌드", "#추천템")
                cardColor = if (isDark) Color(0xFFB26A8A) else Color(0xFFFFF2F7)
            }

            else -> {
                resultType = "실용중심형 소비"
                description = "필요성과 효율성을 우선적으로 고려하는\n소비 성향이에요."
                keywords = listOf("#실용소비", "#가성비", "#효율중심")
                cardColor = if (isDark) Color(0xFF6FA27A) else Color(0xFFEFFAF3)
            }
        }

        val context = LocalContext.current
        val dataStoreManager = remember { DataStoreManager(context) }

        LaunchedEffect(mainCategory, totalAmount, mainReason, resultType, description) {
            dataStoreManager.saveRecentAnalysis(
                resultType = resultType,
                description = description,
                category = categoriesText,
                amount = "${totalAmountFormatted}원",
                reason = mainReason
            )
        }

        LaunchedEffect(Unit) {
            try {
                val itemsText = consumptionList.joinToString("\n") { item ->
                    "- 카테고리: ${item.category}, 금액: ${item.amount}원, 이유: ${item.reason}, 메모: ${item.memo}"
                }

                val prompt = """
                당신은 소비 심리 분석 AI입니다.

                사용자의 오늘의 소비 내역 목록은 다음과 같습니다:
                $itemsText

                총 소비 금액: ${totalAmountFormatted}원
                소비 빈도 (총 건수): ${consumptionList.size}건

                반드시 자연스럽게 읽히는 한국어로만 답변하세요.
                한자와 일본어는 사용을 절대 금지하며, 필요한 고유명사 외에는 영어 사용도 금지합니다.
                특히 [소비 유형]은 반드시 '필요형 소비자', '자기보상형 소비자'와 같은 한국어 명사 형태로만 생성해야 하며, '必要 소비자'처럼 한자가 포함되어서는 절대 안 됩니다.
                [AI 분석] 문장에 한국어 외 한자, 일본어, 영어 사용 금지합니다.
                
                관광, 음식, 여행 등 입력되지 않은 내용을 절대 추측하지 마세요.

                다음 형식을 정확히 지키세요.

                [소비 유형]
                20자 이내

                [AI 분석]
                2문장 이내

                [한 줄 조언]
                다음 조건들을 반드시 준수하여 딱 1문장으로만 작성하세요:
                - 사용자가 바로 이해할 수 있는 자연스러운 한국어로 작성합니다.
                - 조언을 나열하기보다 하나의 유기적인 흐름으로 이어지는 문장을 작성합니다.
                - 단어 중간에서 줄바꿈이 되어도 어색하지 않도록, 공백 포함 문장 길이를 반드시 40~60자 내외로 유지합니다.
                - '~노력해요', '~하도록 하세요'와 같이 딱딱한 표현 대신, '~해 보세요', '~이어가 보세요', '~유지해 보세요', '~어떨까요?'와 같이 부드럽고 친근한 표현을 사용합니다.
                - 사용자의 최근 지출 내역 및 소비 유형에 맞는 실질적인 조언을 반영하여 개인화된 지출 통찰력처럼 느껴지게 작성합니다.
                - 지나치게 일반적인 잔소리나 뻔하고 반복적인 표현은 피합니다.

                예시)

                [소비 유형]
                자기보상형 소비자

                [AI 분석]
                스트레스를 해소하기 위한 감정 소비 경향이 보입니다.
                비교적 큰 금액을 사용해 만족감을 얻으려는 모습이 나타납니다.

                [한 줄 조언]
                자신을 위한 소비는 좋지만, 예산 범위를 정해두면 더 현명하게 즐길 수 있어요.
                """.trimIndent()

                val client = OkHttpClient()

                val json = JSONObject().apply {
                    put("model", "llama-3.3-70b-versatile")
                    put("messages", JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        }
                    ))
                    put("temperature", 0.7)
                }

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val body = withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    println("HTTP CODE = ${response.code}")
                    response.body?.string()
                }

                val result = JSONObject(body!!)
                val rawReport = result
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                aiReport = rawReport

                val insight = extractInsight(rawReport)
                if (insight.isNotEmpty()) {
                    dataStoreManager.saveInsight(insight)
                }

            } catch (e: Exception) {
                aiReport = "오류 발생\n${e.javaClass.simpleName}"
                println("ERROR TYPE = ${e.javaClass.simpleName}")
                println("ERROR MESSAGE = ${e.message}")
                e.printStackTrace()
            }
        }

        // 차트 컬러 매핑
        val categoryColors = remember {
            mapOf(
                "식비" to Color(0xFFEF9A9A),
                "카페" to Color(0xFFFFE082),
                "교통" to Color(0xFF90CAF9),
                "쇼핑" to Color(0xFF8A70B3),
                "취미" to Color(0xFFA5D6A7),
                "자기계발" to Color(0xFFB39DDB),
                "기타" to Color(0xFFB0BEC5)
            )
        }

        // 카테고리별 누적 금액 계산
        val categoryAmounts = remember(consumptionList) {
            consumptionList.groupBy { it.category }
                .mapValues { (_, list) -> list.sumOf { it.amount.toLongOrNull() ?: 0L } }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "소비 분석 결과",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 결과 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = cardColor)
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
                        color = if (isDark) Color.LightGray else Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = resultType,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (isDark) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = if (isDark) Color(0xFFE0E0E0) else Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // AI 리포트 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = aiReport,
                        fontSize = 15.sp,
                        lineHeight = 25.sp,
                        color = if (isDark) Color.LightGray else Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 소비 시각화 카드 (파이 차트 및 막대 그래프)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "소비 분석 시각화",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "📊 카테고리별 소비 비율",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 커스텀 파이 차트
                    PieChart(
                        categoryAmounts = categoryAmounts,
                        categoryColors = categoryColors
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "📈 카테고리별 소비 금액",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 커스텀 막대 차트
                    BarChart(
                        categoryAmounts = categoryAmounts,
                        categoryColors = categoryColors
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 키워드 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        keywords.forEach { keyword ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isDark) Color(0xFF3B2A56) else Color(0xFFF1E6FF),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                  Text(
                                    text = keyword,
                                    color = if (isDark) Color(0xFFC8AFFF) else Color(0xFF7E57C2),
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
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "입력한 소비 정보 요약",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "주요 카테고리 : $categoriesText",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "총 소비 금액 : ${totalAmountFormatted}원",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "주요 소비 이유 : $mainReason",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "소비 위치 : $locationStr",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 메모 카드
            if (combinedMemo.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "오늘의 소비 한마디 모음",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = combinedMemo,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            color = if (isDark) Color.LightGray else Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val returnInteractionSource = remember { MutableInteractionSource() }
            val isReturnPressed by returnInteractionSource.collectIsPressedAsState()
            val returnScale by animateFloatAsState(
                targetValue = if (isReturnPressed) 0.96f else 1f,
                label = "returnScale"
            )
            val returnBaseColor = MaterialTheme.colorScheme.primary
            val returnButtonColor = if (isReturnPressed) lerp(returnBaseColor, Color.Black, 0.15f) else returnBaseColor

            Button(
                onClick = {
                    navController.navigate("main") {
                        popUpTo("main") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                interactionSource = returnInteractionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(scaleX = returnScale, scaleY = returnScale)
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = returnButtonColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "메인화면으로 돌아가기",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PieChart(
    categoryAmounts: Map<String, Long>,
    categoryColors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    val total = categoryAmounts.values.sum().toFloat()
    if (total == 0f) return

    val proportions = categoryAmounts.mapValues { it.value / total }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Canvas(modifier = Modifier.size(130.dp)) {
            var startAngle = 0f
            proportions.forEach { (category, proportion) ->
                val sweepAngle = proportion * 360f
                val color = categoryColors[category] ?: Color.Gray
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            categoryAmounts.forEach { (category, amount) ->
                val color = categoryColors[category] ?: Color.Gray
                val percent = if (total > 0) (amount.toFloat() / total * 100).toInt() else 0
                val amountFormatted = java.text.DecimalFormat("#,###").format(amount)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = "$category ($percent%) : ${amountFormatted}원",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun BarChart(
    categoryAmounts: Map<String, Long>,
    categoryColors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    val maxAmount = categoryAmounts.values.maxOrNull()?.toFloat() ?: 1f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        categoryAmounts.forEach { (category, amount) ->
            val color = categoryColors[category] ?: Color.Gray
            val barHeightFraction = if (maxAmount > 0) amount.toFloat() / maxAmount else 0f
            val amountFormatted = if (amount >= 10000) {
                "${(amount / 10000.0).let { if (it % 1.0 == 0.0) it.toInt() else it }}만"
            } else {
                java.text.DecimalFormat("#,###").format(amount)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = amountFormatted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(barHeightFraction * 0.75f) // leave space for text
                        .background(color, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun cleanAndSummarize(text: String): String {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return ""
    
    val sentences = trimmed.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
    if (sentences.isEmpty()) return trimmed
    
    val firstSentence = sentences.first()
    if (trimmed.length > 60 && firstSentence.length < trimmed.length) {
        return firstSentence
    }
    
    return trimmed
}

fun extractInsight(report: String): String {
    val lines = report.lines().map { it.trim() }.filter { it.isNotEmpty() }
    
    var rawInsight = ""
    
    val adviceIndex = lines.indexOfFirst { it.contains("조언") }
    if (adviceIndex != -1) {
        if (adviceIndex + 1 < lines.size) {
            val candidate = lines[adviceIndex + 1]
            if (!candidate.startsWith("[")) {
                rawInsight = candidate
            }
        }
        if (rawInsight.isEmpty()) {
            val afterBrackets = lines[adviceIndex].substringAfter("]").trim()
            if (afterBrackets.isNotEmpty()) {
                rawInsight = afterBrackets
            }
        }
    }
    
    if (rawInsight.isEmpty()) {
        val analysisIndex = lines.indexOfFirst { it.contains("분석") }
        if (analysisIndex != -1) {
            if (analysisIndex + 1 < lines.size) {
                val candidate = lines[analysisIndex + 1]
                if (!candidate.startsWith("[")) {
                    rawInsight = candidate
                }
            }
        }
    }
    
    if (rawInsight.isEmpty()) {
        val cleanLines = lines.filter { !it.contains("[") && !it.contains("]") }
        if (cleanLines.isNotEmpty()) {
            rawInsight = cleanLines.last()
        }
    }
    
    return cleanAndSummarize(rawInsight)
}