package livewallpaper.aod.screenlock.zipper.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeType
import livewallpaper.aod.screenlock.zipper.ads_cam.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
import livewallpaper.aod.screenlock.zipper.databinding.EnableFirstActivityBinding
import livewallpaper.aod.screenlock.zipper.service.LiveService
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.ActivePref
import livewallpaper.aod.screenlock.zipper.utilities.Constants
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isFirstEnable
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class EnableFirstActivity : Fragment() {

    private var _binding: EnableFirstActivityBinding? = null
    private var isFirst = false
    private var sharedPrefUtils: DbHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = EnableFirstActivityBinding.inflate(layoutInflater)
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
        sharedPrefUtils = DbHelper(requireContext())
        loadBanner()
        isFirst = sharedPrefUtils?.chkPass(isFirstEnable) == true
        _binding?.enableLockSwitch?.setOnCheckedChangeListener { compoundButton, z ->
            if (compoundButton.isPressed) {
                if (checkPermissionOverlay(activity ?: return@setOnCheckedChangeListener)) {
                    if (z) {
                        if (!Constants.isMainServiceRunning(
                                context ?: requireContext()
                            )
                        ) {
                            sharedPrefUtils?.setBroadCast(isFirstEnable, true)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(
                                    context ?: requireContext(),
                                    Intent(context ?: requireContext(), LiveService::class.java)
                                )
                            } else {
                                context?.startService(
                                    Intent(
                                        context ?: requireContext(), LiveService::class.java
                                    )
                                )
                            }
                        }
                    } else {
                        if (Constants.isMainServiceRunning(
                                context ?: requireContext()
                            )
                        ) Constants.stopServiceCall(
                            activity ?: requireActivity()
                        )
                    }
                } else {
                    _binding?.enableLockSwitch?.isChecked = false
                }
            }
        }
        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        _binding?.topLay?.title?.text = getString(R.string.enable_lockscreen)

    }


    private fun checkPermissionOverlay(activity: Activity): Boolean {
        return try {
            if (Settings.canDrawOverlays(activity)) {
                return true
            }
            showCustomDialog()
            false
        } catch (unused: Exception) {
            true
        }
    }

    /*   private fun loadNative() {

   *//*        adsManager?.let {
            showTwoInterAdFirst(
                ads = it,
                activity = activity ?: requireActivity(),
                remoteConfigMedium = val_ad_inter_enable_screen_front,
                remoteConfigNormal = val_ad_inter_enable_screen_front,
                adIdMedium = id_inter_main_medium,
                adIdNormal = id_inter_main_medium,
                tagClass = "enable_first_time",
                isBackPress = false,
                layout = _binding?.adsLay ?: return@let,
            ) {

            }
        }*//*

        adsManager?.nativeAds()?.loadNativeAd(activity ?: return,
            val_ad_native_enable_screen,
            id_native_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    _binding?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.adView?.visibility = View.GONE
                    val adView =
                        layoutInflater.inflate(R.layout.ad_unified_privacy, null) as NativeAdView
                    adsManager?.nativeAds()
                        ?.nativeViewPolicy(
                            context ?: requireContext(),
                            currentNativeAd ?: return,
                            adView
                        )
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
        when (type_ad_native_enable_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility = View.GONE
            }

            1 -> {
                loadBanner(val_ad_native_enable_screen)
            }

            2 -> {
                AdmobNative().loadNativeAds(
                    activity,
                    _binding?.nativeExitAd!!,
                    id_native_screen,
                    if (val_ad_native_enable_screen)
                        1 else 0,
                    isAppPurchased = BillingUtil(activity ?: return).checkPurchased(
                        activity ?: return
                    ),
                    isInternetConnected = isNetworkAvailable(activity),
                    nativeType = NativeType.LARGE,
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
         
        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adView?.visibility = View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun showCustomDialog() {
        val dialog = Dialog(context ?: requireContext())
        dialog.requestWindowFeature(1)
        dialog.setContentView(R.layout.permission_dialog)
        dialog.show()
        Glide.with(this)
            .load(R.raw.dialog)
            .into(dialog.findViewById<ImageView>(R.id.animationView))
        dialog.window?.apply {
            // Set the dialog to be full-screen
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))  // Optional: Set background to transparent
        }
        dialog.findViewById<View>(R.id.buttonOk).setOnClickListener {
            dialog.dismiss()
            askPermission()
        }
        dialog.findViewById<View>(R.id.closeBtn).setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context?.packageName}")
        )
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            // Check if the user granted the overlay permission
            if (isOverlayPermissionGranted()) {
                sharedPrefUtils?.setBroadCast(isFirstEnable, true)
                _binding?.enableLockSwitch?.isChecked = true
                DataBasePref.SavePref(ActivePref, "1", context ?: requireContext())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(
                        context ?: requireContext(),
                        Intent(context ?: requireContext(), LiveService::class.java)
                    )
                } else {
                    context?.startService(
                        Intent(
                            context ?: requireContext(), LiveService::class.java
                        )
                    )
                }
//                showOpenAd(activity ?: requireActivity())
            } else {
                // Permission denied, handle accordingly
                // You may show a message to the user or take appropriate action
            }
        }
    }

    private fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity?.applicationContext)
        } else {
            true // On pre-M devices, overlay permission is not required
        }
    }


    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        if (i == 123) {
            if (iArr.isEmpty() || iArr[0] != 0) {
                val str = strArr[0]
                Toast.makeText(
                    context ?: return,
                    "You need to give permission in order to preview",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
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