package livewallpaper.aod.screenlock.zipper.zip_custom.adapter

import android.annotation.SuppressLint
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

class CustomAdapter(
    private val context: Context,
    private val function: (Int) -> Unit
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private val items = mutableListOf<Int>()
    private var selected = 0
    private var positionT = 0

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<Int>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.zipper_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position],positionT,context)
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

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(imageRes: Int,positionT :Int,context : Context) {
            if(positionT==1) {
                Glide.with(context?:return).load(imageRes).into(view.findViewById<ImageFilterView>(R.id.imageWallpaper))
                view.findViewById<ImageFilterView>(R.id.image).setImageDrawable(null)
            }
            else{
                view.findViewById<ImageFilterView>(R.id.imageWallpaper).setImageDrawable(null)
                Glide.with(context?:return).load(imageRes).into(view.findViewById<ImageFilterView>(R.id.image))
                }
        }
        val ticket: ImageView = itemView.findViewById(R.id.ticket)
    }


    fun updateItem(position: Int,positionType : Int) {
        selected = position
        positionT = positionType
        notifyDataSetChanged()
    }

}
