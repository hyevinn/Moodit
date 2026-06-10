package com.example.moodit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Data class to represent recent analysis
data class RecentAnalysis(
    val resultType: String,
    val description: String,
    val category: String,
    val amount: String,
    val reason: String
)

val DEFAULT_ANALYSIS = RecentAnalysis(
    resultType = "자기보상형 소비",
    description = "만족감과 동기부여를 중요하게\n생각하는 소비 성향이에요.",
    category = "쇼핑",
    amount = "1~5만원",
    reason = "자기만족"
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "recent_analysis_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val KEY_RESULT_TYPE = stringPreferencesKey("result_type")
        val KEY_DESCRIPTION = stringPreferencesKey("description")
        val KEY_CATEGORY = stringPreferencesKey("category")
        val KEY_AMOUNT = stringPreferencesKey("amount")
        val KEY_REASON = stringPreferencesKey("reason")
        val KEY_INSIGHT = stringPreferencesKey("insight")
    }

    val recentAnalysisFlow: Flow<RecentAnalysis?> = context.dataStore.data.map { preferences ->
        val resultType = preferences[KEY_RESULT_TYPE]
        val description = preferences[KEY_DESCRIPTION]
        val category = preferences[KEY_CATEGORY]
        val amount = preferences[KEY_AMOUNT]
        val reason = preferences[KEY_REASON]

        if (resultType != null && description != null && category != null && amount != null && reason != null) {
            RecentAnalysis(resultType, description, category, amount, reason)
        } else {
            null
        }
    }

    val insightFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_INSIGHT]
    }

    suspend fun saveRecentAnalysis(
        resultType: String,
        description: String,
        category: String,
        amount: String,
        reason: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_RESULT_TYPE] = resultType
            preferences[KEY_DESCRIPTION] = description
            preferences[KEY_CATEGORY] = category
            preferences[KEY_AMOUNT] = amount
            preferences[KEY_REASON] = reason
        }
    }

    suspend fun saveInsight(insight: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_INSIGHT] = insight
        }
    }
}
