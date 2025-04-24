package com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdOpenApp.Companion.openAdForSplash
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_utilities.id_app_open_screen
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/**
 * Created by
 * @Author: Javed Khan ,
 */

object AdsManager {
    /*Ads*/
    private var appAds: AdsManager? = null

    fun appAdsInit(activity: Activity): AdsManager {
        if (appAds == null) {
            appAds = AdsManager
            instance(activity)
            Log.d("adsInit", "   AdsClass")
        }
        return appAds as AdsManager
    }

    private fun instance(activity: Activity) {
        try {
            MobileAds.initialize(activity) {}
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(
                        listOf(
                            "6EEDFFFDB10991D90042D447C324A2C8",
                            "4C8533F42EF4302B2C5EBAD8342FED1E",
                            "3E11AE1541874059F31845BAC0C07141",
                            "22C89905A2F2191E022D6FDF3DEEA8AF",
                            "D082D453B2AD74DE2CA2D21AD93D892C",
                            "E5EB2E35D773FF290CEA242569D65135",
                            "6A38F63D0AF0EED8204DD6A830AFB817",
                            "0606A8A3B58F68943FF8F44212120F61",
                            "C4D52198B0C0E23A383A8C386E4DE7E5",
                            "0606A8A3B58F68943FF8F44212120F61",
                            "C4D52198B0C0E23A383A8C386E4DE7E5",
                            "619E7BD5EC272E8C9ADA1B25AAD9283F",
                        )
                    )
                    .build()
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun adsBanners(): AdsBanners {
        return AdsBanners.adsBanner()
    }

    fun fullScreenAds(): FullScreenAds {
        return FullScreenAds.fullScreenAds()
    }

    fun fullScreenAdsTwo(): FullScreenAdsTwo {
        return FullScreenAdsTwo.fullScreenAdsTwo()
    }

    fun nativeAds(): NativeAds {
        return NativeAds.nativeAds()
    }


    fun nativeAdsMain(): NativeAdsSplash {
        return NativeAdsSplash.nativeAds()
    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun showOpenAd(splashFragment: Activity, function: () -> Unit){
        Log.d(TAG, "loadOpenAdSplash: SHow $openAdForSplash")
        openAdForSplash?.show(splashFragment)
        openAdForSplash?.fullScreenContentCallback =  object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                function.invoke()
            }
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                function.invoke()
            }
        }
        splashFragment.let { AdOpenApp(it.application, id_app_open_screen) }
    }
    /*Useful links*/

    /**
     * HELP
     *https://developers.google.com/admob/android/test-ads
     *
     *
     * implementation platform('com.google.firebase:firebase-bom:30.3.1')
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.android.gms:play-services-ads:21.1.0")
    implementation("com.android.billingclient:billing:4.1.0")

    OPEN APP SDK
    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0"
    annotationProcessor "androidx.lifecycle:lifecycle-compiler:2.2.0"


     * */


}