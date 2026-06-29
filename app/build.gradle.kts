plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace   = "com.thrivelearn.app"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.thrivelearn.app"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 2
        versionName   = "2.0.0"

        // Keeps TalkBack content-description strings in release builds
        vectorDrawables.useSupportLibrary = true
    }

    // ── AAB (Play Store) release build ───────────────────────────────────────
    bundle {
        language {
            // Bundle all language resources; Play delivers the right split APK
            enableSplit = true
        }
        density  { enableSplit = true }
        abi      { enableSplit = true }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled   = true   // R8 full-mode shrink + obfuscate
            isShrinkResources = true   // remove unused drawables/strings
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Crash-safe obfuscation: keeps mapping.txt for symbolication
        }
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget        = "17"
        // Compose compiler metrics (delete in production if not needed)
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ── Core ────────────────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")

    // ── ViewModel (needed for AccessibilityViewModel) ────────────────────────
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

    // ── Jetpack Compose BOM (locks all compose versions consistently) ────────
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    // ── Media3 (ExoPlayer) ──────────────────────────────────────────────────
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    // ── Debug only ──────────────────────────────────────────────────────────
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
