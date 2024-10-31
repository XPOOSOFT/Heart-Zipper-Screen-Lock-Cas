package livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters

import android.util.Log

class ULooper : Runnable {
    var Gamethread: Thread? = null
    var isRunning = false
    var updateListner: MutableList<UpdateListner?>? = null

    interface UpdateListner {
        fun Updatee()
    }

    fun Resume() {
        isRunning = true
        updateListner = ArrayList()
        Gamethread = Thread(this)
        Gamethread?.start()
        Update()
    }

    fun Pause() {
        isRunning = false
    }

    init {
        isRunning = true
    }

    override fun run() {
        while (isRunning) {
            if (Update()) {
                try {
                    val thread = Gamethread
                    Thread.sleep(sleep.toLong())
                } catch (e: InterruptedException) {
                }
                ct++
            } else {
                isRunning = false
            }
        }
    }

    fun Update(): Boolean {
        return GameAdapter.Update()
    }

    fun addOnUpdateListner(updateListner2: UpdateListner?) {
        updateListner?.add(updateListner2)
    }

    companion object {
        var ct = 0
        var sleep = 1
    }
}