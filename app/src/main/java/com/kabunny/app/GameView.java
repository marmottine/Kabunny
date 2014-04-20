package com.kabunny.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.Iterator;
import java.util.LinkedList;

public class GameView extends SurfaceView {
    private String TAG = "GameView";

    private Context context;

    // initial number of bunnies. Actual number varies over time.
    private int num_bunnies = 16;
    private LinkedList<Bunny> bunnies;
    private Grass[] grasses;
    private int num_grasses = 25;
    private Bomb bomb;

    private Rect playground;
    private Rect score_panel;

    private SoundEffectsManager SEM;
    private int sound_ids[];
    private final int bomb_explosion_index = 0;

    private PerfStats perf_stats;

    private long last_update_time = 0;
    private long cur_simtime = 0;
    private long delta_simtime = 0;
    private long min_fps = 1000/20; // max delta time between frames

    public GameView(Context context) {
        super(context);
        setWillNotDraw(false);
        setKeepScreenOn(true);

        this.context = context;

        SEM = new SoundEffectsManager(context);
        init_sounds();

        perf_stats = new PerfStats();
    }

    private void init_sounds() {
        sound_ids = new int[10];
        sound_ids[bomb_explosion_index] = SEM.load(R.raw.bomb);
    }

    private void init_objects(int width, int height) {
        // split width between score panel and playground panel
        int split = width - Math.max(200, (int) (height * 1.5));  // TODO
        score_panel = new Rect(0, 0, split, height);
        playground = new Rect(split, 0, width, height);

        // background
        int playground_color = getResources().getColor(R.color.playground);
        Paint playground_paint = new Paint();
        playground_paint.setColor(playground_color);

        int score_panel_color = getResources().getColor(R.color.score_panel);
        Paint score_panel_paint = new Paint();
        score_panel_paint.setColor(score_panel_color);

        Bitmap targetBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawRect(score_panel, score_panel_paint);
        canvas.drawRect(playground, playground_paint);

        Drawable background = new BitmapDrawable(getResources(), targetBitmap);
        setBackground(background);


        grasses = new Grass[num_grasses];
        for (int i = 0; i < num_grasses; i++) {
            grasses[i] = new Grass(context, playground);
        }

        bunnies = new LinkedList<Bunny>();
        for (int i = 0; i < num_bunnies; i++) {
            bunnies.add(new Bunny(context, playground, null, null, null, null, null));
        }

        bomb = new Bomb(context);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        Log.d(TAG, "size changed: " + xOld + "x" + yOld + " -> " + xNew + "x" + yNew);

        if (xNew == 0 || yNew == 0) {
            return;
        }

        if (xOld == 0 && yOld == 0) {
            // we were just added to the view hierarchy
            // The view has probably just been created.
            init_objects(xNew, yNew);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (bunnies.size() == 0) {
            ((Activity)getContext()).finish();
        }
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

        bomb.draw(canvas);

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

            int i = 0;
            for (Bunny bunny1 : bunnies) {
                int j = 0;
                for (Bunny bunny2 : bunnies) {
                    if (j > i) {
                        delta_simtime = Math.min(delta_simtime, timeToCollision(bunny1, bunny2));
                        if (delta_simtime == 1) break;
                    }
                    j++;
                }
                i++;
            }

            target_simtime -= delta_simtime;
            cur_simtime += delta_simtime;

            // update bunnies individually
            for (Bunny bunny : bunnies) {
                bunny.update(delta_simtime);
            }

            // check bunny-to-bunny collisions
            i = 0;
            for (Bunny bunny1 : bunnies) {
                int j = 0;
                for (Bunny bunny2 : bunnies) {
                    if (j > i) {
                        checkCollision(bunny1, bunny2);
                    }
                    j++;
                }
                i++;
            }

            // check bunny-to-wall collisions
            for (Bunny bunny : bunnies) {
                if ((bunny.position.x + bunny.radius >= playground.right
                            && bunny.velocity.x > 0)
                        || (bunny.position.x - bunny.radius <= playground.left
                            && bunny.velocity.x < 0)) {
                    bunny.velocity.x = -bunny.velocity.x;
                }
                if ((bunny.position.y - bunny.radius <= playground.top
                            && bunny.velocity.y < 0)
                        || (bunny.position.y + bunny.radius >= playground.bottom
                            && bunny.velocity.y > 0)) {
                    bunny.velocity.y = -bunny.velocity.y;
                }
            }
        } while(target_simtime > 0);

//        float energy = 0;
//        float momentum_x = 0;
//        float momentum_y = 0;
//        for (Bunny bunny : bunnies) {
//            energy += 0.5 * bunny.mass * bunny.velocity.len2();
//            momentum_x += bunny.mass * Math.abs(bunny.velocity.x);
//            momentum_y += bunny.mass * Math.abs(bunny.velocity.y);
//        }
//        Log.i("energy / momentum", "" + energy + " / " + (momentum_x + momentum_y));
//        if (sims != 1) Log.i("co", "t" + sims);
    }

    private void make_bomb_explode() {

        SEM.play(sound_ids[bomb_explosion_index], 1);

        // Notify killed bunnies and build a list of their indices.
        int idx = 0;
        LinkedList<Integer> killed = new LinkedList<Integer>();
        for (Bunny bunny : bunnies) {
            Vector2 diff = bomb.position.clone().sub(bunny.position);
            float distance = diff.len();
            if (bomb.explosion_range + bunny.radius >= distance) {
                bunny.explode();
                killed.add(idx);
            }
            idx++;
        }

        // Remove killed bunnies from the list.
        Iterator<Integer> idx_iter = killed.descendingIterator();
        while (idx_iter.hasNext()) {
            bunnies.remove((int) idx_iter.next());
        }

        // Notify the bomb.
        bomb.explode();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        final int action = event.getActionMasked();
        final Vector2 pos = new Vector2(event.getX(), event.getY());

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                bomb.start(pos);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                bomb.move(pos);
                break;
            }
            case MotionEvent.ACTION_UP: {
                make_bomb_explode();
            }
            case MotionEvent.ACTION_CANCEL: {
                bomb.delete();
                break;
            }
        }
        return true;
    }
}
