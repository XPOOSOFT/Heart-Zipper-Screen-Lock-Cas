package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type


class Scaler {
    constructor(
        urect: Urect,
        d: Double,
        d2: Double,
        d3: Double,
        transition_Type: Transition_Type?,
        i: Int
    ) {
        Sizer(urect, urect.Width(), urect.Height(), d, d2, d3, transition_Type?:return , i)
        Deplace(
            urect,
            urect.relativeLeft,
            urect.relativeTop,
            urect.relativeLeft - (d - urect.Width()) / 2.0,
            urect.top - (d2 - urect.Height()) / 2.0,
            d3,
            transition_Type!!,
            i.toDouble()
        )
    }

    constructor(
        urect: Urect,
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double,
        transition_Type: Transition_Type?,
        i: Int
    ) {
        Sizer(urect, d, d2, d3, d4, d5, transition_Type?:return , i)
        Deplace(
            urect,
            urect.relativeLeft,
            urect.relativeTop,
            urect.relativeLeft - (d3 - d) / 2.0,
            urect.relativeTop - (d4 - d2) / 2.0,
            d5,
            transition_Type!!,
            i.toDouble()
        )
    }
}