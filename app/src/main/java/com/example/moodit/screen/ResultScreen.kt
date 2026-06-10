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

@Composable
fun ResultScreen(
    navController: NavController,
    category: String,
    amount: String,
    reason: String,
    memo: String
) {
    MooditTheme {
        val isDark = isSystemInDarkTheme()
        val locationStr = SharedLocationHolder.location

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

    // AI 결과
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
                "만족감과 동기부여를 중요하게 생각하는 \n 소비 성향이에요."

            keywords = listOf(
                "#자기보상",
                "#동기부여",
                "#만족소비"
            )

            cardColor = if (isDark) Color(0xFF7A68A1) else Color(0xFFF5EEFF)
        }

        "스트레스 해소" -> {

            resultType = "감정해소형 소비"

            description =
                "감정 해소와 스트레스 완화를 위해 \n 소비하는 소비 성향이에요."

            keywords = listOf(
                "#감정소비",
                "#소확행",
                "#기분전환"
            )

            cardColor = if (isDark) Color(0xFF8A70B3) else Color(0xFFF8EEFF)
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

            cardColor = if (isDark) Color(0xFFB26A8A) else Color(0xFFFFF2F7)
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

            cardColor = if (isDark) Color(0xFF6FA27A) else Color(0xFFEFFAF3)
        }
    }

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    LaunchedEffect(decodedCategory, decodedAmount, decodedReason, resultType, description) {
        dataStoreManager.saveRecentAnalysis(
            resultType = resultType,
            description = description,
            category = decodedCategory,
            amount = decodedAmount,
            reason = decodedReason
        )
    }

    LaunchedEffect(Unit) {

        try {

            val prompt = """
            당신은 소비 심리 분석 AI입니다.

            사용자 정보

            카테고리: $decodedCategory
            소비 금액: $decodedAmount
            소비 이유: $decodedReason
            메모: $decodedMemo

            반드시 자연스럽게 읽히는 한국어로만 답변하세요.
            한자와 일본어는 사용을 절대 금지하며, 필요한 고유명사 외에는 영어 사용도 금지합니다.
            특히 [소비 유형]은 반드시 '필요형 소비자', '자기보상형 소비자'와 같은 한국어 명사 형태로만 생성해야 하며, '必要 소비자'처럼 한자가 포함되어서는 절대 안 됩니다.

            관광, 음식, 여행 등 입력되지 않은 내용을 절대 추측하지 마세요.

            다음 형식을 정확히 지키세요.

            [소비 유형]
            20자 이내

            [AI 분석]
            2문장 이내

            [한 줄 조언]
            1문장

            예시)

            [소비 유형]
            자기보상형 소비자

            [AI 분석]
            스트레스를 해소하기 위한 감정 소비 경향이 보입니다.
            비교적 큰 금액을 사용해 만족감을 얻으려는 모습이 나타납니다.

            [한 줄 조언]
            기분 소비 전에 예산을 먼저 정해보세요.
            """.trimIndent()

            val client = OkHttpClient()

            val json = JSONObject().apply {

                put(
                    "model",
                    "llama-3.3-70b-versatile"
                )

                put(
                    "messages",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        }
                    )
                )

                put("temperature", 0.7)
            }

            val requestBody =
                json.toString().toRequestBody(
                    "application/json".toMediaType()
                )

            val request =
                Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader(
                        "Authorization",
                        "Bearer ${BuildConfig.GROQ_API_KEY}"
                    )
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .post(requestBody)
                    .build()

            val body = withContext(Dispatchers.IO) {

                val response =
                    client.newCall(request).execute()

                println("HTTP CODE = ${response.code}")

                response.body?.string()
            }

            val result =
                JSONObject(body!!)

            val rawReport =
                result
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

            aiReport =
                "오류 발생\n${e.javaClass.simpleName}"

            println("ERROR TYPE = ${e.javaClass.simpleName}")
            println("ERROR MESSAGE = ${e.message}")

            e.printStackTrace()
        }
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
                onClick = {
                    navController.popBackStack()
                }
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

            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
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

            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
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

        // 키워드 카드
        Card(
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp),

            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
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
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                )
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

            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "카테고리 : $decodedCategory",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "소비 금액 : $decodedAmount",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "소비 이유 : $decodedReason",
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
        if (decodedMemo.isNotBlank()) {

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(22.dp),

                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = decodedMemo,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = if (isDark) Color.LightGray else Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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