package livewallpaper.aod.screenlock.lib.UgameLib.Shapes

import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Screen


class AliveRect {
    var Gravity = true
    var GravityS: Double
    var Sx: Double
    var Sy: Double
    var rect: Urect

    constructor(urect: Urect, d: Double, d2: Double, z: Boolean) {
        rect = urect
        Sx = d
        Sy = d2
        Gravity = z
        GravityS = Screen.Width / 130000.0
        InstalUpdateMethod()
    }

    constructor(urect: Urect, z: Boolean) {
        rect = urect
        Sx = 0.0
        Sy = 0.0
        Gravity = z
        GravityS = Screen.Width / 130000.0
        InstalUpdateMethod()
    }

    /* access modifiers changed from: package-private */
    fun InstalUpdateMethod() {
//        rect.OnUpdateListner {
//            if (Gravity) {
//                Sy += GravityS
//            }
//            rect.left = rect.left + Sx
//            rect.top = rect.top + Sy
//        }
    }
}