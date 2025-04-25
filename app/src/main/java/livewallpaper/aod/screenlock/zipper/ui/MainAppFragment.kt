package livewallpaper.aod.screenlock.zipper.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
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
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.billing.PurchasePrefs
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import livewallpaper.aod.screenlock.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.ads_manager.AdsBanners
import livewallpaper.aod.screenlock.ads_manager.AdsManager
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeType
import livewallpaper.aod.screenlock.ads_manager.loadTwoInterAds
import livewallpaper.aod.screenlock.ads_manager.showTwoInterAd
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.MainMenuAdapter
import livewallpaper.aod.screenlock.zipper.databinding.FragmentMainMenuBinding
import livewallpaper.aod.screenlock.zipper.service.LiveService
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter
import livewallpaper.aod.screenlock.zipper.utilities.CheckBoxUpdater
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.ActivePref
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.StyleSelect
import livewallpaper.aod.screenlock.zipper.utilities.Constants
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.LoadPrefString
import livewallpaper.aod.screenlock.zipper.utilities.IS_NOTIFICATION
import livewallpaper.aod.screenlock.zipper.utilities.LANG_CODE
import livewallpaper.aod.screenlock.zipper.utilities.NOTIFICATION_PERMISSION
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.Uscreen
import livewallpaper.aod.screenlock.zipper.utilities.appUpdateType
import livewallpaper.aod.screenlock.zipper.utilities.askRatings
import livewallpaper.aod.screenlock.zipper.utilities.checkNotificationPermission
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getImageLanguage
import livewallpaper.aod.screenlock.zipper.utilities.getMainMenu
import livewallpaper.aod.screenlock.zipper.utilities.getNativeLayout
import livewallpaper.aod.screenlock.zipper.utilities.home_native
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isFirstEnable
import livewallpaper.aod.screenlock.zipper.utilities.isMainAdsShow
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isRating
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.loadImage
import livewallpaper.aod.screenlock.zipper.utilities.loadImagethumbnail
import livewallpaper.aod.screenlock.zipper.utilities.requestCameraPermissionNotification
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_customize_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_enable_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_list_data_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_main_menu_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.val_collapsable_banner_home
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_inter_front
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency
import livewallpaper.aod.screenlock.zipper.utilities.val_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp


class MainAppFragment : Fragment() {

    private var sharedPrefUtils: DbHelper? = null
    private var _binding: FragmentMainMenuBinding? = null
    private var adsManager: AdsManager? = null
    private var isActivated = false
    private var isFirst = false
    private var shouldCheckForOverlayPermissionLoop = false
    private lateinit var appUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE = 200
    private var isSplashScreen: Boolean = false
    private val admobNative by lazy { AdmobNative() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Uscreen.Init(activity ?: return)
        Log.d("main_fragment", "onViewCreated: calling")
        firebaseAnalytics("main_menu_fragment_open", "main_menu_fragment_open -->  Click")
        isSplash =true
        if(++PurchaseScreen == val_inapp_frequency && !BillingUtil(activity?:return).checkPurchased(activity?:return)){
            PurchaseScreen =0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        arguments?.let {
            isSplashScreen = it.getBoolean("isSplash")
        }
        sharedPrefUtils = DbHelper(context ?: return)
        isFirst = sharedPrefUtils?.chkBroadCast(isFirstEnable) == true
        if(BillingUtil(activity?:return).checkPurchased(activity?:return)){
            _binding?.topLay?.settingBtn?.visibility=View.INVISIBLE
        }else{
            _binding?.topLay?.settingBtn?.visibility=View.VISIBLE
        }
        adsManager = AdsManager.appAdsInit(activity ?: return)
        if(PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp){
            _binding?.topLay?.settingBtn?.visibility=View.GONE
        }else{
            _binding?.topLay?.settingBtn?.visibility=View.VISIBLE
        }
        _binding?.topLay?.settingBtn?.clickWithThrottle {
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
        }
        setupBackPressedCallback {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity ?: return@let,
                    remoteConfigNormal = val_exit_dialog_inter_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "activity_all_style",
                    isBackPress = false,
                    layout = _binding?.adsLay?:return@setupBackPressedCallback,
                ) {
                    findNavController().navigate(R.id.FragmentExitScreen)
                }
            }
        }
        _binding?.topLay?.navMenu?.loadImage(
            context ?: return,
            R.drawable.nav_menu
        )

