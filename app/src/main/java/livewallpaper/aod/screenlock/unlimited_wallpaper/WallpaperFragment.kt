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
import livewallpaper.aod.screenlock.ads_manager.AdOpenApp.Companion.rewardedInterstitialAd
import livewallpaper.aod.screenlock.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.ads_manager.AdsManager
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeType
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.FragmentWallpaperBinding
import livewallpaper.aod.screenlock.zipper.utilities.Wallpaper_Cat
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showAdsDialog
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.wallpaper_fragment

class WallpaperFragment  : Fragment() {

    private var adsmanager: AdsManager? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<Category>()
    private var sharedPrefUtils: DbHelper? = null
    private var _binding: FragmentWallpaperBinding? = null
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
        Log.d("calling", "onCreateView: load main fragment")
        adsmanager = AdsManager.appAdsInit(activity ?: requireActivity())
        sharedPrefUtils = DbHelper(requireContext())
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        loadBanner()
        loadRewardedAd()
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        try {
            if (Wallpaper_Cat.isEmpty()) {
                Log.e("JSON_ERROR", "Wallpaper_Cat is null or empty")
                return
            }

            val gson = GsonBuilder()
                .registerTypeAdapter(CategoriesResponse::class.java, CategoriesResponseAdapter())
                .create()

            val categoriesResponse = gson.fromJson(Wallpaper_Cat, CategoriesResponse::class.java)
            categories.clear()
            categories.addAll(categoriesResponse.categories)

            categoryAdapter = CategoryAdapter(context ?: return, categories) { Tilte_ ->
                if (!AdsManager.isNetworkAvailable(activity)) {
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
                        showRewardedVideo {}
                        findNavController().navigate(
                            R.id.FragmentListCustomWallpaper,
                            bundleOf("title" to Tilte_)
                        )
                    }
                )
            }

            view.findViewById<RecyclerView>(R.id.recyclerView).adapter = categoryAdapter
        } catch (e: JsonSyntaxException) {
            Log.e("JSON_ERROR", "Malformed JSON: ${e.message}")
        } catch (e: JsonParseException) {
            Log.e("JSON_ERROR", "Parsing Error: ${e.message}")
        } catch (e: Exception) {
            Log.e("GENERAL_ERROR", "Error: ${e.message}")
        }
    }

    private val admobNative by lazy { AdmobNative() }

    private fun loadRewardedAd() {
        if(!val_ad_inter_reward_screen){
            return
        }
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

        if(!val_ad_inter_reward_screen){
            return
        }
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
//                if (native_precashe_copunt_current >= native_precashe_counter) {
                admobNative.loadNativeAds(
                    activity,
                    _binding?.nativeExitAd!!,
                    id_native_screen,
                    if (val_ad_native_reward_screen)
                        1 else 0,
                    isAppPurchased = BillingUtil(activity ?: return).checkPurchased(
                        activity ?: return
                    ),
                    isInternetConnected = AdsManager.isNetworkAvailable(activity),
                    nativeType = wallpaper_fragment,
                    nativeCallBack = object : NativeCallBack {
                        override fun onAdFailedToLoad(adError: String) {
                            _binding?.nativeExitAd?.visibility = View.GONE
                            _binding?.adView?.visibility = View.GONE
                        }

                        override fun onAdLoaded() {
                            _binding?.adView?.visibility = View.GONE
                        }

                        override fun onAdImpression() {
                            _binding?.adView?.visibility = View.GONE
                        }
                    }
                )
//                } else {
//                    AdsManager.appAdsInit(activity ?: return).nativeAds().loadNativeAd(
//                        activity ?: return,
//                        val_ad_native_reward_screen,
//                        id_native_screen,
//                        object : NativeListener {
//                            override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
//                                if (isAdded && isVisible && !isDetached) {
//                                    _binding?.nativeExitAd?.visibility = View.VISIBLE
//                                    _binding?.adView?.visibility = View.GONE
//                                    val adView = layoutInflater.inflate(
//                                        if (val_ad_native_reward_screen_h) R.layout.ad_unified_media else R.layout.ad_unified_privacy,
//                                        null
//                                    ) as NativeAdView
//                                    AdsManager.appAdsInit(activity ?: return).nativeAds()
//                                        .nativeViewMedia(
//                                            context ?: return,
//                                            currentNativeAd ?: return,
//                                            adView
//                                        )
//                                    _binding?.nativeExitAd?.removeAllViews()
//                                    _binding?.nativeExitAd?.addView(adView)
//                                }
//                                super.nativeAdLoaded(currentNativeAd)
//                            }
//
//                            override fun nativeAdFailed(loadAdError: LoadAdError) {
//                                if (isAdded && isVisible && !isDetached) {
//                                    _binding?.nativeExitAd?.visibility = View.INVISIBLE
//                                    _binding?.adView?.visibility = View.INVISIBLE
//                                }
//                                super.nativeAdFailed(loadAdError)
//                            }
//
//                            override fun nativeAdValidate(string: String) {
//                                if (isAdded && isVisible && !isDetached) {
//                                    _binding?.nativeExitAd?.visibility = View.INVISIBLE
//                                    _binding?.adView?.visibility = View.INVISIBLE
//                                }
//                                super.nativeAdValidate(string)
//                            }
//                        })
//                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

}
