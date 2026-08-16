# ProGuard rules for NoTubeTV v0.0.4
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Compose WebView
-keep class com.multiplatform.webview.** { *; }
-dontwarn com.multiplatform.webview.**

# TV Material
-keep class androidx.tv.material3.** { *; }

# JS Interfaces
-keepclassmembers class com.ycngmn.notubetv.utils.ExitBridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class com.ycngmn.notubetv.utils.NetworkBridge {
    @android.webkit.JavascriptInterface <methods>;
}

# General Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
