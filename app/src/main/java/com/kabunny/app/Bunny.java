package com.kabunny.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

public class Bunny {
    private String TAG = "Bunny";
    public float radius;
    public float mass;

    public Vector2 position;
    public Vector2 velocity; // pixels per ms

    private Bitmap bm;
    private float offset = 0f;
    private int bm_width;
    private int bm_height;
    static Random rand = new Random();

    public static float randFloat(float min, float max) {
        return rand.nextFloat() * (max - min) + min;
    }

    // TODO: move this function!
    // from: http://stackoverflow.com/a/22471448/2386438
    public Bitmap getRoundedShape(Bitmap sourceBitmap, int offset, int radius,
                                  float rotation) {
        int diameter = radius * 2;

        Bitmap targetBitmap = Bitmap.createBitmap(diameter, diameter,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.rotate(rotation, radius, radius);

        int source_width = sourceBitmap.getWidth();
        Rect src = new Rect(0, offset, source_width, offset + source_width);
        Rect dst = new Rect(0, 0, diameter, diameter);
        canvas.drawBitmap(sourceBitmap, src, dst, null);

        return targetBitmap;
    }

    public Bunny(Context context, Rect playground,
                 Integer radius, Float x, Float y, Float vx, Float vy) {
        Log.d(TAG, "ctor");

        this.radius = radius != null ? radius : randFloat(20f, 80f);
        mass = this.radius * this.radius * this.radius;

        if (x == null) {
            x = randFloat(playground.left + this.radius, playground.right - this.radius);
        }
        if (y == null) {
            y = randFloat(playground.top + this.radius, playground.bottom - this.radius);
        }
        position = new Vector2(x, y);

        if (vx == null) {
            vx = randFloat(-0.2f, 0.2f);
        }
        if (vy == null) {
            vy = randFloat(-0.2f, 0.2f);
        }
        velocity = new Vector2(vx, vy);

        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.flat_bunny);
        bm_width = bm.getWidth();
        bm_height = bm.getHeight();
    }

    public void draw(Canvas canvas) {
        float angle = (float) Math.toDegrees(Math.atan(velocity.y/velocity.x));
        if (velocity.x >= 0) {
            angle -= 90;
        } else {
            angle += 90;
        }

        Bitmap actual_bm = getRoundedShape(bm, (int) Math.round(offset), (int) Math.round(radius), angle);
        canvas.drawBitmap(actual_bm, Math.round(position.x-radius), Math.round(position.y-radius), null);
    }

    public void update(long delta) {
        Vector2 delta_pos = velocity.clone().scl(delta);
        position.add(delta_pos);

        int min_offset = 0;
        int max_offset = bm_height - bm_width;

        // by how much should we increment the offset?
        // depends on the speed and radius of the bunny.
        // When a bunny performs a complete rotation,
        // it runs 2*PI*radius on the ground,
        // and the total offset of the bitmap is max_offset.
        // Thus, for every pixel on the ground, we should offset the bitmap by
        // max_offset / (2*PI*radius)

        double delta_offset = delta_pos.len() * max_offset / (2.0 * Math.PI * radius);

        offset -= delta_offset;
        if (offset <= min_offset) {
            offset += max_offset;
        }
    }

    public void explode() {
        // TODO
        Log.d(TAG, "bunny killed");
    }
}
