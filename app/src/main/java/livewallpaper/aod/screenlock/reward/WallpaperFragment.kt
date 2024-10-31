package livewallpaper.aod.screenlock.reward

import android.content.Context
import android.os.Bundle
import android.util.Log
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
        val prefs = RewardPreferences(requireContext())
//        val currentDay = prefs.getCurrentDay()
//        prefs.setCurrentDay(currentDay + 2)

        //This Code For Unlock Day
//        prefs.incrementUnlockDay(activity?.applicationContext?:requireContext())
        Log.d("TAG_incrementUnlockDay", "onCreateView: ${prefs.getCurrentDay()}")
        Log.d("TAG_incrementUnlockDay", "onCreateView: ${RewardPreferences(requireContext()).getCurrentUnlockDay(requireContext())}")
        unlockNextCategory(requireContext(),view)
        return view
    }

    private fun unlockNextCategory(context: Context, view: View) {
        val prefs = RewardPreferences(context)
        var currentDay = prefs.getCurrentDay()
        setupRecyclerView(view)
        if (--currentDay <= RewardConstants.TOTAL_DAYS) {
            prefs.setCurrentDay(currentDay + 1)
            prefs.setLastOpenDate(System.currentTimeMillis())
            loadCategories()
        } else {
            prefs.incrementUnlockDay(activity?.applicationContext?:requireContext())
            loadCategories1()
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
    private fun loadCategories1() {
        currentUnlockDay = RewardPreferences(context?:return).getCurrentUnlockDay(requireContext())

        // Assume 8 categories for simplicity
        val allCategories = (1..8).map {
            WallpaperCategory(name = "Category $it", locked = false)
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }

}
