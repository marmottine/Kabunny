package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends SurfaceView {
    private String TAG = "MainView";
    private List<Frog> frogs;
    private int num_frogs = 4;

    private PerfStats perf_stats;

    public MainView(Context context) {
        super(context);
        setWillNotDraw(false);

        frogs = new ArrayList<Frog>(num_frogs);
        for (int i = 0; i < num_frogs; i++) {
            frogs.add(new Frog());
        }

        perf_stats = new PerfStats();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Frog frog: frogs) {
            frog.draw(canvas);
        }

        perf_stats.onDraw(canvas);
        invalidate();
    }
}
