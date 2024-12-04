package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Timer
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect.list


object AnimationAdapter {
    fun Init() {
        Deplace.list = ArrayList()
        Fade.list = ArrayList()
        Sizer.list = ArrayList()
        FadeInOut.list = ArrayList()
        Rotation.list = ArrayList()
        Timer.list = ArrayList()
    }

    fun Update() {
        Deplace.update()
        Fade.update()
        Sizer.update()
        FadeInOut.update()
        Rotation.update()
    }

    fun CleareMemory() {
        if (Deplace.list != null) {
            Deplace.list?.clear()
            Deplace.list = null
        }
        if (Timer.list != null) {
            Timer.list.clear()
            Timer.list = null
        }
        if (Fade.list != null) {
            Fade.list?.clear()
            Fade.list = null
        }
        if (Sizer.list != null) {
            Sizer.list?.clear()
            Sizer.list = null
        }
        if (FadeInOut.list != null) {
            FadeInOut.list?.clear()
            FadeInOut.list = null
        }
        if (Rotation.list != null) {
            Rotation.list?.clear()
            Rotation.list = null
        }
//        if (Urect.list != null) {
//            Urect.list.clear()
//            Urect.list = null
//        }
        if (list != null) {
            list?.clear()
            list = null
        }
//        if (UimagePart.list != null) {
//            UimagePart.list.clear()
//            UimagePart.list = null
//        }
//        if (ULabel.list != null) {
//            ULabel.list.clear()
//            ULabel.list = null
//        }
//        if (UTriangle.list != null) {
//            UTriangle.list.clear()
//            UTriangle.list = null
//        }
    }
}