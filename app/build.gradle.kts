import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.lmy6268.deviceshaking"
    compileSdk = getIntFromLibs { libs.versions.appCompileSdk }

    defaultConfig {
        applicationId = "io.lmy6268.deviceshaking"
        minSdk = getIntFromLibs { libs.versions.appMinSdk }
        targetSdk = getIntFromLibs { libs.versions.appTargetSdk }
        versionCode = getIntFromLibs { libs.versions.appVersionCode }
        versionName = libs.versions.appVersionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    hilt {
        enableAggregatingTask = false
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.ui.activity)
    implementation(platform(libs.compose.ui.bom))
    implementation(libs.bundles.compose.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.ui.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    //hilt 사용
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

fun getIntFromLibs(getter: () -> Provider<String>): Int = getter().get().toInt()