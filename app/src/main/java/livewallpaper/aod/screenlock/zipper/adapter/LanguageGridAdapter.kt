package livewallpaper.aod.screenlock.zipper.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.databinding.AdsItemBinding
import livewallpaper.aod.screenlock.zipper.databinding.LanguageAppItemBinding
import livewallpaper.aod.screenlock.zipper.model.LanguageModel


class LanguageGridAdapter(
    private val items: List<LanguageModel>,
    private var clickItem: ((LanguageModel) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var context: Context? = null
    private var lastCheckedPosition: Int = -1

    companion object {
        const val ITEM_TYPE = 0
        const val AD_TYPE = 1
    }

    class ViewHolder(val binding: LanguageAppItemBinding) : RecyclerView.ViewHolder(binding.root)

    class AdViewHolder(private val bindingAds: AdsItemBinding) : RecyclerView.ViewHolder(bindingAds.root)


    override fun getItemViewType(position: Int): Int {
        return if (items[position].country_name == "Ad") AD_TYPE else ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == AD_TYPE) {
            AdViewHolder(
                AdsItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ViewHolder(
                LanguageAppItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE) {
            Log.d("adapter_item", "onBindViewHolder: Adapter View Hoder $position")
            (holder as ViewHolder).binding.countryName.text = items[position].country_name
            holder.binding.radioButton.isChecked = items[position].check
            Glide.with(context ?: return).load(items[position].country_flag)
                .into(holder.binding.flagIcon)
            holder.binding.root.setOnClickListener {
                updateSelection(position)
                clickItem.invoke(items[position])
            }
            holder.binding.radioButton.setOnClickListener {
                updateSelection(position)
                clickItem.invoke(items[position])
            }
        } else {
            Log.d("adapter_ad", "onBindViewHolder: Adapter View Hoder $position")

        }
    }

    override fun getItemCount(): Int {

        Log.d("adapter", "onBindViewHolder: Adapter Size${items.size}")
        return items.size
    }

    fun selectLanguage(langPositionSelected: String) {
        for (i in items) {
            i.check = i.country_code == langPositionSelected
        }
//        notifyDataSetChanged()
    }

    private fun updateSelection(position: Int) {
        if (lastCheckedPosition != position) {
            // Uncheck the previously selected item
            if (lastCheckedPosition != -1) {
                items[lastCheckedPosition].check = false
                notifyItemChanged(lastCheckedPosition)
            }
            // Check the newly selected item
            items[position].check = true
            notifyItemChanged(position)
            lastCheckedPosition = position
        }
    }

}

