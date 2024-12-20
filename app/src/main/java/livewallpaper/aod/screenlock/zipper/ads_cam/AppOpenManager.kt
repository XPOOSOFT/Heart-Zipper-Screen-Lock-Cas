package livewallpaper.aod.screenlock.zipper.ads_cam

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.cleversolutions.ads.AdCallback
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdStatusHandler
import com.cleversolutions.ads.CASAppOpen
import com.cleversolutions.ads.LoadAdCallback
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.val_app_open

class AppOpenManager(private val application: Application, private val casId: String) :
    ActivityLifecycleCallbacks, LifecycleObserver {

    private var appOpenAd: CASAppOpen? = null
    private var isVisibleAppOpenAd: Boolean = false
    private val tag = "AppOpenManager"
    private var currentActivity: Activity? = null

    init {
        // Initialize the App Open Ad
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAd = CASAppOpen.create(casId).apply {
            contentCallback = object : AdCallback {
                override fun onShown(ad: AdStatusHandler) {
                    Log.d(tag, "App Open Ad shown")
                }

                override fun onShowFailed(message: String) {
                    Log.e(tag, "App Open Ad show failed: $message")
                    isVisibleAppOpenAd = false
                }

                override fun onClicked() {
                    Log.d(tag, "App Open Ad clicked")
                }

                override fun onClosed() {
                    Log.d(tag, "App Open Ad closed")
                    loadAd(val_app_open)
                    isVisibleAppOpenAd = false
                }
            }
        }
        loadAd(val_app_open)
    }
    fun loadAd(isLoadingAppResources: Boolean) {
        appOpenAd?.loadAd(application, object : LoadAdCallback {
            override fun onAdLoaded() {
                Log.d(tag, "App Open Ad loaded")
                if (isLoadingAppResources) {
                    isVisibleAppOpenAd = true
                }
            }

            override fun onAdFailedToLoad(error: AdError) {
                Log.e(tag, "App Open Ad failed to load: ${error.message}")
            }
        })
    }

    fun showAdIfAvailable(activity: Activity) {
        if (isVisibleAppOpenAd) {
            appOpenAd?.show(activity)
        } else {
            loadAd(val_app_open)
            Log.d(tag, "App Open Ad is not ready to show")
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityResumed(p0: Activity) {
        Log.d("onActivityResumed", "onActivityResumed: $p0")
        currentActivity = p0
//        currentActivity?.let { showAdIfAvailable(it) }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        currentActivity?.let { showAdIfAvailable(it) }
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

}
