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
import com.cleversolutions.ads.AdSize
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeType
import livewallpaper.aod.screenlock.zipper.ads_cam.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
import livewallpaper.aod.screenlock.zipper.databinding.SetPasswordLyBinding
import livewallpaper.aod.screenlock.zipper.utilities.PasswordAdapter
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentApplyPassword : Fragment() {

    private lateinit var vibrator: Vibrator
    private var noOfPasswordWritten = 0
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

        if (++PurchaseScreen == val_inapp_frequency) {
            PurchaseScreen = 0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        try {
            loadBanner()
            mClickListener()
            vibrator = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator
            setupBackPressedCallback {
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            PasswordAdapter.SavePassword(context, passwordValue1)
            showToast(context ?: requireContext(), "password is applied successfully")
            findNavController().navigateUp()
        } else {
            _binding?.goldPasswordId?.text = getString(R.string.set_password)
            noOfPasswordWritten = 0
            paswordValue2 = ""
            passwordValue1 = ""
            showToast(context ?: requireContext(), "Password Mismatch")
        }
    }

    private fun setGoldenDots(dotImg1: Int, dotImg2: Int, dotImg3: Int, dotImg4: Int) {
        _binding?.dotGold1?.setImageResource(dotImg1)
        _binding?.goldDot2?.setImageResource(dotImg2)
        _binding?.goldDot3?.setImageResource(dotImg3)
        _binding?.goldDot4?.setImageResource(dotImg4)
    }

    /*    private fun loadNative() {
            adsManager?.nativeAds()?.loadNativeAd(
                activity?:requireActivity(),
                val_ad_native_password_screen,
                id_native_screen,
                object : NativeListener {
                    override fun nativeAdLoaded(currentNativeAd: NativeAd?) {

                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
                        val adView = layoutInflater.inflate(
                            R.layout.ad_unified_privacy,
                            null
                        ) as NativeAdView
                        adsManager?.nativeAds()
                            ?.nativeViewPolicy(context?:requireContext(),currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                        super.nativeAdLoaded(currentNativeAd)
                    }

                    override fun nativeAdFailed(loadAdError: LoadAdError) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                        super.nativeAdFailed(loadAdError)
                    }

                    override fun nativeAdValidate(string: String) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                        super.nativeAdValidate(string)
                    }
                })
        }*/


    private fun loadBanner() {
        when (type_ad_native_password_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility = View.GONE
            }

            1 -> {
                loadBanner(val_ad_native_password_screen)
            }

            2 -> {

                AdmobNative().loadNativeAds(
                    activity,
                    _binding?.nativeExitAd!!,
                    id_native_screen,
                    if (val_ad_native_password_screen)
                        1 else 0,
                    isAppPurchased = BillingUtil(activity ?: return).checkPurchased(
                        activity ?: return
                    ),
                    isInternetConnected = isNetworkAvailable(activity),
                    nativeType = NativeType.SMALL,
                    nativeCallBack = object : NativeCallBack {
                        override fun onAdFailedToLoad(adError: String) {
                            _binding?.adView?.visibility = View.GONE
                            _binding?.nativeExitAd?.visibility = View.GONE
                        }

                        override fun onAdLoaded() {
                            _binding?.adView?.visibility = View.GONE
                        }

                        override fun onAdImpression() {
                            _binding?.adView?.visibility = View.GONE
                        }
                    }
                )
            }
        }
    }

    private fun loadBanner(isAdsShow: Boolean) {
        _binding?.nativeExitAd?.apply {
            if   (!isAdsShow || !isNetworkAvailable(context)) {
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
        _binding?.adView?.visibility = View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        activity?.finish()
    }
}