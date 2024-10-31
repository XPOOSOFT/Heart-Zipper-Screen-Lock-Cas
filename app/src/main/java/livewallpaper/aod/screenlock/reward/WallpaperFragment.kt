package livewallpaper.aod.screenlock.reward

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import livewallpaper.aod.screenlock.zipper.R

class WallpaperFragment : Fragment() {

    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<WallpaperCategory>()
    private var currentUnlockDay = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallpaper, container, false)
        unlockNextCategory(requireContext())
        setupRecyclerView(view)
        loadCategories()
        return view
    }

    private fun unlockNextCategory(context: Context) {
        val prefs = RewardPreferences(context)
        val currentDay = prefs.getCurrentDay()
        if (currentDay <= RewardConstants.TOTAL_DAYS) {
            prefs.setCurrentDay(currentDay + 1)
            prefs.setLastOpenDate(System.currentTimeMillis())
        }
    }

    private fun setupRecyclerView(view: View) {
        categoryAdapter = CategoryAdapter(categories)
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = categoryAdapter
    }

    private fun loadCategories() {
        currentUnlockDay = RewardPreferences(context?:return).getCurrentUnlockDay(requireContext())

        // Assume 8 categories for simplicity
        val allCategories = (1..8).map {
            WallpaperCategory(name = "Category $it", locked = it > currentUnlockDay)
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }

}
