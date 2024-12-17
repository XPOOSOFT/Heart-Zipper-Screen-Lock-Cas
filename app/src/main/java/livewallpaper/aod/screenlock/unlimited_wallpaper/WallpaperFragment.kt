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
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.android.CAS.manager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.RewardedAdManager
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
import livewallpaper.aod.screenlock.zipper.ads_manager.AdOpenApp.Companion.rewardedInterstitialAd
import livewallpaper.aod.screenlock.zipper.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeType
import livewallpaper.aod.screenlock.zipper.databinding.FragmentWallpaperBinding
import livewallpaper.aod.screenlock.zipper.utilities.Wallpaper_Cat
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_copunt_current
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_counter
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showAdsDialog
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen_h
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_security_screen


class WallpaperFragment : Fragment() {

//    private var adsmanager: AdsManager? = null
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
//        adsmanager = AdsManager.appAdsInit(activity ?: requireActivity())
        sharedPrefUtils = DbHelper(requireContext())
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        _binding?.topLay?.title?.text = getString(R.string.k_wallpaper_new)
        loadBanner()
        RewardedAdManager(adManager).initializeRewardedAd()
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
                    },
                    onWatchAds = { ->
                        if (!adManager.isRewardedAdReady) {
                            showToast(
                                context ?: requireContext(),
                                getString(R.string.try_agin_ad_not_load)
                            )
                            RewardedAdManager(adManager).initializeRewardedAd()
                            return@showAdsDialog
                        }
                        RewardedAdManager(adManager).showRewardAds(activity?:return@showAdsDialog) {
                            findNavController().navigate(
                                R.id.FragmentListCustomWallpaper,
                                bundleOf("title" to Tilte_)
                            )
                        }
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

/*    private fun loadRewardedAd() {
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
 */

    private fun loadBanner(){
        when (type_ad_native_reward_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                loadBanner(val_ad_native_reward_screen)
            }

            2 -> {

            }
        }
    }

    private fun loadBanner(isAdsShow: Boolean) {
        _binding?.nativeExitAd?.apply {
            if (!isAdsShow) {
                visibility = View.INVISIBLE
                _binding?.adView?.visibility = View.INVISIBLE
                return
            }
            loadNativeBanner(
                context = requireContext(),
                isAdsShow = true,
                adSize = AdSize.LEADERBOARD, // Customize as needed
                onAdLoaded = { toggleVisibility(_binding?.nativeExitAd, true) },
                onAdFailed = { toggleVisibility(_binding?.nativeExitAd, false) },
                onAdPresented = { Log.d(TAG, "Ad presented from network: ${it.network}") },
                onAdClicked = { Log.d(TAG, "Ad clicked!") }
            )
        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adView?.visibility=View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }
}
