package livewallpaper.aod.screenlock.zipper.ui

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.android.CAS
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.zipper.BuildConfig
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.CAS_ID
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
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
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_bg
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_button
import livewallpaper.aod.screenlock.zipper.utilities.id_frequency_counter
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_counter
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_reward
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isFlowOne
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isRating
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.is_val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_counter
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
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
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_enable_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_enable_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_list_data_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_list_data_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_main_menu_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_main_menu_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_password_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_password_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_sound_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_sound_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_customize_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_customize_screen_h
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_intro_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen_h
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen_h
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_main_menu_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_password_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen_h
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_app_open
import livewallpaper.aod.screenlock.zipper.utilities.val_banner_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_banner_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_collapsable_banner
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_inter_back
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_inter_front
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_native
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency
import livewallpaper.aod.screenlock.zipper.utilities.val_inter_back_press
import livewallpaper.aod.screenlock.zipper.utilities.val_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen


class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private var dbHelper: DbHelper? = null

    private var remoteConfig: FirebaseRemoteConfig? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {

            // Initialize SDK
            adManager = CAS.buildManager()
                .withManagerId(CAS_ID)
                .withTestAdMode(BuildConfig.DEBUG)
                .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded, AdType.AppOpen, AdType.Native, AdType.Rewarded)
                .withConsentFlow(
                    ConsentFlow(isEnabled = true)
                        .withDismissListener {
                            Log.d(TAG, "Consent flow dismissed")
                            observeSplashLiveData()
                        }
                )
                .withCompletionListener {
                    if (it.error == null) {
                        Log.d(TAG, "Ad manager initialized")
                        // Initialize App Open Manager
                        isSplash = false
                        isRating = true
                        counter = 0
                        inter_frequency_count = 0
                        dbHelper = DbHelper(context ?: return@withCompletionListener)
                        dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }

                        if (LoadPref("firstTime", context ?: return@withCompletionListener) == 0) {
                            SavePref("firstTime", "1", context ?: return@withCompletionListener)
                            SavePref(SpeedActivePref, "350", context ?: return@withCompletionListener)
                            SaveWallpaper(context ?: return@withCompletionListener, 7)
                        }
                        if (isNetworkAvailable(context)) {
                            initRemoteIds()
                        } else {
                            observeSplashLiveData()
                        }
                    } else {
                        observeSplashLiveData()
                        Log.d(TAG, "Ad manager initialization failed: " + it.error)
                    }
                }
                .build(context?:return@launchWhenStarted)
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

        type_ad_native_list_data_screen =
            remoteConfig.getLong("type_ad_native_list_data_screen").toInt()
        type_ad_native_sound_screen = remoteConfig.getLong("type_ad_native_sound_screen").toInt()
        type_ad_native_setting_screen =
            remoteConfig.getLong("type_ad_native_setting_screen").toInt()
        type_ad_native_security_screen =
            remoteConfig.getLong("type_ad_native_security_screen").toInt()
        type_ad_native_enable_screen = remoteConfig.getLong("type_ad_native_enable_screen").toInt()
        type_ad_native_password_screen =
            remoteConfig.getLong("type_ad_native_password_screen").toInt()
        type_ad_native_reward_screen = remoteConfig.getLong("type_ad_native_reward_screen").toInt()
        native_precashe_counter = remoteConfig.getLong("native_precashe_counter").toInt()

        appUpdateType = remoteConfig.getLong("appUpdateType").toInt()
        id_inter_counter = remoteConfig.getLong("id_inter_counter").toInt()
        id_frequency_counter = remoteConfig.getLong("id_frequency_counter").toInt()

        val_inapp_frequency = remoteConfig.getLong("val_inapp_frequency").toInt()
        banner_height = remoteConfig.getLong("banner_height").toInt()
        banner_type = remoteConfig.getLong("banner_type").toInt()

        id_native_screen = "ca-app-pub-6480664593997158/1302287071"
