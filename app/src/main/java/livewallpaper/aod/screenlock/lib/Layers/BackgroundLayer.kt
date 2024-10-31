package livewallpaper.aod.screenlock.lib.Layers

import livewallpaper.aod.screenlock.lib.Media.Media
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Screen
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Uimage


object BackgroundLayer {
    var bg: Uimage? = null
    fun Inicial() {
        bg = Uimage(0.0, 0.0, Screen.Width, Screen.Height, Media.SelectedBackBg)
        GameAdapter.GetMainRect()?.AddChild(bg?:return)
    }

    fun clearMemory() {
        if (bg != null) {
            bg?.image = null
            bg = null
        }
    }
}