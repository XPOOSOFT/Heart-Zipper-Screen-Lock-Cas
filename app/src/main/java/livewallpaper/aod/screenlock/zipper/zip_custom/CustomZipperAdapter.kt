package livewallpaper.aod.screenlock.zipper.zip_custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle

class CustomZipperAdapter(
    private val context: Context,
    private val items: List<Int>,
    private val function: (Int) -> Unit
) : RecyclerView.Adapter<CustomZipperAdapter.ZipperViewHolder>() {
    private var selected = 0

    inner class ZipperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.previewImageView)
        val ticket: ImageFilterView = itemView.findViewById(R.id.ticket)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZipperViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_custom, parent, false)
        return ZipperViewHolder(view)
    }

    override fun onBindViewHolder(holder: ZipperViewHolder, position: Int) {
        holder.imageView.setImageResource(items[position])
        if (position == selected) {
            Glide.with(context?: return).load(R.drawable.check).into(holder.ticket)
        } else {
            Glide.with(context?: return).load(R.drawable.uncheck).into(holder.ticket)
        }
        holder.itemView.clickWithThrottle {
            function.invoke(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItem(position: Int) {
        selected = position
        notifyDataSetChanged()
    }
}