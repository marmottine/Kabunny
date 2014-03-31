package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.os.SystemClock;
import android.graphics.Color;
import android.view.SurfaceView;

public class MainView extends SurfaceView {
    private String TAG = "MainView";
    private int num_bunnies = 16;
    private Bunny[] bunnies = new Bunny[num_bunnies];
    private Grass[] grasses;
    private int num_grasses = 25;

    private PerfStats perf_stats;

    private long last_update_time = 0;
    private long cur_simtime = 0;
    private long delta_simtime = 0;
    private long min_fps = 1000/20; // max delta time between frames

    public MainView(Context context) {
        super(context);
        setWillNotDraw(false);
        setKeepScreenOn(true);
        setBackgroundColor(Color.rgb(0x8B, 0xD8, 0x37));

        grasses = new Grass[num_grasses];
        for (int i = 0; i < num_grasses; i++) {
            grasses[i] = new Grass(context);
        }

        bunnies = new Bunny[num_bunnies];
        for (int i = 0; i < num_bunnies; i++) {
            bunnies[i] = new Bunny(context, null, null, null, 0f, null);
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

    /**
     * Collide two circles conserving momentum
     */
    private void elasticCollision(Bunny bunny1, Bunny bunny2) {
        // compute a normalized vector between centers
        Vector2 normal = bunny1.position.clone().
                sub(bunny2.position).
                normalize();

        // project velocity onto the normalized vector
        float velocity1 = bunny1.velocity.dot(normal);
        float velocity2 = bunny2.velocity.dot(normal);

        float momentum = 2f * (velocity1 - velocity2) / (bunny1.mass + bunny2.mass);

        bunny1.velocity.sub(normal.clone().scl(momentum * bunny2.mass));
        bunny2.velocity.add(normal.scl(momentum * bunny1.mass));
    }

    private float epsilon = 1e-9f;

    /**
     * Determine if two circles are approaching each other
     */
    private boolean approaching(Bunny bunny1, Bunny bunny2) {
        // compute a vector between centers
        Vector2 normal = bunny1.position.clone().sub(bunny2.position);
        // compute the relative velocity
        Vector2 relative_velocity = bunny2.velocity.clone().sub(bunny1.velocity);
        return normal.dot(relative_velocity) + epsilon > 0;
    }

    private void checkCollision(Bunny bunny1, Bunny bunny2) {
        Vector2 diff = bunny1.position.clone().sub(bunny2.position);
        float distance = diff.len();

        if (approaching(bunny1, bunny2) &&
            distance < bunny1.radius + bunny2.radius) {
            elasticCollision(bunny1, bunny2);
        }
    }

    private long timeToCollision(Bunny bunny1, Bunny bunny2) {
        // A collision is when distance(center_pos1, center_pos2) - (radius1 + radius2) = 0,
        // where center_pos = pos + t * vel, developing this yields a*t^2 + b*t + c = 0
        // where a = (vel1 - vel2)^2, b = 2*(pos1-pos2)*(vel1 - vel2) and
        // c = (pos1 - pos2)^2 - (radius1 + radius2)^2
        Vector2 delta_vel = bunny1.velocity.clone().sub(bunny2.velocity);
        float a = delta_vel.dot(delta_vel);

        Vector2 delta_pos = bunny1.position.clone().sub(bunny2.position);
        float b = 2 * delta_vel.dot(delta_pos);

        float radius_sum = bunny1.radius + bunny2.radius;
        float c = delta_pos.dot(delta_pos) - radius_sum * radius_sum;

        float discriminant = b*b - 4*a*c;
        if (discriminant >= 0) {
            float sqrt_d = (float) Math.sqrt(discriminant);
            long t0 = (long)((-b - sqrt_d) / (2 * a));
            long t1 = (long)((-b + sqrt_d) / (2 * a));
            if (t0 < 1) {
                t0 = Long.MAX_VALUE;
            }
            if (t1 < 1) {
                t1 = Long.MAX_VALUE;
            }
            return Math.min(t0, t1);
        } else {
            return Long.MAX_VALUE;
        }
    }

    private void update(Canvas canvas) {
        perf_stats.update();

        long update_time = SystemClock.elapsedRealtime();
        long target_simtime = Math.min(update_time - last_update_time, min_fps);
        last_update_time = update_time;

        int sims = 0;
        do {
            sims++;

            long delta_simtime = target_simtime;
            for (int i = 0; i < num_bunnies - 1; i++) {
                Bunny bunny1 = bunnies[i];
                for (int j = i + 1; j < num_bunnies; j++) {
                    Bunny bunny2 = bunnies[j];
                    delta_simtime = Math.min(delta_simtime, timeToCollision(bunny1, bunny2));
                    if (delta_simtime == 1) break;
                }
            }

            target_simtime -= delta_simtime;
            cur_simtime += delta_simtime;

            // update bunnies individually
            for (Bunny bunny : bunnies) {
                bunny.update(delta_simtime);
            }

            // check bunny-to-bunny collisions
            for (int i = 0; i < num_bunnies - 1; i++) {
                Bunny bunny1 = bunnies[i];
                for (int j = i + 1; j < num_bunnies; j++) {
                    Bunny bunny2 = bunnies[j];
                    checkCollision(bunny1, bunny2);
                }
            }

            // get "wall" coordinates
            int top_wall = 0;
            int bottom_wall = canvas.getHeight();
            int left_wall = 0;
            int right_wall = canvas.getWidth();

            // check bunny-to-wall collisions
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
        } while(target_simtime > 0);

        float energy = 0;
        float momentum = 0;
        for (Bunny bunny : bunnies) {
            energy += 0.5 * bunny.mass * bunny.velocity.len2();
            momentum += bunny.mass * bunny.velocity.len();
        }
        Log.i("energy / momentum", "" + energy + " / " + momentum);
        //if (sims != 1) Log.i("co", "t" + sims);
    }
}
