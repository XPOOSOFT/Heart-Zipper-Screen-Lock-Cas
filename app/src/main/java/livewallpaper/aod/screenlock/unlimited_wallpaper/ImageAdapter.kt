package livewallpaper.aod.screenlock.unlimited_wallpaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import livewallpaper.aod.screenlock.zipper.R

class ImageAdapter(private val images: List<ImageData>,
                   private val onItemClick: (ImageData) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageFilterView = itemView.findViewById(R.id.previewImageView)
        val userTextView: TextView = itemView.findViewById(R.id.userTextView)
        init {
            itemView.setOnClickListener {
                onItemClick(images[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_new, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Picasso.get().load(image.previewURL).into(holder.imageView)
        holder.userTextView.text = image.user
    }

    override fun getItemCount(): Int = images.size
}
