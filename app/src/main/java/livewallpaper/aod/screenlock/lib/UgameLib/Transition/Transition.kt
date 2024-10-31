package livewallpaper.aod.screenlock.lib.UgameLib.Transition

object Transition {
    fun easeBounceOut(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        if (d5 < 0.36363636363636365) {
            return d3 * 7.5625 * d5 * d5 + d2
        }
        return if (d5 < 0.7272727272727273) {
            val d6 = d5 - 0.5454545454545454
            d3 * (7.5625 * d6 * d6 + 0.75) + d2
        } else if (d5 < 0.9090909090909091) {
            val d7 = d5 - 0.8181818181818182
            d3 * (7.5625 * d7 * d7 + 0.9375) + d2
        } else {
            val d8 = d5 - 0.9545454545454546
            d3 * (7.5625 * d8 * d8 + 0.984375) + d2
        }
    }

    fun easeInCubic(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return d3 * d5 * d5 * d5 + d2
    }

    fun easeInOutCubic(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        if (d5 < 1.0) {
            return d3 / 2.0 * d5 * d5 * d5 + d2
        }
        val d6 = d5 - 2.0
        return d3 / 2.0 * (d6 * d6 * d6 + 2.0) + d2
    }

    fun easeInOutQuad(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        if (d5 < 1.0) {
            return d3 / 2.0 * d5 * d5 + d2
        }
        val d6 = d5 - 1.0
        return -d3 / 2.0 * (d6 * (d6 - 2.0) - 1.0) + d2
    }

    fun easeInOutQuart(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        if (d5 < 1.0) {
            return d3 / 2.0 * d5 * d5 * d5 * d5 + d2
        }
        val d6 = d5 - 2.0
        return -d3 / 2.0 * (d6 * d6 * d6 * d6 - 2.0) + d2
    }

    fun easeInOutQudouble(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        if (d5 < 1.0) {
            return d3 / 2.0 * d5 * d5 * d5 * d5 * d5 + d2
        }
        val d6 = d5 - 2.0
        return d3 / 2.0 * (d6 * d6 * d6 * d6 * d6 + 2.0) + d2
    }

    fun easeInOutQuint(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        if (d5 < 1.0) {
            return d3 / 2.0 * d5 * d5 * d5 * d5 * d5 + d2
        }
        val d6 = d5 - 2.0
        return d3 / 2.0 * (d6 * d6 * d6 * d6 * d6 + 2.0) + d2
    }

    fun easeInQuad(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return d3 * d5 * d5 + d2
    }

    fun easeInQuart(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return d3 * d5 * d5 * d5 * d5 + d2
    }

    fun easeInQudouble(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return d3 * d5 * d5 * d5 * d5 * d5 + d2
    }

    fun easeInQuint(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return d3 * d5 * d5 * d5 * d5 * d5 + d2
    }

    fun easeOutCubic(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4 - 1.0
        return d3 * (d5 * d5 * d5 + 1.0) + d2
    }

    fun easeOutQuad(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return -d3 * d5 * (d5 - 2.0) + d2
    }

    fun easeOutQuart(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4 - 1.0
        return -d3 * (d5 * d5 * d5 * d5 - 1.0) + d2
    }

    fun easeOutQudouble(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4 - 1.0
        return d3 * (d5 * d5 * d5 * d5 * d5 + 1.0) + d2
    }

    fun easeOutQuint(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4 - 1.0
        return d3 * (d5 * d5 * d5 * d5 * d5 + 1.0) + d2
    }

    fun linearTween(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return d3 * d / d4 + d2
    }

    @JvmStatic
    fun GetValue(
        transition_Type: Transition_Type,
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double
    ): Double {
        return if (transition_Type == Transition_Type.easeInCirc) {
            easeInCirc(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInCubic) {
            easeInCubic(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInExpo) {
            easeInExpo(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutCirc) {
            easeInOutCirc(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutCubic) {
            easeInOutCubic(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutExpo) {
            easeInOutExpo(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutQuad) {
            easeInOutQuad(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutQuart) {
            easeInOutQuart(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutQuint) {
            easeInOutQuint(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInOutSine) {
            easeInOutSine(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInQuad) {
            easeInQuad(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInQuart) {
            easeInQuart(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInQuint) {
            easeInQuint(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInSine) {
            easeInSine(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutCirc) {
            easeOutCirc(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutCubic) {
            easeOutCubic(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutExpo) {
            easeOutExpo(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutQuad) {
            easeOutQuad(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutQuart) {
            easeOutQuart(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutQuint) {
            easeOutQuint(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutSine) {
            easeOutSine(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.linearTween) {
            linearTween(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.Special1) {
            easeInOutQuad(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.Special2) {
            easeInOutQuart(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.Special3) {
            easeOutCubic(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.Special4) {
            easeOutSine(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeInBoune) {
            easeBounceIn(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutbounce) {
            easeBounceOut(d, d2, d3 - d2, d4)
        } else if (transition_Type == Transition_Type.easeOutbounce) {
            easeBounceInOut(d, d2, d3 - d2, d4)
        } else {
            easeInOutSine(d, d2, d3 - d2, d4)
        }
    }

    fun easeInSine(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return -d3 * Math.cos(d / d4 * 1.5707963267948966) + d3 + d2
    }

    fun easeOutSine(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return d3 * Math.sin(d / d4 * 1.5707963267948966) + d2
    }

    fun easeInOutSine(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return -d3 / 2.0 * (Math.cos(3.141592653589793 * d / d4) - 1.0) + d2
    }

    fun easeInExpo(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return d3 * Math.pow(2.0, 10.0 * (d / d4 - 1.0)) + d2
    }

    fun easeOutExpo(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return d3 * (-Math.pow(2.0, -10.0 * d / d4) + 1.0) + d2
    }

    fun easeInOutExpo(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        return if (d5 < 1.0) {
            d3 / 2.0 * Math.pow(2.0, 10.0 * (d5 - 1.0)) + d2
        } else d3 / 2.0 * (-Math.pow(2.0, -10.0 * (d5 - 1.0)) + 2.0) + d2
    }

    fun easeInCirc(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4
        return -d3 * (Math.sqrt(1.0 - d5 * d5) - 1.0) + d2
    }

    fun easeOutCirc(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / d4 - 1.0
        return d3 * Math.sqrt(1.0 - d5 * d5) + d2
    }

    fun easeInOutCirc(d: Double, d2: Double, d3: Double, d4: Double): Double {
        val d5 = d / (d4 / 2.0)
        if (d5 < 1.0) {
            return -d3 / 2.0 * (Math.sqrt(1.0 - d5 * d5) - 1.0) + d2
        }
        val d6 = d5 - 2.0
        return d3 / 2.0 * (Math.sqrt(1.0 - d6 * d6) + 1.0) + d2
    }

    fun easeBounceIn(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return d3 - easeBounceOut(d4 - d, 0.0, d3, d4) + d2
    }

    fun easeBounceInOut(d: Double, d2: Double, d3: Double, d4: Double): Double {
        return if (d < d4 / 2.0) {
            easeBounceIn(2.0 * d, 0.0, d3, d4) * 0.5 + d2
        } else easeBounceOut(
            d * 2.0 - d4,
            0.0,
            d3,
            d4
        ) * 0.5 + d3 * 0.5 + d2
    }
}