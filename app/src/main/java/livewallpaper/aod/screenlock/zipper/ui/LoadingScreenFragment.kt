package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.ads_manager.AdsManager
import livewallpaper.aod.screenlock.ads_manager.AdsManager.showOpenAd
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.ads_manager.showNormalInterAdSingle
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.FragmentLoadingBinding
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.LANG_CODE
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.counter
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getNativeLayout
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_splash_native
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isFlowOne
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.sessionOnboarding
import livewallpaper.aod.screenlock.zipper.utilities.sessionOpenLanguageNew
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.splash_bottom
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash

class LoadingScreenFragment:
    BaseFragment<FragmentLoadingBinding>(FragmentLoadingBinding::inflate) {

    private var adsManager: AdsManager? = null
    private var dbHelper: DbHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSplash = true
        counter = 0
        inter_frequency_count = 0
        adsManager = AdsManager.appAdsInit(activity ?: return)

        dbHelper = DbHelper(context ?: return)
        firebaseAnalytics("loading_fragment_open", "loading_fragment_open -->  Click")
        dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }

        if (DataBasePref.LoadPref("firstTime", context ?: return) == 0) {
            DataBasePref.SavePref("firstTime", "1", context ?: return)
            DataBasePref.SavePref(ConstantValues.SpeedActivePref, "350", context ?: return)
            AppAdapter.SaveWallpaper(context ?: return, 7)
        }

        if (isFlowOne) {
            lifecycleScope.launchWhenCreated {
                loadNative()
                delay(5000)
                if (isAdded && isVisible && !isDetached) {
                    _binding?.next?.visibility = View.VISIBLE
                    _binding?.animationView?.visibility = View.INVISIBLE
                }
            }
        } else {
            _binding?.next?.visibility = View.INVISIBLE
            _binding?.nativeExitAd?.visibility = View.INVISIBLE
            _binding?.adView?.visibility = View.INVISIBLE
            _binding?.animationView?.visibility = View.VISIBLE
            lifecycleScope.launchWhenCreated {
                delay(5000)
                if (val_ad_app_open_screen) {
                    isSplash = true
                    showOpenAd(activity ?: return@launchWhenCreated) {
                    }

                    when (sessionOnboarding) {
                        0 -> {
                            when (sessionOpenLanguageNew) {
                                0 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_main",
                                        "loading_fragment_load_next_btn_main -->  Click"
                                    )
                                    if (BillingUtil(
                                            activity ?: return@launchWhenCreated
                                        ).checkPurchased(
                                            context ?: return@launchWhenCreated
                                        ) || !val_is_inapp_splash
                                    ) {
                                        findNavController().navigate(
                                            R.id.myMainMenuFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    } else {
                                        findNavController().navigate(
                                            R.id.FragmentBuyScreen,
                                            bundleOf("isSplash" to true)
                                        )
                                    }
                                }

                                1 -> {
                                    if (dbHelper?.getBooleanData(
                                            context ?: return@launchWhenCreated,
                                            IS_FIRST,
                                            false
                                        ) == false
                                    ) {
                                        findNavController().navigate(
                                            R.id.LanguageFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    } else {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_main",
                                            "loading_fragment_load_next_btn_main -->  Click"
                                        )
                                        if (BillingUtil(
                                                activity ?: return@launchWhenCreated
                                            ).checkPurchased(
                                                context ?: return@launchWhenCreated
                                            ) || !val_is_inapp_splash
                                        ) {
                                            findNavController().navigate(
                                                R.id.myMainMenuFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            findNavController().navigate(
                                                R.id.FragmentBuyScreen,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    }
                                }

                                2 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_language",
                                        "loading_fragment_load_next_btn_language -->  Click"
                                    )
                                    findNavController().navigate(
                                        R.id.LanguageFragment, bundleOf("isSplash" to true)
                                    )
                                }
                            }
                        }

                        1 -> {
                            if (dbHelper?.getBooleanData(
                                    context ?: return@launchWhenCreated,
                                    IS_INTRO,
                                    false
                                ) == false
                            ) {
                                firebaseAnalytics(
                                    "loading_fragment_load_next_btn_intro",
                                    "loading_fragment_load_next_btn_intro -->  Click"
                                )
                                findNavController().navigate(R.id.IntoScreenFragment)
                            } else {
                                when (sessionOpenLanguageNew) {
                                    0 -> {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_main",
                                            "loading_fragment_load_next_btn_main -->  Click"
                                        )
                                        if (BillingUtil(
                                                activity ?: return@launchWhenCreated
                                            ).checkPurchased(
                                                context ?: return@launchWhenCreated
                                            ) || !val_is_inapp_splash
                                        ) {
                                            findNavController().navigate(
                                                R.id.myMainMenuFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            findNavController().navigate(
                                                R.id.FragmentBuyScreen,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    }

                                    1 -> {
                                        if (dbHelper?.getBooleanData(
                                                context ?: return@launchWhenCreated,
                                                IS_FIRST,
                                                false
                                            ) == false
                                        ) {
                                            findNavController().navigate(
                                                R.id.LanguageFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_main",
                                                "loading_fragment_load_next_btn_main -->  Click"
                                            )
                                            if (BillingUtil(
                                                    activity
                                                        ?: return@launchWhenCreated
                                                ).checkPurchased(
                                                    context
                                                        ?: return@launchWhenCreated
                                                ) || !val_is_inapp_splash
                                            ) {
                                                findNavController().navigate(
                                                    R.id.myMainMenuFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                findNavController().navigate(
                                                    R.id.FragmentBuyScreen,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }
                                    }

                                    2 -> {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_language",
                                            "loading_fragment_load_next_btn_language -->  Click"
                                        )
                                        findNavController().navigate(
                                            R.id.LanguageFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    }
                                }
                            }
                        }

                        2 -> {
                            firebaseAnalytics(
                                "loading_fragment_load_next_btn_intro",
                                "loading_fragment_load_next_btn_intro -->  Click"
                            )
                            findNavController().navigate(R.id.IntoScreenFragment)
                        }
                    }
                } else {
                    adsManager?.let {
                        showNormalInterAdSingle(
                            it,
                            activity ?: return@let,
                            remoteConfigNormal = val_ad_inter_loading_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "splash",
                            layout = _binding?.adsLay ?: return@launchWhenCreated
                        ) {
                            when (sessionOnboarding) {
                                0 -> {
                                    when (sessionOpenLanguageNew) {
                                        0 -> {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_main",
                                                "loading_fragment_load_next_btn_main -->  Click"
                                            )
                                            if (BillingUtil(
                                                    activity ?: return@showNormalInterAdSingle
                                                ).checkPurchased(
                                                    context ?: return@showNormalInterAdSingle
                                                ) || !val_is_inapp_splash
                                            ) {
                                                findNavController().navigate(
                                                    R.id.myMainMenuFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                findNavController().navigate(
                                                    R.id.FragmentBuyScreen,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }

                                        1 -> {
                                            if (dbHelper?.getBooleanData(
                                                    context ?: return@showNormalInterAdSingle,
                                                    IS_FIRST,
                                                    false
                                                ) == false
                                            ) {
                                                findNavController().navigate(
                                                    R.id.LanguageFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                firebaseAnalytics(
                                                    "loading_fragment_load_next_btn_main",
                                                    "loading_fragment_load_next_btn_main -->  Click"
                                                )
                                                if (BillingUtil(
                                                        activity ?: return@showNormalInterAdSingle
                                                    ).checkPurchased(
                                                        context ?: return@showNormalInterAdSingle
                                                    ) || !val_is_inapp_splash
                                                ) {
                                                    findNavController().navigate(
                                                        R.id.myMainMenuFragment,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                } else {
                                                    findNavController().navigate(
                                                        R.id.FragmentBuyScreen,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                }
                                            }
                                        }

                                        2 -> {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_language",
                                                "loading_fragment_load_next_btn_language -->  Click"
                                            )
                                            findNavController().navigate(
                                                R.id.LanguageFragment, bundleOf("isSplash" to true)
                                            )
                                        }
                                    }
                                }

                                1 -> {
                                    if (dbHelper?.getBooleanData(
                                            context ?: return@showNormalInterAdSingle,
                                            IS_INTRO,
                                            false
                                        ) == false
                                    ) {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_intro",
                                            "loading_fragment_load_next_btn_intro -->  Click"
                                        )
                                        findNavController().navigate(R.id.IntoScreenFragment)
                                    } else {
                                        when (sessionOpenLanguageNew) {
                                            0 -> {
                                                firebaseAnalytics(
                                                    "loading_fragment_load_next_btn_main",
                                                    "loading_fragment_load_next_btn_main -->  Click"
                                                )
                                                if (BillingUtil(
                                                        activity ?: return@showNormalInterAdSingle
                                                    ).checkPurchased(
                                                        context ?: return@showNormalInterAdSingle
                                                    ) || !val_is_inapp_splash
                                                ) {
                                                    findNavController().navigate(
                                                        R.id.myMainMenuFragment,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                } else {
                                                    findNavController().navigate(
                                                        R.id.FragmentBuyScreen,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                }
                                            }

                                            1 -> {
                                                if (dbHelper?.getBooleanData(
                                                        context ?: return@showNormalInterAdSingle,
                                                        IS_FIRST,
                                                        false
                                                    ) == false
                                                ) {
                                                    findNavController().navigate(
                                                        R.id.LanguageFragment,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                } else {
                                                    firebaseAnalytics(
                                                        "loading_fragment_load_next_btn_main",
                                                        "loading_fragment_load_next_btn_main -->  Click"
                                                    )
                                                    if (BillingUtil(
                                                            activity
                                                                ?: return@showNormalInterAdSingle
                                                        ).checkPurchased(
                                                            context
                                                                ?: return@showNormalInterAdSingle
                                                        ) || !val_is_inapp_splash
                                                    ) {
                                                        findNavController().navigate(
                                                            R.id.myMainMenuFragment,
                                                            bundleOf("isSplash" to true)
                                                        )
                                                    } else {
                                                        findNavController().navigate(
                                                            R.id.FragmentBuyScreen,
                                                            bundleOf("isSplash" to true)
                                                        )
                                                    }
                                                }
                                            }

                                            2 -> {
                                                firebaseAnalytics(
                                                    "loading_fragment_load_next_btn_language",
                                                    "loading_fragment_load_next_btn_language -->  Click"
                                                )
                                                findNavController().navigate(
                                                    R.id.LanguageFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }
                                    }
                                }

                                2 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_intro",
                                        "loading_fragment_load_next_btn_intro -->  Click"
                                    )
                                    findNavController().navigate(R.id.IntoScreenFragment)
                                }
                            }
                        }
                    }
                }

            }

        }

        setupBackPressedCallback {
            //Do Nothing
        }
        _binding?.next?.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                if (val_ad_app_open_screen) {
                    isSplash = true
                    showOpenAd(activity ?: return@launchWhenCreated) {
                    }

                    when (sessionOnboarding) {
                        0 -> {
                            when (sessionOpenLanguageNew) {
                                0 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_main",
                                        "loading_fragment_load_next_btn_main -->  Click"
                                    )
                                    if (BillingUtil(
                                            activity ?: return@launchWhenCreated
                                        ).checkPurchased(
                                            context ?: return@launchWhenCreated
                                        ) || !val_is_inapp_splash
                                    ) {
                                        findNavController().navigate(
                                            R.id.myMainMenuFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    } else {
                                        findNavController().navigate(
                                            R.id.FragmentBuyScreen,
                                            bundleOf("isSplash" to true)
                                        )
                                    }
                                }

                                1 -> {
                                    if (dbHelper?.getBooleanData(
                                            context ?: return@launchWhenCreated,
                                            IS_FIRST,
                                            false
                                        ) == false
                                    ) {
                                        findNavController().navigate(
                                            R.id.LanguageFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    } else {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_main",
                                            "loading_fragment_load_next_btn_main -->  Click"
                                        )
                                        if (BillingUtil(
                                                activity ?: return@launchWhenCreated
                                            ).checkPurchased(
                                                context ?: return@launchWhenCreated
                                            ) || !val_is_inapp_splash
                                        ) {
                                            findNavController().navigate(
                                                R.id.myMainMenuFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            findNavController().navigate(
                                                R.id.FragmentBuyScreen,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    }
                                }

                                2 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_language",
                                        "loading_fragment_load_next_btn_language -->  Click"
                                    )
                                    findNavController().navigate(
                                        R.id.LanguageFragment, bundleOf("isSplash" to true)
                                    )
                                }
                            }
                        }

                        1 -> {
                            if (dbHelper?.getBooleanData(
                                    context ?: return@launchWhenCreated,
                                    IS_INTRO,
                                    false
                                ) == false
                            ) {
                                firebaseAnalytics(
                                    "loading_fragment_load_next_btn_intro",
                                    "loading_fragment_load_next_btn_intro -->  Click"
                                )
                                findNavController().navigate(R.id.IntoScreenFragment)
                            } else {
                                when (sessionOpenLanguageNew) {
                                    0 -> {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_main",
                                            "loading_fragment_load_next_btn_main -->  Click"
                                        )
                                        if (BillingUtil(
                                                activity ?: return@launchWhenCreated
                                            ).checkPurchased(
                                                context ?: return@launchWhenCreated
                                            ) || !val_is_inapp_splash
                                        ) {
                                            findNavController().navigate(
                                                R.id.myMainMenuFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            findNavController().navigate(
                                                R.id.FragmentBuyScreen,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    }

                                    1 -> {
                                        if (dbHelper?.getBooleanData(
                                                context ?: return@launchWhenCreated,
                                                IS_FIRST,
                                                false
                                            ) == false
                                        ) {
                                            findNavController().navigate(
                                                R.id.LanguageFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_main",
                                                "loading_fragment_load_next_btn_main -->  Click"
                                            )
                                            if (BillingUtil(
                                                    activity
                                                        ?: return@launchWhenCreated
                                                ).checkPurchased(
                                                    context
                                                        ?: return@launchWhenCreated
                                                ) || !val_is_inapp_splash
                                            ) {
                                                findNavController().navigate(
                                                    R.id.myMainMenuFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                findNavController().navigate(
                                                    R.id.FragmentBuyScreen,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }
                                    }

                                    2 -> {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_language",
                                            "loading_fragment_load_next_btn_language -->  Click"
                                        )
                                        findNavController().navigate(
                                            R.id.LanguageFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    }
                                }
                            }
                        }

                        2 -> {
                            firebaseAnalytics(
                                "loading_fragment_load_next_btn_intro",
                                "loading_fragment_load_next_btn_intro -->  Click"
                            )
                            findNavController().navigate(R.id.IntoScreenFragment)
                        }
                    }
                } else {
                    adsManager?.let {
                        showNormalInterAdSingle(
                            it,
                            activity ?: return@let,
                            remoteConfigNormal = val_ad_inter_loading_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "splash",
                            layout = _binding?.adsLay ?: return@let
                        ) {

                            when (sessionOnboarding) {
                                0 -> {
                                    when (sessionOpenLanguageNew) {
                                        0 -> {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_main",
                                                "loading_fragment_load_next_btn_main -->  Click"
                                            )
                                            if (BillingUtil(
                                                    activity ?: return@showNormalInterAdSingle
                                                ).checkPurchased(
                                                    context ?: return@showNormalInterAdSingle
                                                ) || !val_is_inapp_splash
                                            ) {
                                                findNavController().navigate(
                                                    R.id.myMainMenuFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                findNavController().navigate(
                                                    R.id.FragmentBuyScreen,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }

                                        1 -> {
                                            if (dbHelper?.getBooleanData(
                                                    context ?: return@showNormalInterAdSingle,
                                                    IS_FIRST,
                                                    false
                                                ) == false
                                            ) {
                                                findNavController().navigate(
                                                    R.id.LanguageFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                firebaseAnalytics(
                                                    "loading_fragment_load_next_btn_main",
                                                    "loading_fragment_load_next_btn_main -->  Click"
                                                )
                                                if (BillingUtil(
                                                        activity ?: return@showNormalInterAdSingle
                                                    ).checkPurchased(
                                                        context ?: return@showNormalInterAdSingle
                                                    ) || !val_is_inapp_splash
                                                ) {
                                                    findNavController().navigate(
                                                        R.id.myMainMenuFragment,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                } else {
                                                    findNavController().navigate(
                                                        R.id.FragmentBuyScreen,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                }
                                            }
                                        }

                                        2 -> {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_language",
                                                "loading_fragment_load_next_btn_language -->  Click"
                                            )
                                            findNavController().navigate(
                                                R.id.LanguageFragment, bundleOf("isSplash" to true)
                                            )
                                        }
                                    }
                                }

                                1 -> {
                                    if (dbHelper?.getBooleanData(
                                            context ?: return@showNormalInterAdSingle,
                                            IS_INTRO,
                                            false
                                        ) == false
                                    ) {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_intro",
                                            "loading_fragment_load_next_btn_intro -->  Click"
                                        )
                                        findNavController().navigate(R.id.IntoScreenFragment)
                                    } else {
                                        when (sessionOpenLanguageNew) {
                                            0 -> {
                                                firebaseAnalytics(
                                                    "loading_fragment_load_next_btn_main",
                                                    "loading_fragment_load_next_btn_main -->  Click"
                                                )
                                                if (BillingUtil(
                                                        activity ?: return@showNormalInterAdSingle
                                                    ).checkPurchased(
                                                        context ?: return@showNormalInterAdSingle
                                                    ) || !val_is_inapp_splash
                                                ) {
                                                    findNavController().navigate(
                                                        R.id.myMainMenuFragment,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                } else {
                                                    findNavController().navigate(
                                                        R.id.FragmentBuyScreen,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                }
                                            }

                                            1 -> {
                                                if (dbHelper?.getBooleanData(
                                                        context ?: return@showNormalInterAdSingle,
                                                        IS_FIRST,
                                                        false
                                                    ) == false
                                                ) {
                                                    findNavController().navigate(
                                                        R.id.LanguageFragment,
                                                        bundleOf("isSplash" to true)
                                                    )
                                                } else {
                                                    firebaseAnalytics(
                                                        "loading_fragment_load_next_btn_main",
                                                        "loading_fragment_load_next_btn_main -->  Click"
                                                    )
                                                    if (BillingUtil(
                                                            activity
                                                                ?: return@showNormalInterAdSingle
                                                        ).checkPurchased(
                                                            context
                                                                ?: return@showNormalInterAdSingle
                                                        ) || !val_is_inapp_splash
                                                    ) {
                                                        findNavController().navigate(
                                                            R.id.myMainMenuFragment,
                                                            bundleOf("isSplash" to true)
                                                        )
                                                    } else {
                                                        findNavController().navigate(
                                                            R.id.FragmentBuyScreen,
                                                            bundleOf("isSplash" to true)
                                                        )
                                                    }
                                                }
                                            }

                                            2 -> {
                                                firebaseAnalytics(
                                                    "loading_fragment_load_next_btn_language",
                                                    "loading_fragment_load_next_btn_language -->  Click"
                                                )
                                                findNavController().navigate(
                                                    R.id.LanguageFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }
                                    }
                                }

                                2 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_intro",
                                        "loading_fragment_load_next_btn_intro -->  Click"
                                    )
                                    findNavController().navigate(R.id.IntoScreenFragment)
                                }
                            }

                        }
                    }
                }
            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadNative() {
        val adView = activity?.layoutInflater?.inflate(
            getNativeLayout(
                splash_bottom, _binding?.nativeExitAd!!,
                activity?:return
            ),
            null
        ) as NativeAdView
        adsManager?.nativeAdsMain()?.loadNativeAd(
            activity ?: return,
            val_ad_native_loading_screen,
            id_splash_native,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
//                        val adView =layoutInflater.inflate(R.layout.ad_unified_media, null) as NativeAdView
                        adsManager?.nativeAdsMain()
                            ?.nativeViewMediaSplash(
                                context ?: return,
                                currentNativeAd ?: return,
                                adView
                            )
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    _binding?.nativeExitAd?.visibility = View.INVISIBLE
                    _binding?.adView?.visibility = View.INVISIBLE
                    _binding?.next?.visibility = View.VISIBLE
                    _binding?.animationView?.visibility = View.INVISIBLE
                    super.nativeAdValidate(string)
                }
            })
    }

    override fun onPause() {
        super.onPause()
        isSplash = false
    }

    override fun onResume() {
        super.onResume()
        isSplash = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}