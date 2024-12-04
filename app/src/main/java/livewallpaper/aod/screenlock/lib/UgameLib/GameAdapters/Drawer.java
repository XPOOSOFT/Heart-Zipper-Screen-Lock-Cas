package livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class Drawer extends SurfaceView implements Runnable {
    public static boolean Share = false;
    public static Canvas canvas;
    public boolean IsFinished = false;
    public List<DrawListner> drawListners = new ArrayList();
    boolean drawedOnce;
    public boolean isRunning;
    SurfaceHolder myHolder = getHolder();
    public Thread myThread = null;
    boolean paused = false;

    public interface DrawListner {
        void OnDraw(Canvas canvas);
    }

    public Drawer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.myHolder.setFormat(-2);
        setDrawingCacheEnabled(true);
    }

    public Drawer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.myHolder.setFormat(-2);
        setDrawingCacheEnabled(true);
    }

    public Drawer(Context context) {
        super(context);
        this.myHolder.setFormat(-2);
        setDrawingCacheEnabled(true);
    }

    public Context GetCtx() {
        return getContext();
    }

    public void pause() {
        this.paused = true;
        this.isRunning = false;
    }

    public void resume() {
        this.paused = false;
        if (!this.isRunning) {
            this.isRunning = true;
            this.myThread = new Thread(this);
            this.myThread.start();
        }
    }

    public void run() {
        while (true) {
            if ((this.isRunning && !this.IsFinished) || !this.drawedOnce) {
                if (this.myHolder.getSurface().isValid()) {
                    Draw();
                    this.drawedOnce = true;
                }
            } else {
                return;
            }
        }
    }

    public Bitmap getBmp() {
        return getDrawingCache();
    }

    private void Draw() {
        canvas = this.myHolder.lockCanvas();
        UpdateDrawing();
        try {
            this.myHolder.unlockCanvasAndPost(canvas);
        } catch (Exception unused) {
        }
    }

    public void UpdateDrawing() {
        if (canvas != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            for (DrawListner OnDraw : this.drawListners) {
                OnDraw.OnDraw(canvas);
            }
        }
    }

    public void addOnDrawListner(DrawListner drawListner) {
        this.drawListners.add(drawListner);
    }

    public void stopDrawing() {
        pause();
    }
}
