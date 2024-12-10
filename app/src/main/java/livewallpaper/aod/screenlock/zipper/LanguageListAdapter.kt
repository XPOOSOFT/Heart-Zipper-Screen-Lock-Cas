package livewallpaper.aod.screenlock.zipper

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeType
import livewallpaper.aod.screenlock.zipper.databinding.AdsItemBinding
import livewallpaper.aod.screenlock.zipper.databinding.LanguageAppItemBinding
import livewallpaper.aod.screenlock.zipper.model.LanguageModel
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen_h

class LanguageListAdapter(
    private val items: List<LanguageModel>,
    private val ads: AdsManager,
    private val activity: Activity,
    private var clickItem: ((LanguageModel) -> Unit)
) : BaseAdapter() {

    private var lastCheckedPosition: Int = -1
    companion object {
        const val ITEM_TYPE = 0
        const val AD_TYPE = 1
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return if (items[position].country_name == "Ad") AD_TYPE else ITEM_TYPE
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val context = parent?.context ?: return convertView!!
        val viewType = getItemViewType(position)

        return if (viewType == AD_TYPE) {
            val bindingAds = convertView?.tag as? AdsItemBinding
                ?: AdsItemBinding.inflate(LayoutInflater.from(context), parent, false).apply {
                    root.tag = this
                }
            loadAd(bindingAds, position)
            bindingAds.root
        } else {
            val binding = convertView?.tag as? LanguageAppItemBinding
                ?: LanguageAppItemBinding.inflate(LayoutInflater.from(context), parent, false).apply {
                    root.tag = this
                }
            bindItem(binding, position)
            binding.root
        }
    }

    private fun bindItem(binding: LanguageAppItemBinding, position: Int) {
        val item = items[position]
        binding.countryName.text = item.country_name
        binding.radioButton.isChecked = item.check
        Glide.with(binding.root.context).load(item.country_flag).into(binding.flagIcon)

        binding.root.setOnClickListener {
            updateSelection(position)
            clickItem.invoke(item)
        }

        binding.radioButton.setOnClickListener {
            updateSelection(position)
            clickItem.invoke(item)
        }
    }

    private fun loadAd(bindingAds: AdsItemBinding, position: Int) {
        AdmobNative().loadNativeAds(
            activity,
            bindingAds.nativeExitAd,
            id_native_screen,
            if (val_ad_native_language_screen) 1 else 0,
            isAppPurchased = BillingUtil(activity).checkPurchased(activity),
            isInternetConnected = AdsManager.isNetworkAvailable(activity),
            nativeType = if (val_ad_native_language_screen_h) NativeType.LARGE else NativeType.BANNER,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    bindingAds.adView.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    bindingAds.adView.visibility = View.VISIBLE
                }

                override fun onAdImpression() {
                    bindingAds.adView.visibility = View.GONE
                }
            }
        )
    }

    fun selectLanguage(langPositionSelected: String) {
        for (i in items) {
            i.check = i.country_code == langPositionSelected
        }
    }

    private fun updateSelection(position: Int) {
        if (lastCheckedPosition != position) {
            if (lastCheckedPosition != -1) {
                items[lastCheckedPosition].check = false
                notifyDataSetChanged()
            }
            items[position].check = true
            lastCheckedPosition = position
            notifyDataSetChanged()
        }
    }
}
