package livewallpaper.aod.screenlock.zipper.ui

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdOpenApp
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdsManager
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.CmpClass
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.BillingUtil
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.PurchasePrefs
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.loadTwoInterAds
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.loadTwoInterAdsSplash
import com.google.common.reflect.TypeToken
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.hypersoft.billing.BillingManager
import com.hypersoft.billing.dataClasses.PurchaseDetail
import com.hypersoft.billing.interfaces.BillingListener
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.ads_manager.NativeDesignType
import livewallpaper.aod.screenlock.zipper.BuildConfig
import livewallpaper.aod.screenlock.zipper.BuildConfig.DEBUG
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.FragmentSplashBinding
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter.SaveWallpaper
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SpeedActivePref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.LoadPref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.SavePref
import livewallpaper.aod.screenlock.zipper.utilities.LANG_CODE
import livewallpaper.aod.screenlock.zipper.utilities.Wallpaper_Cat
import livewallpaper.aod.screenlock.zipper.utilities.apiKey
import livewallpaper.aod.screenlock.zipper.utilities.appUpdateType
import livewallpaper.aod.screenlock.zipper.utilities.banner_height
import livewallpaper.aod.screenlock.zipper.utilities.banner_type
import livewallpaper.aod.screenlock.zipper.utilities.counter
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_bg
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_button
import livewallpaper.aod.screenlock.zipper.utilities.id_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_frequency_counter
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_counter
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_splash_Screen
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_watch_ads_Screen
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.home_native
import livewallpaper.aod.screenlock.zipper.utilities.custtom_main
import livewallpaper.aod.screenlock.zipper.utilities.wallpaper_fragment
import livewallpaper.aod.screenlock.zipper.utilities.theme_all
import livewallpaper.aod.screenlock.zipper.utilities.enable_first
import livewallpaper.aod.screenlock.zipper.utilities.apply_password
import livewallpaper.aod.screenlock.zipper.utilities.setting_fragment
import livewallpaper.aod.screenlock.zipper.utilities.sound_select
import livewallpaper.aod.screenlock.zipper.utilities.security_question
import livewallpaper.aod.screenlock.zipper.utilities.id_splash_native
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isFlowOne
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isRating
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.is_val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.language_bottom
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_counter
import livewallpaper.aod.screenlock.zipper.utilities.on_bord_native
import livewallpaper.aod.screenlock.zipper.utilities.sessionOnboarding
import livewallpaper.aod.screenlock.zipper.utilities.sessionOpenLanguageNew
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.splash_bottom
import livewallpaper.aod.screenlock.zipper.utilities.thankyou_bottom
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_splash_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_customize_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_enable_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_list_data_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_main_menu_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_on_board
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_password_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_sound_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_wallpaper_server_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_watch_ads_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_customize_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_intro_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_main_menu_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_app_open
import livewallpaper.aod.screenlock.zipper.utilities.val_banner_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_banner_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_inter_front
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_native
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency
import livewallpaper.aod.screenlock.zipper.utilities.val_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen
import java.util.EnumMap


