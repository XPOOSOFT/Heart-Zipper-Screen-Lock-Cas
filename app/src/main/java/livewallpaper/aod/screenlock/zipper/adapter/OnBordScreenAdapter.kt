package livewallpaper.aod.screenlock.zipper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import livewallpaper.aod.screenlock.zipper.databinding.IntroLayoutBinding
import livewallpaper.aod.screenlock.zipper.utilities.introDetailTextNew
import livewallpaper.aod.screenlock.zipper.utilities.introHeadingNew
import livewallpaper.aod.screenlock.zipper.utilities.slideImages

class OnBordScreenAdapter(private val context: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return introHeadingNew(context).size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = IntroLayoutBinding.inflate(LayoutInflater.from(context), container, false)
        val viewHolder = ViewHolder(binding)

        with(viewHolder) {
            imageView.setImageResource(slideImages[position])
            textViewHeading.text = introHeadingNew(context)[position].toString()
            textViewContent.text = introDetailTextNew(context)[position].toString()
        }
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private class ViewHolder(val binding: IntroLayoutBinding) {
        val imageView = binding.sliderImage
        val textViewHeading = binding.sliderHeading
        val textViewContent = binding.sliderDesc
    }
}
