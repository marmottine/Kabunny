package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.os.SystemClock;
import android.graphics.Color;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MainView extends SurfaceView {
    private String TAG = "MainView";
    private List<Bunny> bunnies;
    private int num_bunnies = 4;
    private List<Grass> grasses;
    private int num_grasses = 25;

    private PerfStats perf_stats;

    private long last_update_time = 0;
    private long cur_simtime = 0;
    private long delta_simtime = 0;
    private long min_fps = 1000/20;

    public MainView(Context context) {
        super(context);
        setWillNotDraw(false);
        setKeepScreenOn(true);
        setBackgroundColor(Color.rgb(0x8B, 0xD8, 0x37));

        grasses = new ArrayList<Grass>(num_grasses);
        for (int i = 0; i < num_grasses; i++) {
            grasses.add(new Grass(context));
        }

        bunnies = new ArrayList<Bunny>(num_bunnies);
        for (int i = 0; i < num_bunnies; i++) {
            bunnies.add(new Bunny(context));
        }

        perf_stats = new PerfStats();
    }

    @Override
    public void onDraw(Canvas canvas) {
        update(canvas);
        super.onDraw(canvas);
        invalidate();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);

        for (Grass grass : grasses) {
            grass.draw(canvas);
        }

        for (Bunny bunny : bunnies) {
            bunny.draw(canvas);
        }

        perf_stats.draw(canvas);
    }

    private void update(Canvas canvas) {
        perf_stats.update();

        long update_time = SystemClock.elapsedRealtime();
        delta_simtime = Math.min(update_time - last_update_time, min_fps);
        cur_simtime += delta_simtime;
        last_update_time = update_time;

        // update bunnies individually
        for (Bunny bunny : bunnies) {
            bunny.update(delta_simtime);
        }

        // get "wall" coordinates
        int top_wall = 0;
        int bottom_wall = canvas.getHeight();
        int left_wall = 0;
        int right_wall = canvas.getWidth();

        // check collisions
        for (Bunny bunny : bunnies) {
            if ((bunny.position.x + bunny.radius >= right_wall && bunny.velocity.x > 0)
                    || (bunny.position.x - bunny.radius <= left_wall && bunny.velocity.x < 0)) {
                bunny.velocity.x = -bunny.velocity.x;
            }
            if ((bunny.position.y - bunny.radius <= top_wall && bunny.velocity.y < 0)
                    || (bunny.position.y + bunny.radius >= bottom_wall && bunny.velocity.y > 0)) {
                bunny.velocity.y = -bunny.velocity.y;
            }
        }
    }
}
