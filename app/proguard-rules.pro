# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Serializable classes
-keep,includedescriptorclasses class com.kanzan.touchrecorder.**$$serializer { *; }
-keepclassmembers class com.kanzan.touchrecorder.** {
    *** Companion;
}
-keepclasseswithmembers class com.kanzan.touchrecorder.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# AdMob
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}
