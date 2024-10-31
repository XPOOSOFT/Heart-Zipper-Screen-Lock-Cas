package livewallpaper.aod.screenlock.zipper.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref

class ZippersRecyclerAdapter(private val list: List<Int>, var pref: String, val context: Context?, val isWallpaper: Boolean?) :
    RecyclerView.Adapter<ZippersRecyclerAdapter.MyViewHolder>() {
    var selected = 0
    var onClick: ((Int) -> Unit)? = null

    inner class MyViewHolder(private var parent: View) : RecyclerView.ViewHolder(
        parent
    ) {
        var image: ImageFilterView
        var selectedTicket: ImageView

        init {
            image = parent.findViewById(R.id.image)
            selectedTicket = parent.findViewById(R.id.ticket)
        }
    }

    init {
        selected = context?.let { DataBasePref.LoadPref(pref, it) }?: 0
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return if(isWallpaper == true){
            MyViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.zipper_item_wallpaper_layout, viewGroup, false)
            )
        }else{
            MyViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.zipper_item_layout, viewGroup, false)
            )
        }
    }

    override fun getItemViewType(i: Int): Int {
        return if (i < list.size || i == 0) 0 else 1
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        Glide.with(context ?: return).load(list[i]).thumbnail().into(myViewHolder.image)
        if (i == selected) {
            Glide.with(context ?: return).load(R.drawable.check).into(myViewHolder.selectedTicket)
        } else {
            Glide.with(context ?: return).load(R.drawable.uncheck).into(myViewHolder.selectedTicket)
        }
        myViewHolder.image.setOnClickListener { view ->
            onClick?.invoke(i)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateItem(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

}