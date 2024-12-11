package livewallpaper.aod.screenlock.zipper.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.model.MainMenu

class MainMenuAdapter(
    private val items: List<MainMenu>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MainMenuAdapter.MainMenuViewHolder>() {

    inner class MainMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.customizeText)
        private val image = itemView.findViewById<ImageFilterView>(R.id.customizeImage)
        private val detail = itemView.findViewById<TextView>(R.id.customizeTextD)

        fun bind(mainMenu: MainMenu, position: Int) {
            title.text = mainMenu.title
            image.setImageResource(mainMenu.image)
            detail.text = mainMenu.titleDetail

            itemView.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)
        return MainMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainMenuViewHolder, position: Int) {
        holder.bind(items[position],position)
    }

    override fun getItemCount(): Int = items.size
}
