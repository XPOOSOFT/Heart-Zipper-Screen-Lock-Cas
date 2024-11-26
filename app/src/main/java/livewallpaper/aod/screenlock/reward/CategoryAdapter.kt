package livewallpaper.aod.screenlock.reward

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.ItemCategoryBinding
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.getIcon
import livewallpaper.aod.screenlock.zipper.utilities.toSimpleTimeFormat

class CategoryAdapter(
    private val itemList: List<WallpaperCategory>,private var function: (Boolean, String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {
    val timers = mutableMapOf<Int, CountDownTimer>()
    class ItemViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        holder.binding.topName.text = item.name

        if (item.locked) {
            holder.binding.lockIcon.setImageResource(getIcon()[position])
            holder.binding.categoryName.visibility = View.INVISIBLE
        } else {
            holder.binding.lockIcon.setImageResource(R.drawable.gift_icon)
            holder.binding.categoryName.visibility = View.VISIBLE
                startTimer(position, item.nameDay, holder,item.saveTime)
        }
        holder.itemView.clickWithThrottle {
                function.invoke(item.locked, item.name)
            }
    }
    private fun startTimer(
        position: Int,
        remainingTime: Int,
        holder: ItemViewHolder,
        saveTime: Long
    ) {
        val elapsedTime = (((System.currentTimeMillis()) - saveTime) / 1000).toInt() // Calculate elapsed time in seconds
        if ((remainingTime - elapsedTime) <= 0) {
            // If time has expired, unlock the item immediately
            itemList[position].locked = true // Set locked to false to indicate unlocked
            itemList[position].nameDay = 0
            holder.itemView.post {
                notifyItemChanged(position) // Update UI to reflect unlocked status
            }
        return
        } else {
            // Start timer for remaining time
            timers[position] = object : CountDownTimer((remainingTime - elapsedTime) * 1000L, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    itemList[position].nameDay = (millisUntilFinished / 1000).toInt()
                    holder.binding.categoryName.text = (millisUntilFinished / 1000).toInt().toSimpleTimeFormat()
                }

                override fun onFinish() {
                    // Unlock item when timer finishes
                    itemList[position].locked = true
                    itemList[position].nameDay = 0
                    holder.binding.categoryName.text = ""
                    holder.itemView.post {
                        notifyItemChanged(position) // Update UI safely
                    }
                }
            }.start()
        }
    }

    override fun getItemCount(): Int = itemList.size
    fun cancelTimers() {
        for (timer in timers.values) {
            timer.cancel()
        }
        timers.clear()
    }
}
