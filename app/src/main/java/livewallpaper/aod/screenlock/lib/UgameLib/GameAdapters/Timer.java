package livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters;

import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class Timer {
    public static List<Timer> list = new ArrayList();
    public boolean AlreadyFinished = false;
    public int CurentTime;
    public boolean Done = false;
    public int MaxTime;
    public boolean Pause = true;
    List<TimerFinishListner> TimerFinishListner = new ArrayList();
    List<TimerStepListner> TimerSteps = new ArrayList();



    public void OnTimerFinishCounting(TimerFinishListner timerFinishListner) {
        this.TimerFinishListner.add(timerFinishListner);
    }

    public void OnEveryStep(TimerStepListner timerStepListner) {
        this.TimerSteps.add(timerStepListner);
    }

    public Timer(int i, int i2) {
        if (list == null) {
            list = new ArrayList();
        }
        this.MaxTime = i;
        this.CurentTime = i2;
        list.add(this);
    }

    public boolean IsFinished() {
        return this.CurentTime >= this.MaxTime || this.Done;
    }

    public boolean isPaused() {
        return this.Pause;
    }

    public boolean update() {
        if (IsFinished()) {
            if (!this.AlreadyFinished) {
                this.AlreadyFinished = true;
                for (TimerFinishListner DoWork : this.TimerFinishListner) {
                    DoWork.DoWork(this);
                }
            }
            return true;
        } else if (isPaused()) {
            return false;
        } else {
            this.CurentTime++;
            for (TimerStepListner DoWork2 : this.TimerSteps) {
                DoWork2.DoWork(this.CurentTime, this);
            }
            return false;
        }
    }

    public void Restart() {
        this.AlreadyFinished = false;
        this.Pause = false;
        this.CurentTime = 0;
    }

    public static void Update() {
        if (list == null) {
        } else if (list.size() != 0) {
            int i = 0;
            while (i < list.size()) {
                if (list.get(i).Done) {
                    list.remove(i);
                    i--;
                } else {
                    list.get(i).update();
                }
                i++;
            }
        }
    }

    public void start() {
        this.Pause = false;
        if (!list.contains(this)) {
            list.add(this);
        }
    }

    public void resume() {
        this.Pause = false;
    }

    public void Remove() {
        this.Done = true;
    }

    public void pause() {
        this.Pause = true;
    }

    public static void Clear() {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).Delete();
            }
            list.clear();
        }
    }

    private void Delete() {
        this.TimerFinishListner.clear();
        this.TimerSteps.clear();
    }
}
