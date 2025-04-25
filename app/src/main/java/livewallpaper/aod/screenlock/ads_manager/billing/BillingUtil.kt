package livewallpaper.aod.screenlock.ads_manager.billing

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.util.Log
import livewallpaper.aod.screenlock.zipper.BuildConfig
import livewallpaper.aod.screenlock.ads_manager.FullScreenAds
import livewallpaper.aod.screenlock.ads_manager.FullScreenAdsTwo
import livewallpaper.aod.screenlock.ads_manager.NativeAds

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

