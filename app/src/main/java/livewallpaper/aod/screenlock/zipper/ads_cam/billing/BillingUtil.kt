package livewallpaper.aod.screenlock.zipper.ads_cam.billing

import android.app.Activity
import android.content.Context
import livewallpaper.aod.screenlock.zipper.BuildConfig


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

