plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

/*cas {
    useAdvertisingId = true
    adapters {
        googleAds = true
//        ironSource = true
        appLovin = true
        audienceNetwork = true
        bigoAds = true
        casExchange = true
        chartboost = true
        crossPromo = true
        dtExchange = true
        hyprMX = true
        inMobi = true
        kidoz = true
        liftoffMonetize = true
        loopMe = true
        madex = true
        mintegral = true
        myTarget = true
        pangle = true
//        smaato = true
        startIO = true
        superAwesome = true
        unityAds = true
        yandexAds = true
        // Include more adapters here
    }
    // Instead of defining each adapter separately,
    // you can simply include the list of recommended adapters in one line:
    //includeOptimalAds = true
}*/

android {
    namespace = "livewallpaper.aod.screenlock.zipper"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.heartzipperlock.lovezipper.romanticlockscreen.securelock.roselock"
        minSdk = 24
        targetSdk = 35
        versionCode = 87
        versionName = "26.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        setProperty("archivesBaseName", "LoveHeartLockScreen_v$versionName($versionCode)")
    }
    signingConfigs {
        create("release") {
            storeFile = file("$rootDir\\gold_heart_zip.jks")
            storePassword = "gold_heart_zip"
            keyAlias = "gold_heart_zip"
            keyPassword = "gold_heart_zip"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            resValue ("string", "app_id", "ca-app-pub-9156380751701337~2164135461")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            resValue ("string", "app_id", "ca-app-pub-3940256099942544~3347511713")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.play:review-ktx:2.0.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
// The Kotlin ones
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.1")
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.airbnb.android:lottie:6.6.7")
    // Ads Integration
    implementation("com.google.android.gms:play-services-ads:24.4.0")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-config-ktx:22.1.0")
    implementation("com.google.firebase:firebase-crashlytics:19.4.2")
    implementation("com.google.firebase:firebase-analytics-ktx:22.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-common:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.github.skydoves:powerspinner:1.2.7")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.google.firebase:firebase-messaging:24.1.1")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation ("com.iabtcf:iabtcf-decoder:2.0.10")
    implementation ("com.github.hypersoftdev:inappbilling:3.0.3")
    // So, make sure you also include that repository in your project's build.gradle file.
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("androidx.work:work-runtime-ktx:2.10.1")
    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    // AppLovin Mediation
//    implementation ("com.cleveradssolutions:cas-sdk:3.9.10")
}

