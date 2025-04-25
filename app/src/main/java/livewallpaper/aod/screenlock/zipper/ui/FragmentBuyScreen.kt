package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdsManager
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.PurchasePrefs
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.showTwoInterAdStart
import com.hypersoft.billing.BillingManager
import com.hypersoft.billing.dataClasses.ProductType
import com.hypersoft.billing.dataClasses.PurchaseDetail
import com.hypersoft.billing.interfaces.BillingListener
import com.hypersoft.billing.interfaces.OnPurchaseListener
import livewallpaper.aod.screenlock.zipper.BuildConfig.DEBUG
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.FragmentPremiumScreenBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_in_app

class FragmentBuyScreen  :
    BaseFragment<FragmentPremiumScreenBinding>(FragmentPremiumScreenBinding::inflate) {

    var billingManager: BillingManager? = null
    var planId: String? = null
    var isSplashFrom: Boolean? = false
    var adsManager: AdsManager? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {//      isSplash =true
            adsManager = AdsManager.appAdsInit(activity?:return)
            arguments?.let {
                isSplashFrom = it.getBoolean("isSplash")
            }
            if (isSplash) {
                Handler().postDelayed({
                    _binding?.closeBtn?.visibility = View.VISIBLE
                    _binding?.butClose?.visibility = View.VISIBLE
                },3000)
            } else {
                _binding?.closeBtn?.visibility = View.VISIBLE
                _binding?.butClose?.visibility = View.VISIBLE
            }

            planId = "gold-plan-yearly"
            billingManager = BillingManager(context ?: return)
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
                            Log.d(
                                "BillingTAG",
                                "Billing: initBilling: purchasesResult: $index) $purchaseDetail "
                            )
                        }
                    }
                }
            )
            billingManager?.productDetailsLiveData?.observe(viewLifecycleOwner) { productDetailList ->
                Log.d("in_app_TAG", "Billing: initObservers: $productDetailList")

                productDetailList.forEach { productDetail ->
                    if (productDetail.productType == ProductType.inapp) {
                        // productDetail (monthly)
                        _binding?.butBtn?.text =
                            "${getString(R.string.purchase)} : ${productDetail.pricingDetails[0].price}"
                    }

                }
            }
            setupBackPressedCallback {
                if (isSplashFrom == true) {
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_in_app,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "in_app_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            findNavController().navigate(
                                R.id.myMainMenuFragment,
                                bundleOf("isSplash" to true)
                            )
                        }
                    }
                } else {
                    findNavController().navigateUp()
                }
            }
            _binding?.butBtn?.clickWithThrottle {
                billingManager?.makeInAppPurchase(
                    activity = activity,
                    productId = "inapp_product_id_1",
                    object : OnPurchaseListener {
                        override fun onPurchaseResult(isPurchaseSuccess: Boolean, message: String) {
                            Log.d("in_app_TAG", "makeSubPurchase: $isPurchaseSuccess - $message")
                            if (isPurchaseSuccess) {
                                PurchasePrefs(context).putBoolean("inApp", true)
                                findNavController().navigateUp()
                            }
                        }
                    })
            }
            _binding?.closeBtn?.clickWithThrottle {
                if (isSplashFrom == true) {
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_in_app,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "in_app_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            findNavController().navigate(
                                R.id.myMainMenuFragment,
                                bundleOf("isSplash" to true)
                            )
                        }
                    }
                } else {
                    findNavController().navigateUp()
                }
            }
            _binding?.butClose?.clickWithThrottle {
                if (isSplashFrom == true) {
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_in_app,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "in_app_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            findNavController().navigate(
                                R.id.myMainMenuFragment,
                                bundleOf("isSplash" to true)
                            )
                        }
                    }
                } else {
                    findNavController().navigateUp()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}