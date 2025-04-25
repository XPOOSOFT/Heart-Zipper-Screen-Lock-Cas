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
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle

class ZippersRecyclerAdapter(
                             private val list1: Int,
                             private val list: List<Int>,
                             private var pref: String,
                             val context: Context?
) :
    RecyclerView.Adapter<ZippersRecyclerAdapter.MyViewHolder>() {
    private var selected = 0
    var onClick: ((Int) -> Unit)? = null

    inner class MyViewHolder(private var parent: View) : RecyclerView.ViewHolder(
        parent
    ) {
        var image: ImageFilterView
        var imageWallpaper: ImageFilterView
        var selectedTicket: ImageView

        init {
            image = parent.findViewById(R.id.image)
            imageWallpaper = parent.findViewById(R.id.imageWallpaper)
            selectedTicket = parent.findViewById(R.id.ticket)
        }
    }

    init {
        selected = DataBasePref.LoadPref(pref, context!!)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.zipper_item_layout, viewGroup, false)
        )
    }

    override fun getItemViewType(i: Int): Int {
        return if (i < list.size || i == 0) 0 else 1
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        if(list1==0){
            Glide.with(context ?: return).load(list[i]).apply(RequestOptions()
                .centerCrop() // Ensures the image is scaled correctly within the ImageView
                .transform(RoundedCorners(30)) ).placeholder(R.drawable.error_ic).into(myViewHolder.imageWallpaper)
        }else{
            Glide.with(context ?: return).load(list[i]).thumbnail().placeholder(R.drawable.error_ic).into(myViewHolder.image)
        }
        if (i == selected) {
            Glide.with(context?: return).load(R.drawable.check).into(myViewHolder.selectedTicket)
        } else {
            Glide.with(context?: return).load(R.drawable.uncheck).into(myViewHolder.selectedTicket)
        }
        myViewHolder.itemView.clickWithThrottle {
            onClick?.invoke(i)
        }
    }

    override fun getItemCount(): Int {
        return list.size?:return 0
    }

    fun updateItem(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

}