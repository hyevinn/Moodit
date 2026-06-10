package com.example.moodit.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.BorderStroke
import com.example.moodit.ui.theme.MooditTheme
import com.example.moodit.ui.theme.SharedLocationHolder
import com.example.moodit.model.SharedConsumptionHolder
import com.example.moodit.model.ConsumptionData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputScreen(navController: NavController) {

    val isDark = isSystemInDarkTheme()
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

    var selectedCategory by remember { mutableStateOf("식비") }
    var rawAmount by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("필요 소비") }
    var memo by remember { mutableStateOf("") }
    var editingItemId by remember { mutableStateOf<Long?>(null) }

    val categories = listOf("식비", "카페", "교통", "쇼핑", "취미", "자기계발", "기타")
    val reasons = listOf("필요 소비", "자기만족", "스트레스 해소", "유행 영향")

    val totalAmount = SharedConsumptionHolder.list.sumOf { it.amount.toLongOrNull() ?: 0L }
    val totalAmountFormatted = java.text.DecimalFormat("#,###").format(totalAmount)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 상단 네비게이션 및 타이틀
        item {
            Spacer(modifier = Modifier.height(18.dp))
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
                        text = "소비 기록하기",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 소비 카테고리 선택
        item {
            Text(
                text = "소비 카테고리",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clickable { selectedCategory = category }
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 소비 금액 입력
        item {
            Text(
                text = "소비 금액",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = if (rawAmount.isEmpty()) "" else java.text.DecimalFormat("#,###").format(rawAmount.toLongOrNull() ?: 0L),
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }
                    if (digits.length <= 9) { // 최대 999,999,999원 제한
                        rawAmount = digits
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "금액을 직접 입력해주세요",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                trailingIcon = {
                    Text(
                        text = "원",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(end = 12.dp)
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
                )
            )
        }

        // 소비 이유 선택
        item {
            Text(
                text = "소비 이유",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                reasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    val emoji = when (reason) {
                        "자기만족" -> "🙂"
                        "스트레스 해소" -> "💨"
                        "유행 영향" -> "✨"
                        else -> "✔"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable { selectedReason = reason }
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = reason,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 메모 입력
        item {
            Text(
                text = "오늘 소비 기분 한마디 (선택)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
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
        }

        // 소비 저장하기 버튼
        item {
            Button(
                onClick = {
                    val amountLong = rawAmount.toLongOrNull() ?: 0L
                    if (rawAmount.isEmpty() || amountLong == 0L) {
                        Toast.makeText(context, "금액을 입력해주세요 (0원 초과).", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val currentEditingId = editingItemId
                    if (currentEditingId != null) {
                        val index = SharedConsumptionHolder.list.indexOfFirst { it.id == currentEditingId }
                        if (index != -1) {
                            SharedConsumptionHolder.list[index] = ConsumptionData(
                                category = selectedCategory,
                                amount = rawAmount,
                                reason = selectedReason,
                                memo = memo,
                                id = currentEditingId
                            )
                        }
                        editingItemId = null
                        Toast.makeText(context, "소비 내역이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        SharedConsumptionHolder.list.add(
                            ConsumptionData(
                                category = selectedCategory,
                                amount = rawAmount,
                                reason = selectedReason,
                                memo = memo
                            )
                        )
                        Toast.makeText(context, "소비 내역이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                    // 입력 필드 초기화
                    rawAmount = ""
                    memo = ""
                    selectedCategory = "식비"
                    selectedReason = "필요 소비"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = if (editingItemId != null) "수정 완료" else "소비 저장하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 오늘의 소비 내역 구분 타이틀
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "오늘의 소비 내역 (${SharedConsumptionHolder.list.size}건)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 소비 리스트 아이템 표시
        if (SharedConsumptionHolder.list.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF231E2A) else Color(0xFFFBF8FF)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "저장된 소비 내역이 없습니다.\n소비를 먼저 기록해 주세요.",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            itemsIndexed(SharedConsumptionHolder.list) { index, item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFD6BEFF)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF2E243D) else Color(0xFFF9F6FE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val categoryEmoji = when (item.category) {
                                    "식비" -> "🍽"
                                    "카페" -> "☕"
                                    "교통" -> "🚌"
                                    "쇼핑" -> "🛍"
                                    "취미" -> "🎮"
                                    "자기계발" -> "📖"
                                    else -> "💡"
                                }
                                Text(
                                    text = "$categoryEmoji ${item.category}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            val amountFormatted = java.text.DecimalFormat("#,###").format(item.amount.toLongOrNull() ?: 0L)
                            Text(
                                text = "${amountFormatted}원",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val reasonEmoji = when (item.reason) {
                            "자기만족" -> "🙂"
                            "스트레스 해소" -> "💨"
                            "유행 영향" -> "✨"
                            else -> "✔"
                        }
                        Text(
                            text = "이유 : $reasonEmoji ${item.reason}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        if (item.memo.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "메모 : ${item.memo}",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "수정",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        selectedCategory = item.category
                                        rawAmount = item.amount
                                        selectedReason = item.reason
                                        memo = item.memo
                                        editingItemId = item.id
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "삭제",
                                color = Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable {
                                        SharedConsumptionHolder.list.removeAt(index)
                                        if (editingItemId == item.id) {
                                            editingItemId = null
                                            rawAmount = ""
                                            memo = ""
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 총 소비 금액 및 소비 분석하기 버튼
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 소비 금액 :",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${totalAmountFormatted}원",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

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
                    if (SharedConsumptionHolder.list.isEmpty()) {
                        Toast.makeText(context, "소비 내역을 최소 1건 이상 저장해주세요.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    checkAndSendNotifications(context, SharedConsumptionHolder.list)

                    checkLocationAndSendFeedback(context, fusedLocationClient) {
                        navController.navigate("loading")
                    }
                },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "소비 분석하기",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private fun checkAndSendNotifications(
    context: Context,
    items: List<ConsumptionData>
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val totalAmount = items.sumOf { it.amount.toLongOrNull() ?: 0L }
    if (totalAmount >= 100000) {
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

    val hasLargeTrendSpending = items.any { it.reason == "유행 영향" && (it.amount.toLongOrNull() ?: 0L) >= 50000 }
    if (hasLargeTrendSpending) {
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
        SharedLocationHolder.location = "위치 정보를 불러올 수 없습니다."
        onComplete()
        return
    }

    try {
        val cancellationTokenSource = com.google.android.gms.tasks.CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
            .addOnSuccessListener { location ->
                if (location != null) {
                    val feedback = "외출 중 소비가 발생했어요."
                    Toast.makeText(context, feedback, Toast.LENGTH_SHORT).show()

                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    sendNotification(context, notificationManager, 1004, feedback)

                    val address = getAddressFromLocation(context, location.latitude, location.longitude)
                    SharedLocationHolder.location = address
                } else {
                    SharedLocationHolder.location = "위치 정보를 불러올 수 없습니다."
                }
                onComplete()
            }
            .addOnFailureListener {
                SharedLocationHolder.location = "위치 정보를 불러올 수 없습니다."
                onComplete()
            }

    } catch (e: SecurityException) {
        SharedLocationHolder.location = "위치 정보를 불러올 수 없습니다."
        onComplete()
    }
}

private fun getAddressFromLocation(
    context: Context,
    latitude: Double,
    longitude: Double
): String {
    return try {
        val geocoder = android.location.Geocoder(context, java.util.Locale.KOREAN)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val adminArea = address.adminArea ?: ""
            val locality = address.locality ?: ""
            val subLocality = address.subLocality ?: ""

            listOf(adminArea, locality, subLocality)
                .filter { it.isNotBlank() }
                .joinToString(" ")
        } else {
            "위치 정보를 불러올 수 없습니다."
        }
    } catch (e: Exception) {
        "위치 정보를 불러올 수 없습니다."
    }
}