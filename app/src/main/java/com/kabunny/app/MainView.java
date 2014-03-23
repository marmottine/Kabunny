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

    public MainView(Context context) {
        super(context);
        setWillNotDraw(false);
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
        super.onDraw(canvas);

        update(canvas);
        draw_(canvas);

        perf_stats.onDraw(canvas);
        invalidate();
    }

    private void draw_(Canvas canvas) {
        for (Grass grass : grasses) {
            grass.draw(canvas);
        }

        for (Bunny bunny : bunnies) {
            bunny.draw(canvas);
        }
    }

    private void update(Canvas canvas) {
        if (last_update_time == 0) {
            last_update_time = SystemClock.elapsedRealtime();
        } else {
            long now = SystemClock.elapsedRealtime();
            long delta = now - last_update_time;
            last_update_time = now;

            // update bunnies individually
            for (Bunny bunny : bunnies) {
                bunny.update(delta);
            }

            // get "wall" coordinates
            int top_wall = 0;
            int bottom_wall = canvas.getHeight();
            int left_wall = 0;
            int right_wall = canvas.getWidth();

            // check collisions
            for (Bunny bunny : bunnies) {
                if ((bunny.x + bunny.radius >= right_wall && bunny.vx > 0)
                        || (bunny.x - bunny.radius <= left_wall && bunny.vx < 0)) {
                    bunny.vx = -bunny.vx;
                }
                if ((bunny.y - bunny.radius <= top_wall && bunny.vy < 0)
                        || (bunny.y + bunny.radius >= bottom_wall && bunny.vy > 0)) {
                    bunny.vy = -bunny.vy;
                }
            }
        }
    }
}