        if (AppAdapter.IsFirstUse(context ?: return)) {
            AppAdapter.SetFirstUseTrue(context ?: return)
        }
        isActivated = CheckBoxUpdater.UL(
            ActivePref,
            requireContext(),
            _binding?.enableLockSwitch!!
        )
        _binding?.topLay?.navMenu?.clickWithThrottle {
            findNavController().navigate(R.id.FragmentNavigationScreen)
        }
        sharedPrefUtils?.getStringData(requireContext(), LANG_CODE, "en")?.let { getImageLanguage(it) }
            ?.let {
                _binding?.topLay?.languageBtn?.loadImagethumbnail(
                    context ?: return,
                    it
                )
            }

        _binding?.topLay?.languageBtn?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity ?: return@clickWithThrottle,
                    remoteConfigNormal = val_ad_inter_language_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "language_screen",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return@clickWithThrottle,
                ) {
                    findNavController().navigate(R.id.LanguageFragment)
                }
            }
        }
        _binding?.setting?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity?:return@let,
                    remoteConfigNormal = val_ad_inter_setting_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "activity_all_style",
                    isBackPress = false,
                    layout = _binding?.adsLay?:return@clickWithThrottle
                ){
                    findNavController().navigate(R.id.FragmentSetting)
                }
            }
        }
   /*     _binding?.customize?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity?:return@let,
                    remoteConfigNormal = val_ad_inter_customize_screen,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "activity_all_style",
                    isBackPress = false,
                    layout = _binding?.adsLay?:return@clickWithThrottle
                ){
                    findNavController().navigate(R.id.CustomMainFragment)
                }
            }
        }
        _binding?.unWallpaper?.clickWithThrottle {
            showTwoInterAd(
                ads = adsManager?:return@clickWithThrottle,
                activity = activity ?: requireActivity(),
                remoteConfigNormal = val_ad_inter_reward_screen,
                adIdNormal = id_inter_main_medium,
                tagClass = "fragment_reward",
                isBackPress = false,
                layout = _binding?.adsLay ?: return@clickWithThrottle
            ) {
                findNavController().navigate(R.id.WallpaperFragment)
            }
        }
        _binding?.row?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity?:return@let,
                    remoteConfigNormal = val_ad_inter_list_data_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "activity_all_style",
                    isBackPress = false,
                    layout = _binding?.adsLay?:return@clickWithThrottle
                ){
                    findNavController().navigate(R.id.ActivityAllStyle, bundleOf(StyleSelect to  getString(R.string.row_style)))
                }
            }
        }
        _binding?.wallpaper?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity?:return@let,
                    remoteConfigNormal = val_ad_inter_list_data_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "activity_all_style",
                    isBackPress = false,
                    layout = _binding?.adsLay?:return@clickWithThrottle
                ){
                    findNavController().navigate(R.id.ActivityAllStyle, bundleOf(StyleSelect to  getString(R.string.wallpapers)))
                }
            }

        }
        _binding?.preview?.clickWithThrottle {
            if (Settings.canDrawOverlays(
                    activity
                )
            ) {
                LockScreenService.Start(context ?: return@clickWithThrottle)
                return@clickWithThrottle

            } else {
                showCustomDialog()
            }
        }
        _binding?.zipper?.clickWithThrottle {

        }*/

        val adapter = MainMenuAdapter(getMainMenu(context ?: return)) { selectedMenu ->
            // Handle the click event here
            MenuCalling(selectedMenu)
        }
        _binding?.recyclerView?.adapter = adapter

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
        _binding?.enableLock?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity ?: return@clickWithThrottle,
                    remoteConfigNormal = val_ad_inter_enable_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "main_menu",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return@clickWithThrottle,
                ) {
                    findNavController().navigate(R.id.EnableFirstActivity)
                }
            }
        }
        _binding?.enableLockSwitch?.setOnCheckedChangeListener { compoundButton, z ->
            if (compoundButton.isPressed) {
                if (!isFirst) {
                    _binding?.enableLockSwitch?.isChecked = false
                    findNavController().navigate(R.id.EnableFirstActivity)
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
                                        context,
                                        LiveService::class.java
                                    )
                                )
                            }
                        }
                    } else {
                        if (Constants.isMainServiceRunning(requireContext()))
                            Constants.stopServiceCall(
                                activity ?: return@setOnCheckedChangeListener
                            )
                    }
                } else {
                    _binding?.enableLockSwitch?.isChecked = false
                }
            }
        }
        loadNative()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onResume() {
        super.onResume()
        shouldCheckForOverlayPermissionLoop = false
        sharedPrefUtils?.getBooleanData(context ?: return, IS_NOTIFICATION, false)?.let {
            _binding?.enableLockSwitch?.isChecked = it
        }
        checkNotificationPermission(activity)
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
        appUpdateManager = AppUpdateManagerFactory.create(context?:return)
        // Fetch Remote Config and Check for App Update
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            checkForUpdate()
        } else {
            // Avoid starting the intent sender
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        NOTIFICATION_PERMISSION
                    )
                } != 0) {
                requestCameraPermissionNotification(activity ?: return)
            } else {
                if (isRating) askRatings(activity ?: return)
            }
        } else {
            if (isRating) askRatings(activity ?: return)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun loadInstertital() {
        loadTwoInterAds(
            ads = adsManager ?: return,
            activity = activity ?: return,
            remoteConfigNormal = val_inter_main_medium,
            adIdNormal = id_inter_main_medium,
            tagClass = "main_app_fragment"
        )
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadNative() {
        if(isMainAdsShow){
            _binding?.adView?.visibility = View.GONE
            _binding?.nativeExitAd?.visibility = View.GONE
            _binding!!.adViewB.visibility = View.GONE
            _binding!!.bannerAds.visibility = View.GONE
            isMainAdsShow=false
            return
        }
//        if (native_precashe_copunt_current >= native_precashe_counter) {
        val adView = activity?.layoutInflater?.inflate(
            getNativeLayout(
                home_native, _binding?.nativeExitAd!!,
                activity?:return
            ),
            null
        ) as NativeAdView

        admobNative.loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_ad_native_main_menu_screen)
                1 else 0,
            isAppPurchased = BillingUtil(activity?:return).checkPurchased(activity?:return),
            isInternetConnected = AdsManager.isNetworkAvailable(activity),
            nativeType = home_native,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    _binding?.adView?.visibility = View.GONE
                    _binding?.nativeExitAd?.visibility = View.GONE
                }
                override fun onAdLoaded() {
                    _binding?.adView?.visibility = View.GONE}
                override fun onAdImpression() {
                    _binding?.adView?.visibility = View.GONE}
            }
        )
