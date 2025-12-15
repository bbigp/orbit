plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // 启用 Kotlin 注解处理器
    id("com.google.dagger.hilt.android") // Hilt 插件
    id("kotlin-parcelize")
}

android {
    namespace = "cn.coolbet.orbit"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "cn.coolbet.orbit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation("androidx.compose.animation:animation")
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material3:material3-window-size-class") // 包含 PullToRefreshBox
    implementation(libs.androidx.compose.foundation)
//    implementation("androidx.navigation:navigation-compose")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    // Retrofit (HTTP Client)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    // JSON Converter (使用 Gson)
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // Retrofit 的 OkHttp 拦截器 (用于日志调试)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // ViewModel 和 Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    // Hilt 核心依赖
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.androidx.compose.ui.unit)
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    // Hilt 注解处理器 (需要使用 kapt)
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    // 针对 Compose Navigation 的集成 (您之前使用了 Compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // 如果您需要在 Activity/Fragment 中使用 Hilt，可能还需要以下依赖：
     kapt("androidx.hilt:hilt-compiler:1.2.0")
    // Room 核心库和 KTX (协程支持)
    val roomVersion = "2.8.0" // 稳定版本，兼容 Kotlin 2.0.x
    implementation("androidx.room:room-runtime:${roomVersion}")
    implementation("androidx.room:room-ktx:${roomVersion}")
    // Room 编译器 (使用 KSP)
    kapt("androidx.room:room-compiler:$roomVersion")
    // Hilt 编译器的 KSP 版本也需要检查，通常它也会使用匹配的 KSP 版本
    kapt("com.google.dagger:hilt-compiler:2.51.1") // 确保您的 Hilt 版本也是最新的
    // https://mvnrepository.com/artifact/cafe.adriel.voyager/voyager-navigator
    val voyagerVersion = "1.1.0-beta03"
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-transitions:${voyagerVersion}")
    implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-hilt:$voyagerVersion")
    implementation("androidx.work:work-runtime-ktx:2.11.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    implementation("org.jsoup:jsoup:1.21.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}