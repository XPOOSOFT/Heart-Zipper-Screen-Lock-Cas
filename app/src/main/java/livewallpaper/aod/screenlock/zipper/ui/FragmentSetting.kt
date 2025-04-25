package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdmobNative
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdsManager
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.BillingUtil
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.interfaces.NativeCallBack
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.showTwoInterAd
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.FragmentSettingBinding
import livewallpaper.aod.screenlock.zipper.utilities.CheckBoxUpdater.UCC
import livewallpaper.aod.screenlock.zipper.utilities.CheckBoxUpdater.UL
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.PasswordSet
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SpeedActivePref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.SavePref
import livewallpaper.aod.screenlock.zipper.utilities.PasswordAdapter
import livewallpaper.aod.screenlock.zipper.utilities.PasswordAdapter.checkPasswordAct
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setting_fragment
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showSpeedDialogNew
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_password_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_sound_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentSetting  : Fragment() {

    private var isPassword: Boolean = false
    private var adsManager: AdsManager? = null
    private var _binding: FragmentSettingBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        Log.d("calling", "onCreateView: load main fragment")
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(++PurchaseScreen == val_inapp_frequency && !BillingUtil(activity?:return).checkPurchased(activity?:return)){
            PurchaseScreen =0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        firebaseAnalytics("fragment_open_setting_screen", "fragment_open_setting_screen")
        adsManager = AdsManager.appAdsInit(activity?:requireActivity())

        loadBanner()
        isPassword = checkPasswordAct(context)!!
        Log.d("fragment_setting", "onViewCreated: $isPassword")
        switchCheck()
        _binding?.selectMusic?.setOnClickListener {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity?:requireActivity(),
                    remoteConfigNormal = val_ad_inter_sound_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "sound_activity",
                    isBackPress = true,
                    layout = _binding?.adsLay ?: return@setOnClickListener
                ){
                    findNavController().navigate(R.id.FragmentSoundSelection)
                }
            }
        }
        _binding?.selectSpeed?.setOnClickListener {
            showSpeedDialogNew(
                activity?:return@setOnClickListener,
                onPositiveNoClick = {
                },
                onPositiveYesClick = {
                    SavePref(
                        SpeedActivePref,
                        it.toString() + "",
                        context?:requireContext()
                    )
                }
            )
        }
        _binding?.topLay?.title?.text = getString(R.string.app_security)
        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        _binding?.password?.setOnClickListener {
            if (isPassword) {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity?:requireActivity(),
                        remoteConfigNormal = val_ad_inter_password_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "password_apply_activity",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ){
                        findNavController().navigate(R.id.FragmentApplyPassword)
                    }
                }
            } else {
                showToast(context?:requireContext(), getString(R.string.first_enable_password))
            }
        }
        _binding?.securityQView?.setOnClickListener {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity?:requireActivity(),
                    remoteConfigNormal = val_ad_inter_security_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "activity_all_style",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return@setOnClickListener
                ){
                    findNavController().navigate(R.id.SecurityQuestionFragment)
                }
            }
        }

    }

    private fun switchCheck() {
        _binding?.soundSwitch?.setOnCheckedChangeListener { compoundButton, bool ->
            if (compoundButton.isPressed) {
                if (bool) {
                    UCC(
                        bool,
                        ConstantValues.SoActivePref,
                        context?:requireContext()
                    )
                } else {
                    UCC(
                        bool,
                        ConstantValues.SoActivePref,
                        context?:requireContext(),
                    )
                }
            }
        }
        _binding?.vibrationSwitch?.setOnCheckedChangeListener { compoundButton, bool ->
            if (compoundButton.isPressed) {
                if (bool) {
                    UCC(
                        bool,
                        ConstantValues.VibratActivePref,
                        context?:requireContext()
                    )
                } else {
                    UCC(
                        bool,
                        ConstantValues.VibratActivePref,
                        context?:requireContext()
                    )
                }
            }
        }
        _binding?.passwordSwitch?.setOnCheckedChangeListener { compoundButton, bool ->
            if (compoundButton.isPressed) {
                if (bool) {
                    if (!isPassword) {
                        _binding?.passwordSwitch?.isChecked = false
                        adsManager?.let {
                            showTwoInterAd(
                                ads = it,
                                activity = activity?:requireActivity(),
                                remoteConfigNormal = val_ad_inter_password_screen_front,
                                adIdNormal = id_inter_main_medium,
                                tagClass = "password_apply_activity",
                                isBackPress = false,
                                layout = _binding?.adsLay ?: return@setOnCheckedChangeListener
                            ){
                                findNavController().navigate(R.id.FragmentApplyPassword)
                            }
                        }
                        return@setOnCheckedChangeListener
                    }
                    UCC(
                        bool,
                        PasswordSet,
                        context?:requireContext()
                    )
                } else {
                    PasswordAdapter.SavePassword(context?:requireContext(), "")
                    UCC(
                        bool,
                        PasswordSet,
                        context?:requireContext()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.passwordSwitch?.isChecked = checkPasswordAct(context?:requireContext())!!
        Log.d("fragment_setting", "onResume: $isPassword")
        UL(ConstantValues.VibratActivePref, context?:requireContext(), _binding?.vibrationSwitch)
        UL(ConstantValues.SoActivePref, context?:requireContext(), _binding?.soundSwitch)
    }

    private val admobNative by lazy { AdmobNative() }

    private fun loadBanner(){
        when (type_ad_native_setting_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                adsManager?.adsBanners()?.loadBanner(
                    activity = activity ?: return,
                    view = _binding!!.nativeExitAd,
                    addConfig = val_ad_native_setting_screen,
                    bannerId = id_adaptive_banner
                ) {
                    _binding?.adView?.visibility=View.GONE
                }
            }

            2 -> {
//                if (native_precashe_copunt_current >= native_precashe_counter) {
                admobNative.loadNativeAds(
                    activity,
                    _binding?.nativeExitAd!!,
                    id_native_screen,
                    if (val_ad_native_setting_screen)
                        1 else 0,
                    isAppPurchased = BillingUtil(activity?:return).checkPurchased(activity?:return),
                    isInternetConnected = AdsManager.isNetworkAvailable(activity),
                    nativeType =setting_fragment,
                    nativeCallBack = object : NativeCallBack {
                        override fun onAdFailedToLoad(adError: String) {
                            _binding?.nativeExitAd?.visibility = View.GONE
                            _binding?.adView?.visibility = View.GONE}
                        override fun onAdLoaded() {
                            _binding?.adView?.visibility = View.GONE}
                        override fun onAdImpression() {
                            _binding?.adView?.visibility = View.GONE}
                    }
                )
//                } else {
//                    adsManager?.nativeAds()?.loadNativeAd(
//                        activity ?: return,
//                        val_ad_native_setting_screen,
//                        id_native_screen,
//                        object : NativeListener {
//                            override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
//                                if (isAdded && isVisible && !isDetached) {
//                                    _binding?.nativeExitAd?.visibility = View.VISIBLE
//                                    _binding?.adView?.visibility = View.GONE
//                                    val adView = layoutInflater.inflate(
//                                        R.layout.ad_unified_media,
//                                        null
//                                    ) as NativeAdView
//                                    adsManager?.nativeAds()?.nativeViewMedia(
//                                        context ?: return,
//                                        currentNativeAd ?: return,
//                                        adView
//                                    )
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

}