//        } else {
//            adsManager?.nativeAds()?.loadNativeAd(activity ?: return,
//                val_ad_native_main_menu_screen,
//                id_native_screen,
//                object : NativeListener {
//                    override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
//                        if (isAdded && isVisible && !isDetached) {
//                            _binding?.nativeExitAd?.visibility = View.VISIBLE
//                            _binding?.adView?.visibility = View.GONE
//                            val adView =
//                                layoutInflater.inflate(
//                                    R.layout.ad_unified_media,
//                                    null
//                                ) as NativeAdView
//                            adsManager?.nativeAds()
//                                ?.nativeViewMedia(
//                                    context ?: return,
//                                    currentNativeAd ?: return,
//                                    adView
//                                )
//                            _binding?.nativeExitAd?.removeAllViews()
//                            _binding?.nativeExitAd?.addView(adView)
//                        }
//                        super.nativeAdLoaded(currentNativeAd)
//                    }
//
//                    override fun nativeAdFailed(loadAdError: LoadAdError) {
//                        if (isAdded && isVisible && !isDetached) {
//                            _binding?.nativeExitAd?.visibility = View.GONE
//                            _binding?.adView?.visibility = View.GONE
//                        }
//                        super.nativeAdFailed(loadAdError)
//                    }
//
//                    override fun nativeAdValidate(string: String) {
//                        if (isAdded && isVisible && !isDetached) {
//                            _binding?.nativeExitAd?.visibility = View.GONE
//                            _binding?.adView?.visibility = View.GONE
//                        }
//                        super.nativeAdValidate(string)
//                    }
//                })
//        }

        if(val_collapsable_banner){
            AdsBanners.loadCollapsibleBanner(
                activity?:return,
                _binding?.bannerAds!!,
                val_collapsable_banner_home,
                id_collapsable_banner
            ){
                _binding!!.adViewB.visibility = View.GONE
            }
        }else{
            adsManager?.adsBanners()?.loadBanner(
                activity = activity ?: return,
                view = _binding!!.bannerAds,
                addConfig = val_collapsable_banner_home,
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
//                showOpenAd(activity ?: return)
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
        Glide.with(this)
            .load(R.raw.dialog)
            .into(dialog.findViewById<ImageView>(R.id.animationView))

        dialog.window?.apply {
            // Set the dialog to be full-screen
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))  // Optional: Set background to transparent
        }
        dialog.findViewById<View>(R.id.buttonOk).clickWithThrottle {
            dialog.dismiss()
            askPermission(activity ?: return@clickWithThrottle)
        }

        dialog.findViewById<View>(R.id.closeBtn).clickWithThrottle {
            dialog.dismiss()

        }
    }

    private fun askPermission(activity: Activity) {
//        val intent = Intent(
//            "android.settings.action.MANAGE_OVERLAY_PERMISSION",
//            Uri.parse("package:" + activity.packageName)
//        )
//        shouldCheckForOverlayPermissionLoop = true
//        startCheckingOverlayPermission()
//        activity.startActivity(intent)
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context?.packageName}")
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
                sharedPrefUtils?.setBroadCast(isFirstEnable, true)
                DataBasePref.SavePref(ActivePref, "1", context ?: return)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        context ?: return, Intent(context ?: return, LiveService::class.java)
                    )
                } else {
                    context?.startService(
                        Intent(
                            context ?: return, LiveService::class.java
                        )
                    )
                }
                _binding?.enableLockSwitch?.isChecked = true
            } else {
                // Permission denied, handle accordingly
                // You may show a message to the user or take appropriate action
            }
        }
        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK) {
            // Handle update failure (e.g., user canceled the update)
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
        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                ) {
                    when (appUpdateType){
                        0->{
                            // Request the update
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                activity?:return@addOnSuccessListener,
                                RC_APP_UPDATE
                            )
                        }
                        1->{
                            // Request the update
                            appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.FLEXIBLE,
                                activity?:return@addOnSuccessListener,
                                RC_APP_UPDATE
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return
    }

    private fun MenuCalling(position: Int) {
        when (position) {
            0 -> {
//                showCASInterstitial(val_ad_inter_list_data_screen_front) {
//                    findNavController().navigate(
//                        R.id.ActivityAllStyle,
//                        bundleOf(StyleSelect to getString(R.string.zipperStyle))
//                    )
//                }
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity?:return@let,
                        remoteConfigNormal = val_ad_inter_list_data_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "activity_all_style",
                        isBackPress = false,
                        layout = _binding?.adsLay?:return@let
                    ){
                        findNavController().navigate(R.id.ActivityAllStyle, bundleOf(StyleSelect to  getString(R.string.zipperStyle)))
                    }
                }
            }

            1 -> {
//                showCASInterstitial(val_ad_inter_list_data_screen_front) {
//                    findNavController().navigate(
//                        R.id.ActivityAllStyle,
//                        bundleOf(StyleSelect to getString(R.string.row_style))
//                    )
//                }
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity?:return@let,
                        remoteConfigNormal = val_ad_inter_list_data_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "activity_all_style",
                        isBackPress = false,
                        layout = _binding?.adsLay?:return
                    ){
                        findNavController().navigate(R.id.ActivityAllStyle, bundleOf(StyleSelect to  getString(R.string.row_style)))
                    }
                }
            }

            2 -> {
//                showCASInterstitial(val_ad_inter_list_data_screen_front) {
//                    findNavController().navigate(
//                        R.id.ActivityAllStyle,
//                        bundleOf(StyleSelect to getString(R.string.wallpapers))
//                    )
//                }
                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity?:return@let,
                        remoteConfigNormal = val_ad_inter_list_data_screen_front,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "activity_all_style",
                        isBackPress = false,
                        layout = _binding?.adsLay?:return
                    ){
                        findNavController().navigate(R.id.ActivityAllStyle, bundleOf(StyleSelect to  getString(R.string.wallpapers)))
                    }
                }
            }

            3 -> {
                if (Settings.canDrawOverlays(
                        activity
                    )
                ) {
                    LockScreenService.Start(context ?: return)
                    return

                } else {
                    showCustomDialog()
                }
            }

            4 -> {
//                showCASInterstitial(val_ad_inter_customize_screen) {
//                    findNavController().navigate(R.id.CustomMainFragment)
//                }

                adsManager?.let {
                    showTwoInterAd(
                        ads = it,
                        activity = activity?:return@let,
                        remoteConfigNormal = val_ad_inter_customize_screen,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "activity_all_style",
                        isBackPress = false,
                        layout = _binding?.adsLay?:return@let
                    ){
                        findNavController().navigate(R.id.CustomMainFragment)
                    }
                }
            }

            5 -> {
//                showCASInterstitial(val_ad_inter_reward_screen) {
//                    findNavController().navigate(R.id.rewardFragment)
//                }
                showTwoInterAd(
                    ads = adsManager?:return,
                    activity = activity ?: requireActivity(),
                    remoteConfigNormal = val_ad_inter_reward_screen,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "fragment_reward",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return
                ) {
                    findNavController().navigate(R.id.rewardFragment)
                }
            }
        }
    }

}
/*
    Fragment() {

    private var sharedPrefUtils: DbHelper? = null
    var _binding: FragmentMainMenuBinding? = null
    private var isActivated = false
    private var isFirst = false
    private var shouldCheckForOverlayPermissionLoop = false
    private lateinit var appUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE = 200
    private var isSplashScreen: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Uscreen.Init(activity ?: return)
        Log.d("main_fragment", "onViewCreated: calling")
        firebaseAnalytics("main_menu_fragment_open", "main_menu_fragment_open -->  Click")
        isSplash = true
        if (++PurchaseScreen == val_inapp_frequency && !BillingUtil(
                activity ?: return
            ).checkPurchased(activity ?: return)
        ) {
            PurchaseScreen = 0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        arguments?.let {
            isSplashScreen = it.getBoolean("is_splash")
        }
        sharedPrefUtils = DbHelper(context ?: return)
        isFirst = sharedPrefUtils?.chkBroadCast(isFirstEnable) == true
        if (BillingUtil(activity ?: return).checkPurchased(activity ?: return)) {
            _binding?.topLay?.settingBtn?.visibility = View.INVISIBLE
        } else {
            _binding?.topLay?.settingBtn?.visibility = View.VISIBLE
        }
        if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp) {
            _binding?.topLay?.settingBtn?.visibility = View.GONE
        } else {
            _binding?.topLay?.settingBtn?.visibility = View.VISIBLE
        }
        _binding?.topLay?.settingBtn?.clickWithThrottle {
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
        }
        setupBackPressedCallback {
            showCASInterstitial(val_exit_dialog_inter_front) {
                findNavController().navigate(R.id.FragmentExitScreen)
            }
        }
        _binding?.topLay?.navMenu?.loadImage(
            context ?: return,
            R.drawable.nav_menu
        )

        if (AppAdapter.IsFirstUse(context ?: return)) {
            AppAdapter.SetFirstUseTrue(context ?: return)
        }
        isActivated = CheckBoxUpdater.UL(
            ActivePref,
            requireContext(),
            _binding?.enableLockSwitch!!
        )
        _binding?.topLay?.navMenu?.clickWithThrottle {
            findNavController().navigate(R.id.FragmentNavigationScreen)
        }
        sharedPrefUtils?.getStringData(requireContext(), LANG_CODE, "en")
            ?.let { getImageLanguage(it) }
            ?.let {
                _binding?.topLay?.languageBtn?.loadImagethumbnail(
                    context ?: return,
                    it
                )
            }

        _binding?.topLay?.languageBtn?.clickWithThrottle {

            showCASInterstitial(val_ad_inter_language_screen_front) {
                findNavController().navigate(R.id.LanguageFragment)
            }
            */
