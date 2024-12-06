package livewallpaper.aod.screenlock.unlimited_wallpaper

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import livewallpaper.aod.screenlock.zipper.databinding.ItemCategoryBinding
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle

class CategoryAdapter(
    private val itemList: List<Category>, private var function: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        Picasso.get().load(item.thumbnil_image_url).into(holder.binding.lockIcon)
        holder.itemView.clickWithThrottle {
            item.qurey?.let { function.invoke(it) }
            }
    }

    override fun getItemCount(): Int = itemList.size
}
