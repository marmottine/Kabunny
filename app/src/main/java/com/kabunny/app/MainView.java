package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends SurfaceView {
    private String TAG = "MainView";
    private List<Frog> frogs;
    private int num_frogs = 4;

    private long last_time;
    private int frame_count;
    private int fps;
    private Paint paint;

    public MainView(Context context) {
        super(context);
        setWillNotDraw(false);

        frogs = new ArrayList<Frog>(num_frogs);
        for (int i = 0; i < num_frogs; i++) {
            frogs.add(new Frog());
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);

        last_time = SystemClock.elapsedRealtime();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Frog frog: frogs) {
            frog.draw(canvas);
        }

        long now = SystemClock.elapsedRealtime();
        if (now - last_time >= 1000) {
            last_time = now;
            fps = frame_count;
            frame_count = 0;
        } else {
            frame_count++;
        }
        canvas.drawText(fps + " fps", 20, 20, paint);
        invalidate();
    }
}
