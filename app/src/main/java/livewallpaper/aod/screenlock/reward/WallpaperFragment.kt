package livewallpaper.aod.screenlock.reward

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.databinding.FragmentWallpaperBinding
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getRewardTitle
import livewallpaper.aod.screenlock.zipper.utilities.getTimeTitle
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showAdsDialog
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen


class WallpaperFragment : Fragment() {

    private var adsmanager: AdsManager? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<WallpaperCategory>()
    private var sharedPrefUtils: DbHelper? = null
    private var _binding: FragmentWallpaperBinding? = null

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isLoadingAds = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentWallpaperBinding.inflate(layoutInflater)
        Log.d("calling", "onCreateView: load main fragment")
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics("reward_fragment_open", "reward_fragment_open -->  Click")
        adsmanager = AdsManager.appAdsInit(activity ?: requireActivity())
        sharedPrefUtils = DbHelper(requireContext())
        if (sharedPrefUtils?.getBooleanData(context ?: return, "issetTime", false) == false) {
            sharedPrefUtils?.saveData(context ?: return, "issetTime", true)
            sharedPrefUtils?.saveData(context ?: return, "setTime", System.currentTimeMillis())
            unlockNextCategory(
                view,
                sharedPrefUtils?.getLongData(
                    context ?: return,
                    "setTime",
                    System.currentTimeMillis()
                )!!
            )
        } else {
            unlockNextCategory(
                view,
                sharedPrefUtils?.getLongData(
                    context ?: return,
                    "setTime",
                    System.currentTimeMillis()
                )!!
            )
        }
        loadRewardedInterstitialAd()
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        _binding?.titleBack?.clickWithThrottle {
            findNavController().navigateUp()
        }
        loadBanner()
    }

    private fun unlockNextCategory(view: View, saveTime: Long) {
        setupRecyclerView(view, saveTime)
    }

    private fun setupRecyclerView(view: View, saveTime: Long) {
        val allCategories = (1..8).map {
            WallpaperCategory(
                name = getRewardTitle(context ?: return)[it - 1],
                nameDay = getTimeTitle()[it - 1],
                locked = false,
                saveTime
            )
        }
        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter = CategoryAdapter(categories) { _Lock, Tilte_ ->
            if (!isNetworkAvailable(activity)) {
                showToast(context ?: requireContext(), getString(R.string.no_internet))
                return@CategoryAdapter
            }
            if (BillingUtil(activity ?: return@CategoryAdapter).checkPurchased(
                    activity ?: return@CategoryAdapter
                )
            ) {
                findNavController().navigate(
                    R.id.ImageListFragment,
                    bundleOf("title" to Tilte_)
                )
                return@CategoryAdapter
            }
            if(_Lock) {
                showAdsDialog(
                    context = activity ?: return@CategoryAdapter,
                    onInApp = { ->
                        findNavController().navigate(
                            R.id.FragmentBuyScreen,
                            bundleOf("isSplash" to false)
                        )
                        return@showAdsDialog
                    },
                    onWatchAds = { ->
                        showRewardedVideo {
                            //                                if(itt==0){
//                                    showToast(context?:return@showAdsDialog,getString(R.string.please_wait_app_is_opening))
//                                }else{
                            findNavController().navigate(
                                R.id.ImageListFragment,
                                bundleOf("title" to Tilte_)
                            )
//                                }
                        }

                    })
                return@CategoryAdapter
            }else{
                showToast(context ?: requireContext(), getString(R.string.unlock))
                return@CategoryAdapter
            }

        }
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = categoryAdapter
    }

    private fun loadRewardedInterstitialAd() {
        MobileAds.initialize(
            activity ?: return
        ) { }
        if (rewardedInterstitialAd == null) {
            isLoadingAds = true
            val adRequest = AdRequest.Builder().build()

            // Load an ad.
            RewardedInterstitialAd.load(
                context ?: return,
                id_reward,
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        Log.d("MAIN_ACTIVITY_TAG", "onAdFailedToLoad: ${adError.message}")
                        isLoadingAds = false
                        rewardedInterstitialAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
                        super.onAdLoaded(rewardedAd)
                        Log.d("MAIN_ACTIVITY_TAG", "Ad was loaded.")

                        rewardedInterstitialAd = rewardedAd
                        isLoadingAds = false
                    }
                },
            )
        }
    }

    private fun showRewardedVideo(function: (() -> Unit)) {
        if (rewardedInterstitialAd == null) {
            Log.d("MAIN_ACTIVITY_TAG", "The rewarded interstitial ad wasn't ready yet.")
            function.invoke()
            return
        }

        rewardedInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("MAIN_ACTIVITY_TAG", "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedInterstitialAd = null
                    // Preload the next rewarded interstitial ad.
                    loadRewardedInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("MAIN_ACTIVITY_TAG", "Ad failed to show.")

                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    rewardedInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("MAIN_ACTIVITY_TAG", "Ad showed fullscreen content.")
                }
            }

        rewardedInterstitialAd?.show(activity ?: return) { rewardItem ->
            function.invoke()
        }
    }

    private fun loadBanner() {
        when (type_ad_native_reward_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility = View.GONE
            }

            1 -> {
                AdsManager.appAdsInit(activity ?: return).adsBanners().loadBanner(
                    activity = activity ?: return,
                    view = _binding!!.nativeExitAd,
                    addConfig = val_ad_native_reward_screen,
                    bannerId = id_adaptive_banner
                ) {
                    _binding!!.adView.visibility = View.GONE
                }
            }

            2 -> {
                AdsManager.appAdsInit(activity ?: return).nativeAds().loadNativeAd(
                    activity ?: return,
                    val_ad_native_reward_screen,
                    id_native_screen,
                    object : NativeListener {
                        override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.VISIBLE
                                _binding?.adView?.visibility = View.GONE
                                val adView = layoutInflater.inflate(
                                    R.layout.ad_unified_media,
                                    null
                                ) as NativeAdView
                                AdsManager.appAdsInit(activity ?: return).nativeAds()
                                    .nativeViewMedia(
                                        context ?: return,
                                        currentNativeAd ?: return,
                                        adView
                                    )
                                _binding?.nativeExitAd?.removeAllViews()
                                _binding?.nativeExitAd?.addView(adView)
                            }
                            super.nativeAdLoaded(currentNativeAd)
                        }

                        override fun nativeAdFailed(loadAdError: LoadAdError) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.INVISIBLE
                                _binding?.adView?.visibility = View.INVISIBLE
                            }
                            super.nativeAdFailed(loadAdError)
                        }

                        override fun nativeAdValidate(string: String) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.INVISIBLE
                                _binding?.adView?.visibility = View.INVISIBLE
                            }
                            super.nativeAdValidate(string)
                        }
                    })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel all timers when activity is destroyed
        categoryAdapter.cancelTimers()
    }

}