//        id_inter_main_medium = remoteConfig.getString("id_inter_main_medium")
//        id_native_screen = remoteConfig.getString("id_native_screen")
//        id_adaptive_banner = remoteConfig.getString("id_adaptive_banner")
//        id_app_open_screen = remoteConfig.getString("id_app_open_screen")
//        id_inter_splash_Screen = remoteConfig.getString("id_inter_splash_Screen")
//        id_collapsable_banner = remoteConfig.getString("id_collapsable_banner")
//        id_splash_native = remoteConfig.getString("id_splash_native")
        id_reward = remoteConfig.getString("id_reward")
        apiKey = remoteConfig.getString("apiKey")
        Wallpaper_Cat = remoteConfig.getString("Wallpaper_Cat")

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
                val_app_open = remoteConfig!!["val_app_open"].asBoolean()
                isFlowOne = remoteConfig!!["isFlowOne"].asBoolean()
                val_on_bording_screen = remoteConfig!!["val_on_bording_screen"].asBoolean()
                val_inter_back_press = remoteConfig!!["val_inter_back_press"].asBoolean()
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
                val_ad_native_reward_screen =
                    remoteConfig!!["val_ad_native_reward_screen"].asBoolean()
                val_ad_inter_reward_screen =
                    remoteConfig!!["val_ad_inter_reward_screen"].asBoolean()
                val_ad_inter_customize_screen =
                    remoteConfig!!["val_ad_inter_customize_screen"].asBoolean()

                val_ad_inter_loading_screen =
                    remoteConfig!!["val_ad_inter_loading_screen"].asBoolean()
                is_val_ad_inter_loading_screen =
                    remoteConfig!!["is_val_ad_inter_loading_screen"].asBoolean()
                val_ad_app_open_splash_screen =
                    remoteConfig!!["val_ad_app_open_splash_screen"].asBoolean()
                val_ad_inter_main_menu_screen_back =
                    remoteConfig!!["val_ad_inter_main_menu_screen_back"].asBoolean()
                val_ad_inter_sound_screen_back =
                    remoteConfig!!["val_ad_inter_sound_screen_back"].asBoolean()
                val_exit_dialog_inter_back =
                    remoteConfig!!["val_exit_dialog_inter_back"].asBoolean()
                val_ad_inter_setting_screen_back =
                    remoteConfig!!["val_ad_inter_setting_screen_back"].asBoolean()
                val_ad_inter_security_screen_back =
                    remoteConfig!!["val_ad_inter_security_screen_back"].asBoolean()
                val_ad_inter_enable_screen_back =
                    remoteConfig!!["val_ad_inter_enable_screen_back"].asBoolean()
                val_ad_inter_password_screen_back =
                    remoteConfig!!["val_ad_inter_password_screen_back"].asBoolean()
                val_ad_inter_list_data_screen_back =
                    remoteConfig!!["val_ad_inter_list_data_screen_back"].asBoolean()

                val_ad_inter_main_menu_screen_front =
                    remoteConfig!!["val_ad_inter_main_menu_screen_front"].asBoolean()
                val_ad_inter_language_screen =
                    remoteConfig!!["val_ad_inter_language_screen"].asBoolean()
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
                val_ad_inter_password_screen_front =
                    remoteConfig!!["val_ad_inter_password_screen_front"].asBoolean()
                val_ad_inter_list_data_screen_front =
                    remoteConfig!!["val_ad_inter_list_data_screen_front"].asBoolean()
                val_ad_native_customize_screen =
                    remoteConfig!!["val_ad_native_customize_screen"].asBoolean()
                val_banner_language_screen =
                    remoteConfig!!["val_banner_language_screen"].asBoolean()
                val_collapsable_banner = remoteConfig!!["val_collapsable_banner"].asBoolean()
                val_banner_setting_screen = remoteConfig!!["val_banner_setting_screen"].asBoolean()
                val_is_inapp_splash = remoteConfig!!["val_is_inapp_splash"].asBoolean()
                val_is_inapp = remoteConfig!!["val_is_inapp"].asBoolean()
                val_ad_native_language_screen_h =
                    remoteConfig!!["val_ad_native_language_screen_h"].asBoolean()
                val_ad_native_customize_screen_h =
                    remoteConfig!!["val_ad_native_customize_screen_h"].asBoolean()
                val_ad_native_reward_screen_h =
                    remoteConfig!!["val_ad_native_reward_screen_h"].asBoolean()
                val_ad_native_list_data_screen_h =
                    remoteConfig!!["val_ad_native_list_data_screen_h"].asBoolean()

                Log.d("RemoteConfig", "Fetch val_inter_main_medium -> $val_inter_main_medium")
                Log.d("RemoteConfig", "Fetch val_inter_back_press -> $val_inter_back_press")
                observeSplashLiveData()
            }

        } catch (e: Exception) {
            e.printStackTrace()
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
