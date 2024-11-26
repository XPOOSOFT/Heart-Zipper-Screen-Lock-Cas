package livewallpaper.aod.screenlock.reward.wallpaper// ImageAdapter.kt
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
        Glide.with(holder.itemView.context)
            .asBitmap() // Ensure Glide loads the image as a Bitmap
            .load(imageUrl)
            .placeholder(R.drawable.rounded_placeholder) // Show placeholder while loading
            .error(R.drawable.rounded_placeholder) // Show error drawable if loading fails
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // When the image is successfully loaded, set it to the ImageView
                    holder.previewImageView.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    // Set the error drawable if the image fails to load
                    holder.previewImageView.setImageDrawable(errorDrawable)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Clear resources if the request is cancelled or no longer needed
                    holder.previewImageView.setImageDrawable(placeholder)
                }
            })
        holder.itemView.setOnClickListener {
            onImageClick(imageUrl)
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val previewImageView: ImageFilterView = view.findViewById(R.id.previewImageView)
    }
}