class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private var dbHelper: DbHelper? = null

    private var remoteConfig: FirebaseRemoteConfig? = null
    private var adsManager: AdsManager? = null
    var billingManager: BillingManager? = null
    companion object {
        var isUserConsent = false
        var consentListener: ((consent: Boolean) -> Unit?)? = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {

            // Initialize App Open Manager
            counter = 0
            inter_frequency_count = 0
            val cmpClass = CmpClass(activity ?: return@launchWhenStarted)
            cmpClass.initilaizeCMP()
            adsManager = AdsManager.appAdsInit(activity ?: return@launchWhenStarted)
            dbHelper = DbHelper(context ?: return@launchWhenStarted)
            dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }
            if (LoadPref("firstTime", context ?: return@launchWhenStarted) == 0) {
                SavePref("firstTime", "1", context ?: return@launchWhenStarted)
                SavePref(SpeedActivePref, "350", context ?: return@launchWhenStarted)
                SaveWallpaper(context ?: return@launchWhenStarted, 7)
            }
            billingManager = BillingManager(context ?: return@launchWhenStarted)
            val subsProductIdList =
                listOf("gold_product")
            val productInAppConsumable = when (DEBUG) {
                true -> listOf("inapp_product_id_1")
                false -> listOf("inapp_product_id_1", "inapp_product_id_1")
            }
            val inAppProductIdList = when (DEBUG) {
                true -> listOf(billingManager?.getDebugProductIDList())
                false -> listOf("inapp_product_id_1")
            }
            billingManager?.initialize(
                productInAppConsumable = productInAppConsumable,
                productInAppNonConsumable = inAppProductIdList as List<String>,
                productSubscriptions = subsProductIdList,
                billingListener = object : BillingListener {
                    override fun onConnectionResult(isSuccess: Boolean, message: String) {
                        Log.d(
                            "BillingTAG",
                            "Billing: initBilling: onConnectionResult: isSuccess = $isSuccess - message = $message"
                        )
                    }

                    override fun purchasesResult(purchaseDetailList: List<PurchaseDetail>) {
                        if (purchaseDetailList.isEmpty()) {
                            // No purchase found, reset all sharedPreferences (premium properties)
                        }
                        purchaseDetailList.forEachIndexed { index, purchaseDetail ->
                            PurchasePrefs(context?:return).putBoolean("inApp", true)?:return@forEachIndexed
                            Log.d(
                                "BillingTAG",
                                "Billing: initBilling: purchasesResult: $index) $purchaseDetail "
                            )
                        }
                    }
                }
            )
            val consentMap: MutableMap<ConsentType, FirebaseAnalytics.ConsentStatus> =
                EnumMap(
                    ConsentType::class.java
                )
            consentMap[ConsentType.ANALYTICS_STORAGE] = FirebaseAnalytics.ConsentStatus.GRANTED
            consentMap[ConsentType.AD_STORAGE] = FirebaseAnalytics.ConsentStatus.GRANTED
            consentMap[ConsentType.AD_USER_DATA] = FirebaseAnalytics.ConsentStatus.GRANTED
            consentMap[ConsentType.AD_PERSONALIZATION] = FirebaseAnalytics.ConsentStatus.GRANTED
            Firebase.analytics.setConsent(consentMap)
            if (AdsManager.isNetworkAvailable(activity) && !BillingUtil(activity?:return@launchWhenStarted).checkPurchased(activity?:return@launchWhenStarted)){
                consentListener = {
                    isUserConsent = it
                    Log.d("check_contest", "onViewCreated: $isUserConsent")
                    if (isUserConsent) {
                        if (AdsManager.isNetworkAvailable(context)) {
                            adsManager = AdsManager.appAdsInit(requireActivity())
                            initRemoteIds()
                        } else {
                            observeSplashLiveData()
                        }
                    } else {
                        observeSplashLiveData()
                    }
                }
            }else{
                observeSplashLiveData()
            }
            setupBackPressedCallback {
                //Do Nothing
            }
        }
    }

    private fun observeSplashLiveData() {
        try {
            lifecycleScope.launchWhenCreated {
                firebaseAnalytics("splash_fragment_load", "splash_fragment_load -->  Click")
                findNavController().navigate(R.id.myLoadingFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initRemoteIds() {

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // Set the minimum interval for fetching, in seconds
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
// Fetch the remote config values
        remoteConfig.fetchAndActivate().addOnCompleteListener(activity ?: return) { task ->
            if (task.isSuccessful) {
                // Apply the fetched values to your app
                applyAdIdsFromRemoteConfig(remoteConfig)
            } else {
                // Handle the error
                // For example, use default values or log an error message
                observeSplashLiveData()
            }

        }
    }

    private fun applyAdIdsFromRemoteConfig(remoteConfig: FirebaseRemoteConfig) {

        type_ad_native_list_data_screen = remoteConfig.getLong("type_ad_native_list_data_screen").toInt()
        type_ad_native_sound_screen = remoteConfig.getLong("type_ad_native_sound_screen").toInt()
        type_ad_native_setting_screen = remoteConfig.getLong("type_ad_native_setting_screen").toInt()
        type_ad_native_security_screen = remoteConfig.getLong("type_ad_native_security_screen").toInt()
        type_ad_native_enable_screen = remoteConfig.getLong("type_ad_native_enable_screen").toInt()
        type_ad_native_password_screen = remoteConfig.getLong("type_ad_native_password_screen").toInt()
        type_ad_native_reward_screen = remoteConfig.getLong("type_ad_native_reward_screen").toInt()

        banner_height = remoteConfig.getLong("banner_height").toInt()
        banner_type = remoteConfig.getLong("banner_type").toInt()
        id_inter_counter = remoteConfig.getLong("id_inter_counter").toInt()
        appUpdateType = remoteConfig.getLong("appUpdateType").toInt()
        id_frequency_counter = remoteConfig.getLong("id_frequency_counter").toInt()
        val_inapp_frequency = remoteConfig.getLong("val_inapp_frequency").toInt()

        id_inter_main_medium = remoteConfig.getString("id_inter_main_medium")
        id_native_screen = remoteConfig.getString("id_native_screen")
        id_app_open_screen = remoteConfig.getString("id_app_open_screen")
        id_adaptive_banner = remoteConfig.getString("id_adaptive_banner")

        id_reward = remoteConfig.getString("id_reward")
        apiKey = remoteConfig.getString("apiKey")
        Wallpaper_Cat = remoteConfig.getString("Wallpaper_Cat")
        id_inter_splash_Screen = remoteConfig.getString("id_inter_splash_Screen")
        id_collapsable_banner = remoteConfig.getString("id_collapsable_banner")
        id_splash_native = remoteConfig.getString("id_splash_native")
        id_inter_watch_ads_Screen = remoteConfig.getString("id_inter_watch_ads_Screen")
        native_precashe_counter = remoteConfig.getLong("native_precashe_counter").toInt()

        sessionOpenLanguageNew = remoteConfig.getLong("sessionOpenLanguageNew").toInt()
        sessionOnboarding = remoteConfig.getLong("sessionOnboarding").toInt()
        id_ads_button = remoteConfig.getString("id_ads_button")
        id_ads_bg = remoteConfig.getString("id_ads_bg")

//        Log.d("remote_ids", "$val_inapp_frequency")
//        Log.d("remote_ids", "$id_inter_counter")
//        Log.d("remote_ids", "$id_frequency_counter")
//        Log.d("remote_ids", id_inter_main_medium)
//        Log.d("remote_ids", id_native_screen)
//        Log.d("remote_ids", id_app_open_screen)
//        Log.d("remote_ids", id_adaptive_banner)
//        Log.d("remote_ids", id_inter_splash_Screen)
//        Log.d("remote_ids", id_collapsable_banner)
//        Log.d("remote_ids", id_splash_native)
        initRemoteConfig()
        parseJsonWithGson(remoteConfig.getString("test_ui_native"))

    }

    private fun initRemoteConfig() {

        try {
            FirebaseApp.initializeApp(context ?: return)
            remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 6
            }
            remoteConfig?.setConfigSettingsAsync(configSettings)
            remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
            remoteConfig?.fetchAndActivate()?.addOnCompleteListener(activity ?: return) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("RemoteConfig", "Config params updated: $updated")
                } else {
                    Log.d("RemoteConfig", "Fetch failed")
                }
                isFlowOne = remoteConfig!!["isFlowOne"].asBoolean()
                val_app_open = remoteConfig!!["val_app_open"].asBoolean()
                val_inter_main_medium = remoteConfig!!["val_inter_main_medium"].asBoolean()
                val_ad_native_main_menu_screen =
                    remoteConfig!!["val_ad_native_main_menu_screen"].asBoolean()
                val_ad_native_loading_screen =
                    remoteConfig!!["val_ad_native_loading_screen"].asBoolean()
                val_ad_native_intro_screen =
                    remoteConfig!!["val_ad_native_intro_screen"].asBoolean()
                val_ad_native_language_screen =
                    remoteConfig!!["val_ad_native_language_screen"].asBoolean()
                val_ad_native_sound_screen =
                    remoteConfig!!["val_ad_native_sound_screen"].asBoolean()
                val_exit_dialog_native = remoteConfig!!["val_exit_dialog_native"].asBoolean()
                val_ad_app_open_screen = remoteConfig!!["val_ad_app_open_screen"].asBoolean()
                val_ad_native_setting_screen =
                    remoteConfig!!["val_ad_native_setting_screen"].asBoolean()
                val_ad_native_security_screen =
                    remoteConfig!!["val_ad_native_security_screen"].asBoolean()
                val_ad_native_enable_screen =
                    remoteConfig!!["val_ad_native_enable_screen"].asBoolean()
                val_ad_native_password_screen =
                    remoteConfig!!["val_ad_native_password_screen"].asBoolean()
                val_ad_native_list_data_screen =
                    remoteConfig!!["val_ad_native_list_data_screen"].asBoolean()
                val_ad_inter_loading_screen =
                    remoteConfig!!["val_ad_inter_loading_screen"].asBoolean()
                val_ad_inter_language_screen =
                    remoteConfig!!["val_ad_inter_language_screen"].asBoolean()

                val_ad_inter_main_menu_screen_front =
                    remoteConfig!!["val_ad_inter_main_menu_screen_front"].asBoolean()
                val_ad_inter_sound_screen_front =
                    remoteConfig!!["val_ad_inter_sound_screen_front"].asBoolean()
                val_exit_dialog_inter_front =
                    remoteConfig!!["val_exit_dialog_inter_front"].asBoolean()
                val_ad_inter_setting_screen_front =
                    remoteConfig!!["val_ad_inter_setting_screen_front"].asBoolean()
                val_ad_inter_security_screen_front =
                    remoteConfig!!["val_ad_inter_security_screen_front"].asBoolean()
                val_ad_inter_enable_screen_front =
                    remoteConfig!!["val_ad_inter_enable_screen_front"].asBoolean()
                val_ad_inter_list_data_screen_front =
                    remoteConfig!!["val_ad_inter_list_data_screen_front"].asBoolean()
                val_banner_language_screen =
                    remoteConfig!!["val_banner_language_screen"].asBoolean()
                val_collapsable_banner =
                    remoteConfig!!["val_collapsable_banner"].asBoolean()
                val_banner_setting_screen =
                    remoteConfig!!["val_banner_setting_screen"].asBoolean()
                val_is_inapp_splash =
                    remoteConfig!!["val_is_inapp_splash"].asBoolean()
                val_is_inapp =
                    remoteConfig!!["val_is_inapp"].asBoolean()
                val_ad_native_reward_screen =
                    remoteConfig!!["val_ad_native_reward_screen"].asBoolean()
                val_ad_inter_reward_screen =remoteConfig!!["val_ad_inter_reward_screen"].asBoolean()
                val_ad_native_customize_screen =remoteConfig!!["val_ad_native_customize_screen"].asBoolean()
                val_ad_inter_customize_screen =remoteConfig!!["val_ad_inter_customize_screen"].asBoolean()
                val_ad_inter_watch_ads_screen =remoteConfig!!["val_ad_inter_watch_ads_screen"].asBoolean()
                val_ad_inter_wallpaper_server_screen =remoteConfig!!["val_ad_inter_wallpaper_server_screen"].asBoolean()
                val_ad_inter_on_board =remoteConfig!!["val_ad_inter_on_board"].asBoolean()

                if(val_app_open){
                    AdOpenApp(activity?.application ?:return@addOnCompleteListener, id_app_open_screen)
                }
                if (val_ad_app_open_screen) {
                    loadTwoInterAds(
                        ads = adsManager ?: return@addOnCompleteListener,
                        activity = activity ?: return@addOnCompleteListener,
                        remoteConfigNormal = true,
                        adIdNormal = id_inter_main_medium,
                        tagClass = "main_app_fragment"
                    )
                } else {
                    loadTwoInterAdsSplash(
                        adsManager ?: return@addOnCompleteListener,
                        activity ?: return@addOnCompleteListener,
                        remoteConfigNormal = val_ad_inter_loading_screen,
                        adIdNormal = id_inter_splash_Screen,
                        "splash"
                    )
                }
                observeSplashLiveData()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun parseJsonWithGson(jsonString: String) {
        if (jsonString.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<Map<String, List<NativeDesignType>>>() {}.type
            val dataMap: Map<String, List<NativeDesignType>> = gson.fromJson(jsonString, type)

            val language_bottom1 = dataMap["language_bottom"]?.map { it.native_design_type }
            language_bottom1?.forEach {
                language_bottom = it.toInt()
            }

            val on_bord_native1 = dataMap["on_bord_native"]?.map { it.native_design_type }
            on_bord_native1?.forEach {
                on_bord_native = it.toInt()
            }
            val exitdialog_bottom1 = dataMap["splash_bottom"]?.map { it.native_design_type }
            exitdialog_bottom1?.forEach {
                splash_bottom = it.toInt()
            }
            val thankyou_bottom1 = dataMap["thankyou_bottom"]?.map { it.native_design_type }
            thankyou_bottom1?.forEach {
                thankyou_bottom = it.toInt()
            }

            val home_native1 = dataMap["home_native"]?.map { it.native_design_type }
            home_native1?.forEach {
                home_native = it.toInt()
            }
            val custtom_main1 = dataMap["custtom_main"]?.map { it.native_design_type }
            custtom_main1?.forEach {
                custtom_main = it.toInt()
            }
            val wallpaper_fragment1 = dataMap["wallpaper_fragment"]?.map { it.native_design_type }
            wallpaper_fragment1?.forEach {
                wallpaper_fragment = it.toInt()
            }
            val theme_all1 = dataMap["theme_all"]?.map { it.native_design_type }
            theme_all1?.forEach {
                theme_all = it.toInt()
            }
            val enable_first1 = dataMap["enable_first"]?.map { it.native_design_type }
            enable_first1?.forEach {
                enable_first = it.toInt()
            }
            val apply_password1 = dataMap["apply_password"]?.map { it.native_design_type }
            apply_password1?.forEach {
                apply_password = it.toInt()
            }
            val setting_fragment1 = dataMap["setting_fragment"]?.map { it.native_design_type }
            setting_fragment1?.forEach {
                setting_fragment = it.toInt()
            }
            val sound_select1 = dataMap["sound_select"]?.map { it.native_design_type }
            sound_select1?.forEach {
                sound_select = it.toInt()
            }
            val security_question1 = dataMap["security_question"]?.map { it.native_design_type }
            security_question1?.forEach {
                security_question = it.toInt()
            }



        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        activity?.finish()
    }

}
