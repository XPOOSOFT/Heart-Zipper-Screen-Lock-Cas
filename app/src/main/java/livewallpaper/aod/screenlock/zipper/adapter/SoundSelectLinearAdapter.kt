package livewallpaper.aod.screenlock.zipper.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.ItemDetectionSoundLinearBinding
import livewallpaper.aod.screenlock.zipper.model.SoundModel

class SoundSelectLinearAdapter(
    private var clickItem: ((Int) -> Unit),private var onClick: ((Int) -> Unit), private var soundData: ArrayList<SoundModel>

) : RecyclerView.Adapter<SoundSelectLinearAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemDetectionSoundLinearBinding) :
        RecyclerView.ViewHolder(binding.root)

    var context: Context? = null
    private var playingPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemDetectionSoundLinearBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return soundData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.countryName.text = soundData[position].soundName + " " + position
        holder.binding.radioButton.isChecked = soundData[position].isCheck
        holder.binding.radioButton.isChecked = soundData[position].isCheck  // Show "play" or "stop" icon based on whether this item is playing
        if (position == playingPosition) {
            holder.binding.flagIcon.setImageResource(R.drawable.ic_stop) // Replace with stop icon
        } else {
            holder.binding.flagIcon.setImageResource(R.drawable.ic_play) // Replace with play icon
        }

        holder.itemView.setOnClickListener {
            if (playingPosition == position) {
                // If the current item is playing, stop it
                playingPosition = -1
                notifyItemChanged(position) // Update UI
            } else {
                // Play the new item
                val oldPosition = playingPosition
                playingPosition = position
                notifyItemChanged(oldPosition) // Update the previous playing item
                notifyItemChanged(playingPosition) // Update the new playing item
            }
            checkSingleBox(position)
            clickItem.invoke(position)
        }
        holder.binding.radioButton.setOnClickListener {
            if (playingPosition == position) {
                // If the current item is playing, stop it
                playingPosition = -1
                notifyItemChanged(position) // Update UI
            } else {
                // Play the new item
                val oldPosition = playingPosition
                playingPosition = position
                notifyItemChanged(oldPosition) // Update the previous playing item
                notifyItemChanged(playingPosition) // Update the new playing item
            }
            checkSingleBox(position)
            clickItem.invoke(position)
        }
//        holder.binding.radioButton.setOnClickListener {
//            checkSingleBox(position)
//            clickItem.invoke(position)
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectLanguage(langPositionSelected: Int) {
        for (i in soundData.indices) {
            soundData[i].isCheck = i == langPositionSelected
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun checkSingleBox(pos: Int) {
        for (i in soundData.indices) {
            soundData[i].isCheck = i == pos
        }
        notifyDataSetChanged()
    }

    fun onSoundComplete() {
        val oldPosition = playingPosition
        playingPosition = -1
        notifyItemChanged(oldPosition) // Reset the icon of the item that was playing
    }

}