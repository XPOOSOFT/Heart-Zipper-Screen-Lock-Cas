package livewallpaper.aod.screenlock.zipper.zip_custom

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ZipperAdapter(private val items: List<Int>) : RecyclerView.Adapter<ZipperAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ImageView) : RecyclerView.ViewHolder(binding.rootView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(100, 100)
        }
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setImageResource(items[position])
    }

    override fun getItemCount(): Int = items.size
}
