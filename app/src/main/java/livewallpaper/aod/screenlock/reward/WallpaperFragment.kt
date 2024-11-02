package livewallpaper.aod.screenlock.reward

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.clap.whistle.phonefinder.utilities.DbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.getRemainingTimeUntilMidnight
import livewallpaper.aod.screenlock.zipper.utilities.getRewardTitle
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.startCountdownTimer

class WallpaperFragment : Fragment() {

    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<WallpaperCategory>()
    private var currentUnlockDay = 0
    private var sharedPrefUtils : DbHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallpaper, container, false)
        val prefs = RewardPreferences(requireContext())
        sharedPrefUtils = DbHelper(requireContext())
        //This Code For Unlock Day
//        prefs.incrementUnlockDay(activity?.applicationContext?:requireContext())
        Log.d("TAG_incrementUnlockDay", "onCreateView: ${prefs.getCurrentDay()}")
        Log.d("TAG_incrementUnlockDay", "onCreateView: ${RewardPreferences(requireContext()).getCurrentUnlockDay(requireContext())}")
        unlockNextCategory(requireContext(),view)
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        CoroutineScope(Dispatchers.Main).launch {
            startCountdownTimer(getRemainingTimeUntilMidnight(),view.findViewById(R.id.timeText))
        }
        view.findViewById<ImageFilterView>(R.id.titleBack).clickWithThrottle {
            findNavController().navigateUp()
        }
        return view
    }

    private fun unlockNextCategory(context: Context, view: View) {
        setupRecyclerView(view)
        if(sharedPrefUtils?.getBooleanData(activity?:return, "IS_UNLOCK", false)==false){
            loadCategories1()
            return
        }
        val prefs = RewardPreferences(context)
        val currentDay = prefs.getCurrentUnlockDay(context)
        if (currentDay <= RewardConstants.TOTAL_DAYS ) {
            prefs.setCurrentDay(currentDay + 1)
            prefs.setLastOpenDate(System.currentTimeMillis())
            loadCategories()
        } else {
            loadCategories1()
        }

    }

    private fun setupRecyclerView(view: View) {
        categoryAdapter = CategoryAdapter(categories){_Lock,Tilte_ ->
            if(_Lock){
                showToast(context?:requireContext(),"Lock - $Tilte_")
            }else{
                findNavController().navigate(R.id.ImageListFragment, bundleOf("title" to Tilte_))
//                showToast(context?:requireContext(),"Un Lock - $Tilte_")
            }
        }
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = categoryAdapter
    }

    private fun loadCategories() {
        currentUnlockDay = RewardPreferences(context?:return).getCurrentUnlockDay(requireContext())

        // Assume 8 categories for simplicity
        val allCategories = (1..8).map {
            WallpaperCategory(name = getRewardTitle(context?:return)[it-1], nameDay = "Day $it", locked = it > currentUnlockDay)
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }
    private fun loadCategories1() {
        // Assume 8 categories for simplicity
        val allCategories = (1..8).map {
            WallpaperCategory(name = getRewardTitle(context?:return)[it-1], nameDay = "Day $it", locked = false)
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }

}
