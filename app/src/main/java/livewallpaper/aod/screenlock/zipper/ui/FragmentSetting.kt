package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_cam.InterstitialAdManager
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeType
import livewallpaper.aod.screenlock.zipper.ads_cam.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
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
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showSpeedDialogNew
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_password_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentSetting : Fragment() {

    private var isPassword: Boolean = false
private var interstitialAdManager: InterstitialAdManager? = null
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

        if(++PurchaseScreen ==val_inapp_frequency){
            PurchaseScreen =0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        try {
//            adsManager = AdsManager.appAdsInit(activity ?: requireActivity())
            loadBanner()
            isPassword = checkPasswordAct(context) ?: false
            Log.d("fragment_setting", "onViewCreated: $isPassword")
            switchCheck()
            _binding?.selectMusic?.setOnClickListener {
//                showCASInterstitial(val_ad_inter_setting_screen_front){
//                    if(isVisible && !isDetached && isAdded) {
//                        findNavController().navigate(R.id.FragmentSoundSelection)
//                    }
//                }
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: requireActivity(),
                        remoteConfigNormal = val_ad_inter_setting_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "fragment_setting",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ) {
                        if(isVisible && !isDetached && isAdded) {
                            findNavController().navigate(R.id.FragmentSoundSelection)
                        }
                    }
                }
            }
            _binding?.selectSpeed?.setOnClickListener {
                showSpeedDialogNew(
                    activity ?: requireActivity(),
                    onPositiveNoClick = {
                    },
                    onPositiveYesClick = {
                        SavePref(
                            SpeedActivePref,
                            it.toString() + "",
                            context ?: requireContext()
                        )
                    }
                )
            }
            _binding?.topLay?.title?.text = getString(R.string.app_security)
            _binding?.topLay?.backBtn?.clickWithThrottle {
                findNavController().navigateUp()
            }
            _binding?.password?.setOnClickListener {
                if (isPassword) {

                    adsManager?.let {
                        showTwoInterAd(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_setting_screen_front,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "fragment_setting",
                            isBackPress = false,
                            layout = _binding?.adsLay ?: return@setOnClickListener
                        ) {
                            if(isVisible && !isDetached && isAdded) {
                                findNavController().navigate(R.id.FragmentApplyPassword)
                            }
                        }
                    }
                } else {
                    showToast(
                        context ?: requireContext(),
                        getString(R.string.first_enable_password)
                    )
                }
            }

            _binding?.securityQView?.setOnClickListener {

//                showCASInterstitial(val_ad_inter_setting_screen_front){
//                    if(isVisible && !isDetached && isAdded) {
//                        findNavController().navigate(R.id.SecurityQuestionFragment)
//                    }
//                }
               adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: requireActivity(),
                        remoteConfigNormal = val_ad_inter_setting_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "fragment_setting",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ) {
                        if(isVisible && !isDetached && isAdded) {
                            findNavController().navigate(R.id.SecurityQuestionFragment)
                        }
                    }
                }
            }
            setupBackPressedCallback {
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun switchCheck() {
        _binding?.soundSwitch?.setOnCheckedChangeListener { compoundButton, bool ->
            if (compoundButton.isPressed) {
                if (bool) {
                    UCC(
                        bool,
                        ConstantValues.SoActivePref,
                        context ?: requireContext()
                    )
                } else {
                    UCC(
                        bool,
                        ConstantValues.SoActivePref,
                        context ?: requireContext(),
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
                        context ?: requireContext()
                    )
                } else {
                    UCC(
                        bool,
                        ConstantValues.VibratActivePref,
                        context ?: requireContext()
                    )
                }
            }
        }
        _binding?.passwordSwitch?.setOnCheckedChangeListener { compoundButton, bool ->
            if (compoundButton.isPressed) {
                if (bool) {
                    if (!isPassword) {
                        _binding?.passwordSwitch?.isChecked = false

//                        showCASInterstitial(val_ad_inter_password_screen_front){
//                            if(isVisible && !isDetached && isAdded) {
//                                findNavController().navigate(R.id.FragmentApplyPassword)
//                            }
//                        }
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
                        context ?: requireContext()
                    )
                } else {
                    PasswordAdapter.SavePassword(context ?: requireContext(), "")
                    UCC(
                        bool,
                        PasswordSet,
                        context ?: requireContext()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _binding?.passwordSwitch?.isChecked = checkPasswordAct(context ?: requireContext())!!
        Log.d("fragment_setting", "onResume: $isPassword")
        UL(ConstantValues.VibratActivePref, context ?: requireContext(), _binding?.vibrationSwitch)
        UL(ConstantValues.SoActivePref, context ?: requireContext(), _binding?.soundSwitch)
    }

    private fun loadBanner(){
        when (type_ad_native_setting_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                loadBanner(val_ad_native_setting_screen)
            }

            2 -> {


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

        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adView?.visibility=View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

}