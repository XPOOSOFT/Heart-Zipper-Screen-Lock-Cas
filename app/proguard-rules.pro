# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn com.warkiz.tickseekbar.**

-keepclassmembers class fqcn.of.javascript.interface.for.* {
   public *;
}
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }


-keepclassmembers class livewallpaper.aod.screenlock.zipper.model.LanguageModel.** {
    <fields>;
    <methods>;
}
-keepclassmembers class livewallpaper.aod.screenlock.zipper.model.SoundModel.** {
    <fields>;
    <methods>;
}

# Keep classes used by Gson
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep CategoriesResponse and related classes
-keep class livewallpaper.aod.screenlock.unlimited_wallpaper.* { *; }

-keepattributes Signature
-keepattributes Exceptions

# Keep Retrofit interfaces
-keep interface livewallpaper.aod.screenlock.unlimited_wallpaper.PixabayApiService { *; }

# Keep data models (JSON responses)

# Retrofit
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Retrofit and Gson classes
-keep class com.squareup.retrofit2.** { *; }
-keep class com.google.gson.** { *; }

# Keep models

# Keep annotations
-keepattributes RuntimeVisibleAnnotations

# Keep the type signatures for TypeToken and similar classes
-keepclassmembers class com.google.gson.reflect.TypeToken { *; }