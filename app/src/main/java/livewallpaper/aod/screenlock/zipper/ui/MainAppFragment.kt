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
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import livewallpaper.aod.screenlock.zipper.MainActivity.Companion.background
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsBanners
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.PurchasePrefs
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.loadTwoInterAds
import livewallpaper.aod.screenlock.zipper.ads_manager.showTwoInterAd
import livewallpaper.aod.screenlock.zipper.databinding.FragmentMainMenuBinding
import livewallpaper.aod.screenlock.zipper.service.LiveService
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter
import livewallpaper.aod.screenlock.zipper.utilities.CheckBoxUpdater
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.ActivePref
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.StyleSelect
import livewallpaper.aod.screenlock.zipper.utilities.Constants
import livewallpaper.aod.screenlock.zipper.utilities.Constants.isServiceRunning
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.LoadPrefString
import livewallpaper.aod.screenlock.zipper.utilities.NOTIFICATION_PERMISSION
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.Uscreen
import livewallpaper.aod.screenlock.zipper.utilities.appUpdateType
import livewallpaper.aod.screenlock.zipper.utilities.askRatings
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isFirstEnable
import livewallpaper.aod.screenlock.zipper.utilities.isRating
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.loadImage
import livewallpaper.aod.screenlock.zipper.utilities.loadImagethumbnail
import livewallpaper.aod.screenlock.zipper.utilities.requestCameraPermissionNotification
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showServiceDialog
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_enable_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_list_data_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_main_menu_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_inter_front
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp


class MainAppFragment : Fragment() {

    private var sharedPrefUtils: DbHelper? = null
    private var _binding: FragmentMainMenuBinding? = null
    private var adsManager: AdsManager? = null
    private var isActivated = false
    private var isFirst = false
    private var shouldCheckForOverlayPermissionLoop = false
    private var isSplashScreen: Boolean = false
    private lateinit var appUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE = 200

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(layoutInflater)
        Log.d("calling", "onCreateView: load main fragment")
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            isSplash = true
        sharedPrefUtils = DbHelper(context ?: return)
            if (++PurchaseScreen == val_inapp_frequency) {
                PurchaseScreen = 0
                findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
                return
            }
            Uscreen.Init(activity ?: return)
            arguments?.let {
                isSplashScreen = it.getBoolean("is_splash")
            }
            Log.d("main_fragment", "onViewCreated: calling")
            firebaseAnalytics("main_menu_fragment_open", "main_menu_fragment_open -->  Click")

