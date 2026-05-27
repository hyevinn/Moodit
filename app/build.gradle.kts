plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {

    namespace = "com.example.moodit"

    compileSdk = 34

    defaultConfig {

        applicationId = "com.example.moodit"

        minSdk = 26
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${project.properties["GEMINI_API_KEY"]}\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    implementation("androidx.activity:activity-compose:1.9.0")

    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    implementation("androidx.compose.ui:ui")

    implementation("androidx.compose.material3:material3")

    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")


}