/*            adsManager?.let {
                            showTwoInterAd(
                                ads = it,
                                activity = activity ?: return@clickWithThrottle,
                                remoteConfigNormal = val_ad_inter_language_screen_front,
                                adIdNormal = id_inter_main_medium,
                                tagClass = "language_screen",
                                isBackPress = false,
                                layout = _binding?.adsLay ?: return@clickWithThrottle,
                            ) {
                                findNavController().navigate(R.id.LanguageFragment)
                            }
                        }*//*

        }

        _binding?.setting?.clickWithThrottle {
            showCASInterstitial(val_ad_inter_setting_screen_front) {
                findNavController().navigate(R.id.FragmentSetting)
            }
            */
/*         adsManager?.let {
                         showTwoInterAd(
                             ads = it,
                             activity = activity ?: return@let,
                             remoteConfigNormal = val_ad_inter_setting_screen_front,
                             adIdNormal = id_inter_main_medium,
                             tagClass = "activity_all_style",
                             isBackPress = false,
                             layout = _binding?.adsLay ?: return@clickWithThrottle
                         ) {
                             findNavController().navigate(R.id.FragmentSetting)
                         }
                     }*//*

        }

        val adapter = MainMenuAdapter(getMainMenu(context ?: return)) { selectedMenu ->
            // Handle the click event here
            MenuCalling(selectedMenu)
        }

        _binding?.recyclerView?.adapter = adapter

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
        _binding?.enableLock?.clickWithThrottle {

            showCASInterstitial(val_ad_inter_enable_screen_front) {
                findNavController().navigate(R.id.EnableFirstActivity)
            }
            */