            adsManager = AdsManager.appAdsInit(activity ?: return)
            if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp) {
                _binding?.topLay?.settingBtn?.visibility = View.GONE
            } else {
                _binding?.topLay?.settingBtn?.visibility = View.VISIBLE
            }

            _binding?.topLay?.settingBtn?.clickWithThrottle {
                findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            }

                loadNative()
                _binding?.mainbg?.setBackgroundResource(background)
                setupBackPressedCallback {
                    adsManager?.let {
                        showTwoInterAd(
                            ads = it,
                            activity = activity ?: return@setupBackPressedCallback,
                            remoteConfigNormal = val_exit_dialog_inter_front,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "main_menu",
                            isBackPress = false,
                            layout = _binding?.adsLay ?: return@setupBackPressedCallback,
                        ) {
                            findNavController().navigate(R.id.FragmentExitScreen)
                        }
                    }
                }
                _binding?.topLay?.navMenu?.loadImage(
                    context ?: return, R.drawable.nav_menu
                )
                _binding?.zipperImage?.loadImagethumbnail(
                    context ?: return, R.drawable.zipper1
                )
                _binding?.rowImage?.loadImagethumbnail(
                    context ?: return, R.drawable.row1
                )
                _binding?.wallpaperImage?.loadImagethumbnail(
                    context ?: return, R.drawable.bg_7
                )

                if (AppAdapter.IsFirstUse(context ?: return)) {
                    AppAdapter.SetFirstUseTrue(context ?: return)
                }
                isActivated = CheckBoxUpdater.UL(
                    ActivePref, requireContext(), _binding?.enableLockSwitch!!
                )
            _binding?.topLay?.navMenu?.setOnClickListener {
                findNavController().navigate(R.id.FragmentNavigationScreen)
            }
            _binding?.topLay?.languageBtn?.setOnClickListener {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@setOnClickListener,
                        remoteConfigNormal = val_ad_inter_language_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "language_screen",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener,
                    ) {
                        findNavController().navigate(R.id.LanguageFragment)
                    }
                }
            }
            _binding?.row?.setOnClickListener {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@let,
                        remoteConfigNormal = val_ad_inter_list_data_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "activity_all_style",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ) {
                        findNavController().navigate(
                            R.id.ActivityAllStyle,
                            bundleOf(StyleSelect to getString(R.string.row_style))
                        )
                    }
                }

            }
            _binding?.wallpaper?.setOnClickListener {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@setOnClickListener,
                        remoteConfigNormal = val_ad_inter_list_data_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "main_menu",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ) {
                        findNavController().navigate(
                            R.id.ActivityAllStyle,
                            bundleOf(StyleSelect to getString(R.string.wallpapers))
                        )
                    }
                }

            }
            _binding?.zipper?.setOnClickListener {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@setOnClickListener,
                        remoteConfigNormal = val_ad_inter_list_data_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "main_menu",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ) {
                        findNavController().navigate(
                            R.id.ActivityAllStyle,
                            bundleOf(StyleSelect to getString(R.string.zipperStyle))
                        )
                    }
                }


            }
            _binding?.preview?.setOnClickListener {
                if (Settings.canDrawOverlays(
                        activity
                    )
                ) {
                    LockScreenService.Start(context ?: return@setOnClickListener)
                    return@setOnClickListener

                } else {
                    showCustomDialog()
                }
            }
            _binding?.setting?.setOnClickListener {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@setOnClickListener,
                        remoteConfigNormal = val_ad_inter_setting_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "main_menu",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener
                    ) {
                        findNavController().navigate(R.id.FragmentSetting)
                    }
                }
            }
            if (!isFirst) {
                _binding?.enableLockSwitch?.visibility = View.INVISIBLE
                _binding?.enableLockArrow?.visibility = View.VISIBLE
                _binding?.enableLock?.isClickable = true
            } else {
                _binding?.enableLockSwitch?.visibility = View.VISIBLE
                _binding?.enableLockArrow?.visibility = View.INVISIBLE
                _binding?.enableLock?.isClickable = false
            }
            _binding?.enableLock?.setOnClickListener {
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity ?: return@setOnClickListener,
                        remoteConfigNormal = val_ad_inter_enable_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "main_menu",
                        isBackPress = false,
                        layout = _binding?.adsLay ?: return@setOnClickListener,
                    ) {
                        findNavController().navigate(R.id.EnableFirstActivity)
                    }
                }
            }
            _binding?.enableLockSwitch?.setOnCheckedChangeListener { compoundButton, z ->
                if (compoundButton.isPressed) {
                    if (!isFirst) {
                        _binding?.enableLockSwitch?.isChecked = false
                        adsManager?.let {
                            showTwoInterAd(
                                ads = it,
                                activity = activity ?: return@setOnCheckedChangeListener,
                                remoteConfigNormal = val_ad_inter_enable_screen_front,
                                adIdNormal = id_inter_main_medium,
                                tagClass = "main_menu",
                                isBackPress = false,
                                layout = _binding?.adsLay ?: return@setOnCheckedChangeListener,
                            ) {
                                findNavController().navigate(R.id.EnableFirstActivity)
                            }
                        }
                        return@setOnCheckedChangeListener
                    }
                    if (checkPermissionOverlay(activity ?: return@setOnCheckedChangeListener)) {
                        isActivated = CheckBoxUpdater.UC(
                            _binding?.enableLockSwitch ?: return@setOnCheckedChangeListener,
                            isActivated,
                            ActivePref,
                            context ?: return@setOnCheckedChangeListener,
                            true,
                            null
                        )
                        if (isActivated) {
                            if (!Constants.isMainServiceRunning(
                                    context ?: return@setOnCheckedChangeListener
                                )
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(
                                        requireContext(),
                                        Intent(requireContext(), LiveService::class.java)
                                    )
                                } else {
                                    requireContext().startService(
                                        Intent(
                                            context, LiveService::class.java
                                        )
                                    )
                                }
                            }
                        } else {
                            _binding?.enableLockSwitch?.isChecked = true
                            showServiceDialog(onPositiveNoClick = {
                                _binding?.enableLockSwitch?.isChecked = true
                            }, onPositiveYesClick = {
                                _binding?.enableLockSwitch?.isChecked = false
                                if (isServiceRunning()) {
                                    Constants.stopServiceCall(
                                        activity ?: return@showServiceDialog
                                    )
                                }
                            })
                        }
                    } else {
                        _binding?.enableLockSwitch?.isChecked = false
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context?.let {
                        ContextCompat.checkSelfPermission(
                            it,
                            NOTIFICATION_PERMISSION
                        )
                    } != 0) {
//                requestCameraPermissionNotification(activity ?: return)
                } else {
                    if (isRating) askRatings(activity ?: return)
                }
            } else {
                if (isRating) askRatings(activity ?: return)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onResume() {
        super.onResume()
            shouldCheckForOverlayPermissionLoop = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (context?.let {
                        ContextCompat.checkSelfPermission(
                            it,
                            NOTIFICATION_PERMISSION
                        )
                    } != 0) {
                    requestCameraPermissionNotification(activity ?: return)
                }
            }
            isFirst = sharedPrefUtils?.chkBroadCast(isFirstEnable) == true
                if (isFirst) {
                    _binding?.enableLock?.isClickable = false
                    _binding?.enableLock?.isActivated = false
                    _binding?.enableLock?.isEnabled = false
                    _binding?.enableLockArrow?.visibility = View.INVISIBLE
                    _binding?.enableLockSwitch?.visibility = View.VISIBLE
                    _binding?.enableLockSwitch?.isChecked =
                        LoadPrefString(ActivePref, context ?: return).equals("1")
                } else {
                    _binding?.enableLock?.isClickable = true
                    _binding?.enableLock?.isActivated = true
                    _binding?.enableLock?.isEnabled = true
                    _binding?.enableLockArrow?.visibility = View.VISIBLE
                    _binding?.enableLockSwitch?.visibility = View.INVISIBLE
                    _binding?.enableLockSwitch?.isChecked = false
                }
                loadInstertital()
            // Initialize AppUpdateManager
            appUpdateManager = AppUpdateManagerFactory.create(context ?: return)
            // Fetch Remote Config and Check for App Update
            checkForUpdate()
    }

    private fun loadInstertital() {
        loadTwoInterAds(
            ads = adsManager ?: return,
            activity = activity ?: return,
            remoteConfigNormal = true,
            adIdNormal = id_inter_main_medium,
            tagClass = "main_app_fragment"
        )
    }

    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(activity ?: return,
            val_ad_native_main_menu_screen,
            id_native_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
                        val adView = layoutInflater.inflate(
                            R.layout.ad_unified_privacy, null
                        ) as NativeAdView
                        adsManager?.nativeAds()
                            ?.nativeViewPolicy(context ?: return, currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.GONE
                        _binding?.adView?.visibility = View.GONE
                    }
                    super.nativeAdValidate(string)
                }
            })
        if (val_collapsable_banner) {
            AdsBanners.loadCollapsibleBanner(
                activity ?: return,
                _binding?.bannerAds!!,
                _binding?.adViewB!!,
                true,
                id_collapsable_banner
            )
        } else {
            adsManager?.adsBanners()?.loadBanner(
                activity = activity ?: return,
                view = _binding!!.bannerAds,
                addConfig = true,
                bannerId = id_adaptive_banner
            ) {
                _binding!!.adViewB.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        shouldCheckForOverlayPermissionLoop = false
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
            } else {
//                if (!isFirst) {
//                    showOpenAd(activity ?: return)
//                }
            }
        }
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

    private fun showCustomDialog() {
        val dialog = Dialog(context ?: return)
        dialog.requestWindowFeature(1)
        dialog.setContentView(R.layout.permission_dialog)
        dialog.show()
        Glide.with(this).load(R.raw.dialog).into(dialog.findViewById<ImageView>(R.id.animationView))
        dialog.window?.apply {
            // Set the dialog to be full-screen
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))  // Optional: Set background to transparent
        }
        dialog.findViewById<View>(R.id.buttonOk).setOnClickListener {
            dialog.dismiss()
            askPermission(activity ?: return@setOnClickListener)
        }
        dialog.findViewById<View>(R.id.closeBtn).setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun askPermission(activity: Activity) {
        try {
            isSplash = false
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context?.packageName}")
            )
            startActivityForResult(intent, 100)
        } catch (e: Exception) {
           e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            // Check if the user granted the overlay permission
            if (isOverlayPermissionGranted()) {
//                sharedPrefUtils?.setBroadCast(isFirstEnable, true)
                DataBasePref.SavePref(ActivePref, "1", context ?: return)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(
//                        context ?: return, Intent(context ?: return, LiveService::class.java)
//                    )
//                } else {
//                    context?.startService(
//                        Intent(
//                            context ?: return, LiveService::class.java
//                        )
//                    )
//                }
//                _binding?.enableLockSwitch?.isChecked = true
//                _binding?.navView?.customSwitch?.isChecked = true
                if (!isFirst) {
//                    showOpenAd(activity ?: return)
                    isSplash = true
                }
            } else {
                // Permission denied, handle accordingly
                // You may show a message to the user or take appropriate action
            }
        }
    }

    private fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true // On pre-M devices, overlay permission is not required
        }
    }

    private fun checkForUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                when (appUpdateType) {
                    0 -> {
                        // Request the update
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity ?: return@addOnSuccessListener,
                            RC_APP_UPDATE
                        )
                    }

                    1 -> {
                        // Request the update
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            activity ?: return@addOnSuccessListener,
                            RC_APP_UPDATE
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}