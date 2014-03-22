package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends SurfaceView {
    private String TAG = "MainView";
    private List<Bunny> bunnies;
    private int num_bunnies = 4;

    private PerfStats perf_stats;

    private long last_update_time;

    public MainView(Context context) {
        super(context);
        setWillNotDraw(false);

        bunnies = new ArrayList<Bunny>(num_bunnies);
        for (int i = 0; i < num_bunnies; i++) {
            bunnies.add(new Bunny(context));
        }

        perf_stats = new PerfStats();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        update();
        draw_(canvas);

        perf_stats.onDraw(canvas);
        invalidate();
    }

    private void draw_(Canvas canvas) {
        for (Bunny bunny : bunnies) {
            bunny.draw(canvas);
        }
    }

    private void update() {
        for (Bunny bunny : bunnies) {
            bunny.update();
        }
    }
}
