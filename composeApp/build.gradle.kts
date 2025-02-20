import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.androidx.annotation)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.adaptive)
            implementation(libs.adaptive.layout)
            implementation(libs.adaptive.navigation)
            implementation(libs.paging.compose)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.kotlin.logging)

            // 模糊效果
            implementation(libs.haze)
//            implementation(libs.haze.materials)
            // Lottie
            implementation(libs.kottie)
            // UUID
            implementation(libs.kotlinx.uuid.core)
            implementation(libs.kotlinx.uuid.datetime)
            implementation(libs.kotlinx.uuid.sqldelight)
            // 下拉刷新
            implementation(libs.pullrefresh)
            // https://github.com/ltttttttttttt/ComposeViews?tab=readme-ov-file
            // ComposePager
            // Banner
            // PagerIndicator
            // ImageBanner
            // RefreshLayout + PullToRefresh + VerticalRefreshableLayout
            // FlowLayout
            // ValueSelector + DateSelector
            // MenuFloatingActionButton
            // ChainScrollableComponent + ScrollableAppBar + SwipeToDismiss
            implementation(libs.composeviews)

            // 折叠效果: https://github.com/onebone/compose-collapsing-toolbar
            // 由于仅适配了Android，所以这里不引入，直接通过源码引入
//            implementation("me.onebone:toolbar-compose:2.3.5")

        }
    }
}

android {
    namespace = "com.jetbrains.kmpapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jetbrains.kmpapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
}
