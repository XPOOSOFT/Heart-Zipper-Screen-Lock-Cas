package livewallpaper.aod.screenlock.reward

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle

class CategoryAdapter(
    private var categories: List<WallpaperCategory>,
    private var function: (Boolean, String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<WallpaperCategory>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoryName: TextView = view.findViewById(R.id.topName)
        private val categoryDay: TextView = view.findViewById(R.id.categoryName)
        private val lockIcon: ImageView = view.findViewById(R.id.lockIcon)

        fun bind(category: WallpaperCategory) {
            categoryName.text = category.name
            categoryDay.text = category.nameDay
            lockIcon.setImageResource(if (!category.locked) R.drawable.unlock_image_day else R.drawable.gift_icon)
            itemView.clickWithThrottle {
                function.invoke(category.locked, category.name)
            }
        }
    }
}
