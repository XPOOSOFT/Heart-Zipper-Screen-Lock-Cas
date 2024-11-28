package livewallpaper.aod.screenlock.zipper.zip_custom

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.ItemImageCustomBinding

class CustomWallpaperAdapter(
    private val context: Context,
    private val wallpapers: List<Int>,
    private val function: (Int) -> Unit
) : RecyclerView.Adapter<CustomWallpaperAdapter.ViewHolder>() {

    private var selected = 0

    inner class ViewHolder(private val binding: ItemImageCustomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(wallpaper: Int, position: Int) {
            Glide.with(context?: return).load(wallpaper).placeholder(R.drawable.error_ic).into(binding.previewImageView)

            if (position == selected) {
                Glide.with(context?: return).load(R.drawable.check).into(binding.ticket)
            } else {
                Glide.with(context?: return).load(R.drawable.uncheck).into(binding.ticket)
            }
            binding.root.setOnClickListener { function(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageCustomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wallpapers[position],position)

    }

    override fun getItemCount() = wallpapers.size

    fun updateItem(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

}