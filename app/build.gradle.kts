plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.cleveradssolutions.gradle-plugin")
}

cas {
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
}

android {
    namespace = "livewallpaper.aod.screenlock.zipper"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.heartzipperlock.lovezipper.romanticlockscreen.securelock.roselock"
        minSdk = 24
        targetSdk = 35
        versionCode = 78
        versionName = "25.8"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            resValue ("string", "app_id", "ca-app-pub-6480664593997158~3660850936")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.play:review-ktx:2.0.2")
//    implementation("com.google.android.play:core-ktx:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
// The Kotlin ones
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.airbnb.android:lottie:6.2.0")
    // Ads Integration
    implementation("com.google.android.gms:play-services-ads:23.6.0")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-config-ktx:22.0.1")
    implementation("com.google.firebase:firebase-crashlytics:19.2.1")
    implementation("com.google.firebase:firebase-analytics-ktx:22.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-common:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.github.skydoves:powerspinner:1.2.7")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation ("com.iabtcf:iabtcf-decoder:2.0.10")
    implementation ("com.github.hypersoftdev:inappbilling:3.0.3")
    // So, make sure you also include that repository in your project's build.gradle file.
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation ("androidx.work:work-runtime-ktx:2.8.1")
    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    // AppLovin Mediation
//    implementation ("com.google.ads.mediation:applovin:13.0.1.0")
    // Meta Audience Network (Facebook) Mediation
//    implementation  ("com.google.ads.mediation:facebook:6.18.0.0")
    // Pangle Mediation
//    implementation  ("com.google.ads.mediation:pangle:6.3.0.4.0")
    implementation ("com.cleveradssolutions:cas-sdk:3.9.10")
//    implementation("com.cleveradssolutions:smaato:22.7.1.0")
//    implementation("com.cleveradssolutions:startio:5.1.0.0")
}

