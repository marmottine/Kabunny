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
    public int radius;

    // Coordinates
    public double x;
    public double y;

    // Speed (in pixels per ms)
    public double vx;
    public double vy;

    // TODO: get canvas dimensions
    // note that actual dim (WxH) on my tablet is 1280x736 or 800x1205,
    // depending on the orientation.
    // We may want to force either orientation.
    private int WIDTH = 720;
    private int HEIGHT = 720;

    private Bitmap bm;
    private double offset = 0;
    private int bm_width;
    private int bm_height;

    // TODO: move this function!
    // from http://stackoverflow.com/a/363692/2386438
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value. Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    // TODO: move this function!
    public static double randDouble(double min, double max) {
        Random rand = new Random();
        return rand.nextDouble() * (max - min) + min;
    }

    // TODO: move this function!
    // from: http://stackoverflow.com/a/22471448/2386438
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage, int offset, int radius) {
        int targetWidth = radius * 2;
        int targetHeight = radius * 2;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW
        );

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, offset, sourceBitmap.getWidth(), offset + sourceBitmap.getWidth()),
                new Rect(0, 0, targetWidth, targetHeight), null
        );
        return targetBitmap;
    }

    public Bunny(Context context,
                 Integer radius, Integer x, Integer y, Double vx, Double vy) {
        Log.d(TAG, "ctor");

        this.radius = radius != null ? radius : randInt(20, 100);

        this.x = x != null ? x : randInt(this.radius, WIDTH - this.radius);
        this.y = y != null ? y : randInt(this.radius, HEIGHT - this.radius);
        this.vx = vx != null ? vx : randDouble(-0.2, 0.2);
        this.vy = vy != null ? vy : randDouble(-0.2, 0.2);

        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.flat_bunny);
        bm_width = bm.getWidth();
        bm_height = bm.getHeight();
    }

    public void draw(Canvas canvas) {
        Bitmap actual_bm = getRoundedShape(bm, (int) Math.round(offset), radius);
        canvas.drawBitmap(actual_bm, Math.round(x-radius), Math.round(y-radius), null);
    }

    public void update(long delta) {
        double delta_x = vx * delta;
        double delta_y = vy * delta;

        x += delta_x;
        y += delta_y;

        int min_offset = 0;
        int max_offset = bm_height - bm_width;

        // by how much should we increment the offset?
        // depends on the speed and radius of the bunny.
        // When a bunny performs a complete rotation,
        // it runs 2*PI*radius on the ground,
        // and the total offset of the bitmap is max_offset.
        // Thus, for every pixel on the ground, we should offset the bitmap by
        // max_offset / (2*PI*radius)

        double delta_dist = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
        double delta_offset = delta_dist * max_offset / (2.0 * Math.PI * radius);

        if (vy > 0) {
            offset -= delta_offset;
            if (offset <= min_offset) {
                offset += max_offset;
            }
        } else {
            offset += delta_offset;
            if (offset >= max_offset) {
                offset -= max_offset;
            }
        }
    }
}
