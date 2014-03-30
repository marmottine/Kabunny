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

    public Vector2 position;
    public Vector2 velocity; // pixels per ms

    // TODO: get canvas dimensions
    // note that actual dim (WxH) on my tablet is 1280x736 or 800x1205,
    // depending on the orientation.
    // We may want to force either orientation.
    private float WIDTH = 720f;
    private float HEIGHT = 720f;

    private Bitmap bm;
    private int offset = 0;
    private int bm_width;
    private int bm_height;

    public static float randFloat(float min, float max) {
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
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
                new Rect(0, offset, sourceBitmap.getWidth(), offset + 200),
                new Rect(0, 0, targetWidth, targetHeight), null
        );
        return targetBitmap;
    }

    public Bunny(Context context) {
        Log.i(TAG, "ctor");

        radius = randFloat(20f, 100f);
        position = new Vector2(randFloat(radius, WIDTH - radius),
                randFloat(radius, HEIGHT - radius));

        velocity = new Vector2(randFloat(-0.2f, 0.2f),
                randFloat(-0.2f,0.2f));

        bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.flat_bunny);
        bm_width = bm.getWidth();
        bm_height = bm.getHeight();
    }

    public void draw(Canvas canvas) {
        Bitmap actual_bm = getRoundedShape(bm, offset, (int)radius);
        canvas.drawBitmap(actual_bm, Math.round(position.x-radius), Math.round(position.y-radius), null);

        // Next time, display another part of the bunny
        // to create an illusion of rotation
        offset += 2;
        int max_offset = bm_height - bm_width;
        if (offset >= max_offset) {
            offset -= max_offset;
        }
    }

    public void update(long delta) {
        position.add(velocity.clone().scl(delta));
    }
}
