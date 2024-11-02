package livewallpaper.aod.screenlock.zipper.wallpaper// ImageAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.R

class ImageAdapter(
    private val images: List<String>,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context).load(imageUrl).placeholder(R.drawable.gift_icon).into(holder.previewImageView)
        holder.itemView.setOnClickListener {
            onImageClick(imageUrl)
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val previewImageView: ImageFilterView = view.findViewById(R.id.previewImageView)
    }
}
