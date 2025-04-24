package com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.util.Log
import com.gold.zipper.goldzipper.lockscreen.royalgold.BuildConfig
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.FullScreenAds
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.FullScreenAdsTwo
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.NativeAds

    class BillingUtil(
        val activity: Activity
    ) {
        private var lifeTime: String = "demo_1"
        init {
            lifeTime = if (isDebug()) "android.test.purchased" else "demo_1"
        }
        private fun isDebug(): Boolean {
            return BuildConfig.DEBUG
        }

        fun checkPurchased(context: Context): Boolean {
            return PurchasePrefs(context).getBoolean("inApp")
        }


}