/*           adsManager?.let {
                           showTwoInterAd(
                               ads = it,
                               activity = activity ?: return@clickWithThrottle,
                               remoteConfigNormal = val_ad_inter_enable_screen_front,
                               adIdNormal = id_inter_main_medium,
                               tagClass = "main_menu",
                               isBackPress = false,
                               layout = _binding?.adsLay ?: return@clickWithThrottle,
                           ) {
                               findNavController().navigate(R.id.EnableFirstActivity)
                           }
                       }*//*

        }
        _binding?.enableLockSwitch?.setOnCheckedChangeListener { compoundButton, z ->
            if (compoundButton.isPressed) {
                if (!isFirst) {
                    _binding?.enableLockSwitch?.isChecked = false
                    findNavController().navigate(R.id.EnableFirstActivity)
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
                                        context,
                                        LiveService::class.java
                                    )
                                )
                            }
                        }
                    } else {
                        if (Constants.isMainServiceRunning(requireContext()))
                            Constants.stopServiceCall(
                                activity ?: return@setOnCheckedChangeListener
                            )
                    }
                } else {
                    _binding?.enableLockSwitch?.isChecked = false
                }
            }
        }
        loadNative()
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
        sharedPrefUtils?.getBooleanData(context ?: return, IS_NOTIFICATION, false)?.let {
            _binding?.enableLockSwitch?.isChecked = it
        }
        checkNotificationPermission(activity)
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
        loadCASInterstitial(true)
        // Initialize AppUpdateManager
        appUpdateManager = AppUpdateManagerFactory.create(context ?: return)
        // Fetch Remote Config and Check for App Update
        checkForUpdate()
    }

    private fun loadNative() {
        AdmobNative().loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_ad_native_main_menu_screen)
                1 else 0,
            isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
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
        loadBanner(!val_collapsable_banner)


    }

    private fun loadBanner(isAdsShow: Boolean) {
        _binding?.bannerAds?.apply {
            if (!isAdsShow || !isNetworkAvailable(context)) {
                visibility = View.INVISIBLE
                _binding?.adViewB?.visibility = View.INVISIBLE
                return
            }
        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adViewB?.visibility = View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroy() {
        shouldCheckForOverlayPermissionLoop = false
        super.onDestroy()
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
//                showOpenAd(activity ?: return)
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

    fun showCustomDialog() {
        val dialog = Dialog(context ?: return)
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
        dialog.findViewById<View>(R.id.buttonOk).clickWithThrottle {
            dialog.dismiss()
            askPermission(activity ?: return@clickWithThrottle)
        }

        dialog.findViewById<View>(R.id.closeBtn).clickWithThrottle {
            dialog.dismiss()

        }
    }

    private fun askPermission(activity: Activity) {

        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context?.packageName}")
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
                sharedPrefUtils?.setBroadCast(isFirstEnable, true)
                DataBasePref.SavePref(ActivePref, "1", context ?: return)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(
                        context ?: return, Intent(context ?: return, LiveService::class.java)
                    )
                } else {
                    context?.startService(
                        Intent(
                            context ?: return, LiveService::class.java
                        )
                    )
                }
                _binding?.enableLockSwitch?.isChecked = true
            } else {
                // Permission denied, handle accordingly
                // You may show a message to the user or take appropriate action
            }
        }
        if (requestCode == RC_APP_UPDATE && resultCode != RESULT_OK) {
            // Handle update failure (e.g., user canceled the update)
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
        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                ) {
                    when (appUpdateType) {
                        0 -> {
                            // Request the update
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    activity ?: return@addOnSuccessListener,
                                    RC_APP_UPDATE
                                )
                            } catch (e: Exception) {
                               e.printStackTrace()
                            }
                        }

                        1 -> {
                            // Request the update
                            try {
                                appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.FLEXIBLE,
                                    activity ?: return@addOnSuccessListener,
                                    RC_APP_UPDATE
                                )
                            } catch (e: Exception) {
                               e.printStackTrace()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun MenuCalling(position: Int) {
        when (position) {
            0 -> {
                showCASInterstitial(val_ad_inter_list_data_screen_front) {
                    findNavController().navigate(
                        R.id.ActivityAllStyle,
                        bundleOf(StyleSelect to getString(R.string.zipperStyle))
                    )
                }
            }

            1 -> {
                showCASInterstitial(val_ad_inter_list_data_screen_front) {
                    findNavController().navigate(
                        R.id.ActivityAllStyle,
                        bundleOf(StyleSelect to getString(R.string.row_style))
                    )
                }
            }

            2 -> {
                showCASInterstitial(val_ad_inter_list_data_screen_front) {
                    findNavController().navigate(
                        R.id.ActivityAllStyle,
                        bundleOf(StyleSelect to getString(R.string.wallpapers))
                    )
                }
            }

            3 -> {
                if (Settings.canDrawOverlays(
                        activity
                    )
                ) {
                    LockScreenService.Start(context ?: return)
                    return

                } else {
                    showCustomDialog()
                }
            }

            4 -> {
                showCASInterstitial(val_ad_inter_customize_screen) {
                    findNavController().navigate(R.id.CustomMainFragment)
                }
            }

            5 -> {
                showCASInterstitial(val_ad_inter_reward_screen) {
                    findNavController().navigate(R.id.rewardFragment)
                }
            }
        }
    }

}*/
