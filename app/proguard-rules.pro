# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.* {
   public *;
}
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.heartzipperlock.lovezipper.romanticlockscreen.securelock.roselock.** { *; }

# Keep model classes
-keepclassmembers class com.yourpackage.models.** {
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
-keep class livewallpaper.aod.screenlock.unlimited_wallpaper.CategoriesResponse { *; }
-keep class livewallpaper.aod.screenlock.unlimited_wallpaper.Category { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener
-dontwarn android.media.LoudnessCodecController