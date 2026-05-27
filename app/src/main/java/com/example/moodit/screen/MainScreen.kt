package com.example.moodit.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodit.R

@Composable
fun MainScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4FA))
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
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "오늘도 현명한 소비 습관을 만들어봐요",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    .height(140.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp),

                    verticalAlignment = Alignment.Top
                ) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {

                        Text(
                            text = "오늘의 한 줄",
                            color = Color(0xFF8F7AAE),
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "\"오늘의 소비에는\n오늘의 감정이 담겨 있어요.\"",

                            fontSize = 17.sp,

                            lineHeight = 24.sp,

                            fontWeight = FontWeight.Bold,

                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // 캐릭터 이미지
                    Image(
                        painter = painterResource(
                            id = R.drawable.today_character
                        ),

                        contentDescription = null,

                        modifier = Modifier
                            .size(90.dp)
                            .offset(y = 6.dp),

                        contentScale = ContentScale.Fit
                    )
                }
            }

            // 최근 분석 결과
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "최근 분석 결과",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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
                        shape = RoundedCornerShape(50.dp),

                        border = BorderStroke(
                            1.dp,
                            Color(0xFFD6BEFF)
                        ),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Text(
                            text = "🎁",

                            modifier = Modifier.padding(16.dp),

                            fontSize = 34.sp
                        )
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
                            text = "자기보상형 소비",

                            fontSize = 24.sp,

                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "만족감과 동기부여를 중요하게\n생각하는 소비 성향이에요.",

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
                            onClick = { }
                        ) {

                            Text("쇼핑")
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
                            onClick = { }
                        ) {

                            Text("1~5만원")
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
                            onClick = { }
                        ) {

                            Text("자기만족")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 하단 고정 버튼
        Button(
            onClick = {

                navController.navigate("input")
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
                .height(62.dp),

            shape = RoundedCornerShape(18.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC8AFFF)
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