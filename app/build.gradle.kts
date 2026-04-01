import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun localProperty(key: String, defaultValue: String): String {
    return localProperties.getProperty(key)?.takeIf { it.isNotBlank() } ?: defaultValue
}

android {
    namespace = "com.mindmirror"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mindmirror"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val llmEnabled = localProperty("LLM_REMOTE_ENABLED", "false").toBooleanStrictOrNull() ?: false
        val llmApiKey = localProperty("GROQ_API_KEY", localProperty("LLM_API_KEY", ""))
        val llmBaseUrl = localProperty("GROQ_BASE_URL", localProperty("LLM_BASE_URL", "https://api.groq.com/openai/v1"))
        val llmModel = localProperty("GROQ_MODEL", localProperty("LLM_MODEL", "llama-3.1-8b-instant"))

        buildConfigField("boolean", "LLM_REMOTE_ENABLED", llmEnabled.toString())
        buildConfigField("String", "LLM_API_KEY", "\"$llmApiKey\"")
        buildConfigField("String", "LLM_BASE_URL", "\"$llmBaseUrl\"")
        buildConfigField("String", "LLM_MODEL", "\"$llmModel\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-process:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

