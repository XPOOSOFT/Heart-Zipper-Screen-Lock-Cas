package livewallpaper.aod.screenlock.zipper.ui

import android.content.Context.VIBRATOR_SERVICE
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import livewallpaper.aod.screenlock.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.ads_manager.AdsManager
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeCallBack
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.SetPasswordLyBinding
import livewallpaper.aod.screenlock.zipper.utilities.PasswordAdapter
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.apply_password
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getNativeLayout
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentApplyPassword  : Fragment() {

    private lateinit var vibrator: Vibrator
    private var noOfPasswordWritten = 0
    private var adsManager: AdsManager? = null
    var paswordValue2 = ""
    var passwordValue1 = ""
    var mainPassword = ""
    private var _binding: SetPasswordLyBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SetPasswordLyBinding.inflate(layoutInflater)
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

        firebaseAnalytics("fragment_open_pass_screen", "fragment_open_pass_screen")
        adsManager = AdsManager.appAdsInit(activity?:return)
        loadBanner()
        mClickListener()
        vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
    }

    private fun mClickListener() {

        _binding?.topLay?.title?.text = getString(R.string.set_pin)
        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        _binding?.topLay?.settingBtn?.setOnClickListener {
            findNavController().navigateUp()
        }

        _binding?.goldNum3?.setOnClickListener()
        {
            mainPassword += "3"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum4?.setOnClickListener()
        {
            mainPassword += "4"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum5?.setOnClickListener()
        {
            mainPassword += "5"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum0?.setOnClickListener()
        {
            mainPassword += "0"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum1?.setOnClickListener()
        {
            mainPassword += "1"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum2?.setOnClickListener()
        {
            mainPassword += "2"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum6?.setOnClickListener()
        {
            mainPassword += "6"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum7?.setOnClickListener()
        {
            mainPassword += "7"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum8?.setOnClickListener()
        {
            mainPassword += "8"
            setPinDots(mainPassword.length)
        }
        _binding?.goldNum9?.setOnClickListener()
        {
            mainPassword += "9"
            setPinDots(mainPassword.length)
        }
        _binding?.goldBackBtn?.setOnClickListener()
        {
            passwordBackPress()
        }

    }

    private fun passwordBackPress() {

        if (mainPassword.length > 1) {
            mainPassword = mainPassword.substring(0, mainPassword.length - 1)
            setPinDots(mainPassword.length)
        } else {
            mainPassword = ""
            setPinDots(0)
        }
    }

    private fun setPinDots(length: Int) {
        vibrator.vibrate(100)
        when (length) {
            0 -> {
                setGoldenDots(
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot
                )
            }

            1 -> {
                setGoldenDots(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot
                )
            }

            2 -> {
                setGoldenDots(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot
                )

            }

            3 -> {
                setGoldenDots(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_unfill_dot
                )
            }

            4 -> {
                setGoldenDots(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot
                )
                setPasswordApplied()
            }
        }

    }

    private fun setPasswordApplied() {
        noOfPasswordWritten += 1
        if (noOfPasswordWritten == 1) {
            setFirstAttempt()
            _binding?.goldPasswordId?.text = getString(R.string.enter_your_pin_code_again)
            _binding?.topLay?.title?.text = getString(R.string.confirm_pin)
        } else if (noOfPasswordWritten == 2) {
            setSecondAttempt()
        }

        setGoldenDots(
            R.drawable.gold_unfill_dot,
            R.drawable.gold_unfill_dot,
            R.drawable.gold_unfill_dot,
            R.drawable.gold_unfill_dot
        )
    }

    private fun setFirstAttempt() {

        _binding?.goldPasswordId?.text = getString(R.string.re_enter_pass)
        passwordValue1 = mainPassword
        mainPassword = ""
    }

    private fun setSecondAttempt() {

        paswordValue2 = mainPassword
        mainPassword = ""
        if (paswordValue2 == passwordValue1) {
            PasswordAdapter.SavePassword(context?:requireContext(), passwordValue1)
            showToast(context?:requireContext(), "password is applied successfully")
            findNavController().navigateUp()
        } else {
            _binding?.goldPasswordId?.text = getString(R.string.set_password)
            noOfPasswordWritten = 0
            paswordValue2 = ""
            passwordValue1 = ""
            showToast(context?:requireContext(), "Password Mismatch")
        }
    }

    private fun setGoldenDots(dotImg1: Int, dotImg2: Int, dotImg3: Int, dotImg4: Int) {
        _binding?.dotGold1?.setImageResource(dotImg1)
        _binding?.goldDot2?.setImageResource(dotImg2)
        _binding?.goldDot3?.setImageResource(dotImg3)
        _binding?.goldDot4?.setImageResource(dotImg4)
    }
    private val admobNative by lazy { AdmobNative() }
    private fun loadBanner(){
        when (type_ad_native_password_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                adsManager?.adsBanners()?.loadBanner(
                    activity = activity ?: return,
                    view = _binding!!.nativeExitAd,
                    addConfig = val_ad_native_password_screen,
                    bannerId = id_adaptive_banner
                ) {
                    _binding!!.adView?.visibility=View.GONE
                }
            }

            2 -> {
//                if (native_precashe_copunt_current >= native_precashe_counter) {
                val adView = activity?.layoutInflater?.inflate(
                    getNativeLayout(
                        apply_password, _binding?.nativeExitAd!!,
                        activity?:return
                    ),
                    null
                ) as NativeAdView
                admobNative.loadNativeAds(
                    activity,
                    _binding?.nativeExitAd!!,
                    id_native_screen,
                    if (val_ad_native_password_screen)
                        1 else 0,
                    isAppPurchased = BillingUtil(activity?:return).checkPurchased(activity?:return),
                    isInternetConnected = AdsManager.isNetworkAvailable(activity),
                    nativeType = apply_password,
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
//                        val_ad_native_password_screen,
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
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        _binding=null
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}