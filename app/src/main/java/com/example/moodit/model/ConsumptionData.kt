package com.example.moodit.model

import androidx.compose.runtime.mutableStateListOf

data class ConsumptionData(
    val category: String,
    val amount: String,
    val reason: String,
    val memo: String,
    val id: Long = System.currentTimeMillis()
)

object SharedConsumptionHolder {
    val list = mutableStateListOf<ConsumptionData>()
}