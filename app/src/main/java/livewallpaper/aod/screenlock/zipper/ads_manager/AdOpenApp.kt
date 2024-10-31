package livewallpaper.aod.screenlock.zipper.ads_manager

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.val_app_open
import java.util.*

/**
 * Created by
 * @Author: Javed Khan ,
 * @Company: Xpoosoft ,
 * @Email: xpoosoft@gmail.com ,
 * on 12/2/2021 , Thu .
 */


class AdOpenApp(private val myApplication: Application, private var openAppAdId: String) :
    Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var customDialog: Dialog? = null
//    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null

    fun showCustomDialogAndAd() {
        customDialog = Dialog(currentActivity ?: return)
        val inflater = LayoutInflater.from(currentActivity)
        customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view: View = inflater.inflate(R.layout.custom_dialog, null)
        customDialog?.setContentView(view)
        customDialog?.setCancelable(false)
        // Make the dialog full-screen
        val window: Window? = customDialog?.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawableResource(android.R.color.transparent) // Set the background transparent if needed

        // Optionally, you can hide the status bar and navigation bar for true fullscreen
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // Show the dialog
        customDialog?.show()
        // Use a Handler to delay the showing of the ad for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            showAdIfAvailable()
        }, 3000) // 3 seconds delay
    }
    fun fetchAd() {
        if (!val_app_open) {
            return
        }
        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                openAdForSplash = appOpenAd
                loadTime = Date().time
                super.onAdLoaded(appOpenAd)
            }

        }
        val request = adRequest
        if (openAppAdId == "" || NativeAds.isDebug()) {
            openAppAdId = "ca-app-pub-3940256099942544/3419835294"
        }
        AppOpenAd.load(
            myApplication, openAppAdId, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback ?: return
        )
    }

    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private val isAdAvailable: Boolean
        get() = openAdForSplash != null && wasLoadTimeLessThanNHoursAgo(4)

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
        customDialog?.dismiss()
    }

    private fun showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        openAdForSplash = null
                        isShowingAd = false
                        customDialog?.dismiss()
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(LOG_TAG, "Will show ad. ${adError.message}")
                        customDialog?.dismiss()
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            if (isSplash) {
                openAdForSplash?.show(currentActivity ?: return)
                openAdForSplash?.fullScreenContentCallback = fullScreenContentCallback
            }
        } else {
            customDialog?.dismiss()
            Log.d(LOG_TAG, "Can not show ad.")
            fetchAd()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            customDialog?.dismiss()
        }, 4000) // 3 seconds delay
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (isSplash && !isShowingAd  && val_app_open) {
            showCustomDialogAndAd()
        }else{
            fetchAd()
        }
    }

    companion object {
        private const val LOG_TAG = "AppOpenManager"
        var isShowingAd = false
        var openAdForSplash: AppOpenAd? = null
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Init this class with in your Application class eg
    private lateinit var appOpenManager: AdOpenApp
    override fun onCreate() {
    super.onCreate()
    appOpenManager = AdOpenApp(this,"")
    }
     * */

}