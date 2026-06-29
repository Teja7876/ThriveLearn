# ── ThriveLearn ProGuard / R8 rules ────────────────────────────────────────────
# Applied to the release (AAB) build only.  debug builds are unobfuscated.

# ── Kotlin ─────────────────────────────────────────────────────────────────────
-keep class kotlin.** { *; }
-keepclassmembers class **$WhenMappings { *; }
-keep class kotlinx.coroutines.** { *; }

# ── Jetpack Compose ────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── ViewModel (Android lifecycle) ──────────────────────────────────────────────
-keep class androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# ── Accessibility model (never obfuscate preference keys) ──────────────────────
-keep class com.thrivelearn.app.accessibility.** { *; }

# ── Enum classes (needed for profile name → valueOf round-trip in prefs) ───────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ── SharedPreferences & DataStore ──────────────────────────────────────────────
-keep class android.content.SharedPreferences { *; }

# ── Android speech/TTS ─────────────────────────────────────────────────────────
-keep class android.speech.** { *; }
-keep class android.speech.tts.** { *; }

# ── Media3 / ExoPlayer ─────────────────────────────────────────────────────────
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ── Remove logging in release ──────────────────────────────────────────────────
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
