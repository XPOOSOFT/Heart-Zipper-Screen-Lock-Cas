package livewallpaper.aod.screenlock.zipper.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.databinding.LanguageAppItemBinding
import livewallpaper.aod.screenlock.zipper.model.LanguageModel

class AppLanguageAdapter(
    private var clickItem: ((LanguageModel) -> Unit), private var languageData: ArrayList<LanguageModel>
) : RecyclerView.Adapter<AppLanguageAdapter.ViewHolder>() {
    class ViewHolder(val binding: LanguageAppItemBinding) : RecyclerView.ViewHolder(binding.root)

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LanguageAppItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return languageData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.countryName.text = languageData[position].country_name.toString()
        holder.binding.radioButton.isChecked = languageData[position].check

        Glide.with(context?:return).load(languageData[position].country_flag).into(holder.binding.flagIcon)
        holder.binding.root.setOnClickListener {
            checkSingleBox(position)
            clickItem.invoke(languageData[position])
        }

        holder.binding.radioButton.setOnClickListener {
            checkSingleBox(position)
            clickItem.invoke(languageData[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectLanguage(langPositionSelected: String) {
        for (i in languageData) {
            i.check = i.country_code == langPositionSelected
        }

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkSingleBox(pos: Int) {
        for (i in languageData.indices) {
            languageData[i].check = i == pos
        }
        notifyDataSetChanged()
    }


}