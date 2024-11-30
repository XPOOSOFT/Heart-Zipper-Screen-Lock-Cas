package livewallpaper.aod.screenlock.unlimited_wallpaper

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
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.gson.Gson
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdOpenApp.Companion.rewardedInterstitialAd
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsBanners.isDebug
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.databinding.FragmentWallpaperBinding
import livewallpaper.aod.screenlock.zipper.utilities.Wallpaper_Cat
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showAdsDialog
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen


class WallpaperFragment : Fragment() {

    private var adsmanager: AdsManager? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<Category>()
    private var sharedPrefUtils: DbHelper? = null
    private var _binding: FragmentWallpaperBinding? = null
//    private var rewardedAd: RewardedAd? = null
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

        firebaseAnalytics(
            "custom_wallpaper_fragment_open",
            "custom_wallpaper_fragment_open -->  Click"
        )
        adsmanager = AdsManager.appAdsInit(activity ?: requireActivity())
        sharedPrefUtils = DbHelper(requireContext())
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        _binding?.topLay?.title?.text = getString(R.string.un_wallpaper)
        loadBanner()
        loadRewardedAd()
        setupRecyclerView(view)
    }


    private fun setupRecyclerView(view: View) {
        // Sample JSON
//        val Wallpaper_Cat = """
//            {
//              "categories": [
//                {
//                  "title": "christmas",
//                  "images": "http://fireitstudio-502880185.imgix.net/new/christmas/1.png"
//                },
//                {
//                  "title": "vintage",
//                  "images": "http://fireitstudio-502880185.imgix.net/new/vintage/1.png"
//                }
//              ]
//            }
//        """
        // Parse JSON
//        val categoriesResponse = Gson().fromJson(Wallpaper_Cat, CategoriesResponse::class.java)
        try {
            val categoriesResponse = Gson().fromJson(Wallpaper_Cat, CategoriesResponse::class.java)
            categories.clear()
            categories.addAll(categoriesResponse.categories)
            categoryAdapter = CategoryAdapter(categories) { Tilte_ ->
                if (!isNetworkAvailable(activity)) {
                    showToast(context ?: requireContext(), getString(R.string.no_internet))
                    return@CategoryAdapter
                }
                if (BillingUtil(activity ?: return@CategoryAdapter).checkPurchased(
                        activity ?: return@CategoryAdapter
                    )
                ) {
                    findNavController().navigate(
                        R.id.FragmentListCustomWallpaper,
                        bundleOf("title" to Tilte_)
                    )
                    return@CategoryAdapter
                }
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
                        if (rewardedInterstitialAd == null) {
                            showToast(
                                context ?: requireContext(),
                                getString(R.string.try_agin_ad_not_load)
                            )
                            loadRewardedAd()
                            return@showAdsDialog
                        }
                        showRewardedVideo {
                        }

                        findNavController().navigate(
                            R.id.FragmentListCustomWallpaper,
                            bundleOf("title" to Tilte_)
                        )
                    })
                return@CategoryAdapter

            }
            view.findViewById<RecyclerView>(R.id.recyclerView).adapter = categoryAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun loadRewardedAd() {
        if (rewardedInterstitialAd == null) {
            val adRequest = AdRequest.Builder().build()

            // Load a rewarded ad.
            RewardedAd.load(
                context ?: return,
                id_reward,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        Log.d("MAIN_ACTIVITY_TAG", "onAdFailedToLoad: ${adError.message}")
                        isLoadingAds = false
                        rewardedInterstitialAd = null
                    }

                    override fun onAdLoaded(rewarded: RewardedAd) {
                        super.onAdLoaded(rewarded)
                        Log.d("MAIN_ACTIVITY_TAG", "Ad was loaded.")
                        rewardedInterstitialAd = rewarded
                        isLoadingAds = true
                    }
                }
            )
        }
    }

    private fun showRewardedVideo(function: (() -> Unit)) {
        if (rewardedInterstitialAd == null) {
            Log.d("MAIN_ACTIVITY_TAG", "The rewarded ad wasn't ready yet.")
            function.invoke()
            return
        }

        rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("MAIN_ACTIVITY_TAG", "Ad was dismissed.")
                rewardedInterstitialAd = null
                isSplash = true
                loadRewardedAd() // Preload the next rewarded ad
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("MAIN_ACTIVITY_TAG", "Ad failed to show.")
                isSplash = true
                rewardedInterstitialAd = null
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("MAIN_ACTIVITY_TAG", "Ad showed fullscreen content.")
                rewardedInterstitialAd = null
                isSplash = false
                isLoadingAds = false
            }
        }

        rewardedInterstitialAd?.show(activity ?: return) { rewardItem ->
            // Handle the reward
            Log.d(
                "MAIN_ACTIVITY_TAG",
                "User earned reward: ${rewardItem.amount} ${rewardItem.type}"
            )
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }
}
