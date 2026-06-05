package com.example.moodit.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.Manifest
import android.widget.Toast
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.moodit.ui.theme.MooditTheme
import com.example.moodit.ui.theme.SharedLocationHolder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputScreen(navController: NavController) {

    val isDark = isSystemInDarkTheme()
    println("DARK MODE = $isDark")

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ -> }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "consumption_channel"
            val channelName = "소비 알림"
            val channelDescription = "소비 패턴 분석 및 알림을 제공합니다."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val neededPermissions = mutableListOf<String>()

        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocation) {
            neededPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotification = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasNotification) {
                neededPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (neededPermissions.isNotEmpty()) {
            permissionsLauncher.launch(neededPermissions.toTypedArray())
        }
    }

    var selectedCategory by remember {
        mutableStateOf("쇼핑")
    }

    var selectedAmount by remember {
        mutableStateOf("1~5만원")
    }

    var selectedReason by remember {
        mutableStateOf("자기만족")
    }

    var memo by remember {
        mutableStateOf("")
    }

    val categories = listOf(
        "쇼핑",
        "식비",
        "카페",
        "교통",
        "취미",
        "자기계발",
        "기타"
    )

    val amounts = listOf(
        "1만원 이하",
        "1~5만원",
        "5~10만원",
        "10만원 이상"
    )

    val reasons = listOf(
        "자기만족",
        "스트레스 해소",
        "유행 영향",
        "필요 소비"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
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
                        text = "소비 기록하기",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 소비 카테고리
            Text(
                text = "소비 카테고리",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                categories.forEach { category ->

                    Box(
                        modifier = Modifier
                            .clickable {
                                selectedCategory = category
                            }
                            .background(
                                color = if (selectedCategory == category) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(
                                horizontal = 18.dp,
                                vertical = 10.dp
                            )
                    ) {

                        Text(
                            text = category,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedCategory == category) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 소비 금액
            Text(
                text = "소비 금액",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                amounts.forEach { amount ->

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedAmount = amount
                            }
                            .background(
                                color = if (selectedAmount == amount) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(vertical = 14.dp),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = amount,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selectedAmount == amount) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 소비 이유
            Text(
                text = "소비 이유",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                reasons.forEach { reason ->

                    val emoji = when (reason) {
                        "자기만족" -> "🙂"
                        "스트레스 해소" -> "💨"
                        "유행 영향" -> "✨"
                        else -> "✔"
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clickable {
                                selectedReason = reason
                            }
                            .background(
                                color = if (selectedReason == reason) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = emoji,
                                fontSize = 28.sp
                            )

                            Text(
                                text = reason,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Center,
                                color = if (selectedReason == reason) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 메모
            Text(
                text = "오늘 소비 기분 한마디 (선택)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = memo,

                onValueChange = {
                    if (it.length <= 100) {
                        memo = it
                    }
                },

                modifier = Modifier.fillMaxWidth(),

                placeholder = {
                    Text(
                        text = "메모를 입력해주세요 (선택)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },

                shape = RoundedCornerShape(16.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),

                trailingIcon = {
                    Box(
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = "${memo.length}/100",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        // 하단 버튼
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.96f else 1f,
            label = "scale"
        )
        val baseColor = MaterialTheme.colorScheme.primary
        val buttonColor = if (isPressed) lerp(baseColor, Color.Black, 0.15f) else baseColor

        Button(
            onClick = {

                checkAndSendNotifications(context, selectedAmount, selectedReason)

                checkLocationAndSendFeedback(context, fusedLocationClient) {
                    val encodedCategory = URLEncoder.encode(
                        selectedCategory,
                        StandardCharsets.UTF_8.toString()
                    )

                    val encodedAmount = URLEncoder.encode(
                        selectedAmount,
                        StandardCharsets.UTF_8.toString()
                    )

                    val encodedReason = URLEncoder.encode(
                        selectedReason,
                        StandardCharsets.UTF_8.toString()
                    )

                    val encodedMemo = URLEncoder.encode(
                        memo,
                        StandardCharsets.UTF_8.toString()
                    )

                    val safeMemo =
                        if (memo.isBlank()) {
                            "empty"
                        } else {
                            encodedMemo
                        }

                    navController.navigate(
                        "loading/$encodedCategory/$encodedAmount/$encodedReason/$safeMemo"
                    )
                }
            },

            interactionSource = interactionSource,

            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
                .height(60.dp),

            shape = RoundedCornerShape(18.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {

            Text(
                text = "분석하기",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


private fun checkAndSendNotifications(
    context: Context,
    amount: String,
    reason: String
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (amount == "10만원 이상") {
        Toast.makeText(context, "오늘 소비 금액이 높아요!", Toast.LENGTH_SHORT).show()
        sendNotification(
            context,
            notificationManager,
            1001,
            "오늘 소비 금액이 높아요!"
        )
    }

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    if (currentHour >= 22) {
        Toast.makeText(context, "늦은 시간 소비가 감지됐어요.", Toast.LENGTH_SHORT).show()
        sendNotification(
            context,
            notificationManager,
            1002,
            "늦은 시간 소비가 감지됐어요."
        )
    }

    if (reason == "유행 영향" && (amount == "5~10만원" || amount == "10만원 이상")) {
        Toast.makeText(context, "유행 영향 소비 금액이 높아요.", Toast.LENGTH_SHORT).show()
        sendNotification(
            context,
            notificationManager,
            1003,
            "유행 영향 소비 금액이 높아요."
        )
    }
}

private fun sendNotification(
    context: Context,
    notificationManager: NotificationManager,
    id: Int,
    message: String
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val builder = NotificationCompat.Builder(context, "consumption_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Moodit 소비 알림")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(id, builder.build())
}

private fun checkLocationAndSendFeedback(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onComplete: () -> Unit
) {
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocation && !hasCoarseLocation) {
        com.example.moodit.ui.theme.SharedLocationHolder.location = "위치 정보를 불러올 수 없습니다."
        onComplete()
        return
    }

    try {

        val cancellationTokenSource =
            com.google.android.gms.tasks.CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
            .addOnSuccessListener { location ->

                if (location != null) {

                    println("LAT = ${location.latitude}")
                    println("LON = ${location.longitude}")

                    val feedback = "외출 중 소비가 발생했어요."

                    Toast.makeText(
                        context,
                        feedback,
                        Toast.LENGTH_SHORT
                    ).show()

                    val notificationManager =
                        context.getSystemService(
                            Context.NOTIFICATION_SERVICE
                        ) as NotificationManager

                    sendNotification(
                        context,
                        notificationManager,
                        1004,
                        feedback
                    )

                    val address =
                        getAddressFromLocation(
                            context,
                            location.latitude,
                            location.longitude
                        )
                    println("ADDRESS = $address")

                    SharedLocationHolder.location = address

                    com.example.moodit.ui.theme.SharedLocationHolder.location =
                        address

                } else {

                    com.example.moodit.ui.theme.SharedLocationHolder.location =
                        "위치 정보를 불러올 수 없습니다."
                }

                onComplete()
            }
            .addOnFailureListener {

                com.example.moodit.ui.theme.SharedLocationHolder.location =
                    "위치 정보를 불러올 수 없습니다."

                onComplete()
            }

    } catch (e: SecurityException) {

        com.example.moodit.ui.theme.SharedLocationHolder.location =
            "위치 정보를 불러올 수 없습니다."

        onComplete()
    }
}

private fun getAddressFromLocation(
    context: Context,
    latitude: Double,
    longitude: Double
): String {

    return try {

        val geocoder =
            android.location.Geocoder(
                context,
                java.util.Locale.KOREAN
            )

        val addresses =
            geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )

        if (!addresses.isNullOrEmpty()) {

            val address = addresses[0]

            val adminArea =
                address.adminArea ?: ""

            val locality =
                address.locality ?: ""

            val subLocality =
                address.subLocality ?: ""

            listOf(
                adminArea,
                locality,
                subLocality
            )
                .filter { it.isNotBlank() }
                .joinToString(" ")

        } else {

            "위치 정보를 불러올 수 없습니다."
        }

    } catch (e: Exception) {

        "위치 정보를 불러올 수 없습니다."
    }
}