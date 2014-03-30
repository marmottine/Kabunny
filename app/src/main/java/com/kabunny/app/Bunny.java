package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Random;

public class Bunny {
    private String TAG = "Bunny";
    public float radius;

    public Vector2 position;
    public Vector2 velocity;

    // TODO: get canvas dimensions
    // note that actual dim (WxH) on my tablet is 1280x736 or 800x1205,
    // depending on the orientation.
    // We may want to force either orientation.
    private float WIDTH = 720f;
    private float HEIGHT = 720f;

    private Drawable image;

    public static float randFloat(float min, float max) {
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
    }

    public Bunny(Context context) {
        Log.i(TAG, "ctor");

        radius = randFloat(20f, 40f);
        position = new Vector2(randFloat(radius, WIDTH - radius),
                randFloat(radius, HEIGHT - radius));

        velocity = new Vector2(randFloat(-0.2f, 0.2f),
                randFloat(-0.2f,0.2f));

        image = context.getResources().getDrawable(R.drawable.bunny);
    }

    public void draw(Canvas canvas) {
        image.setBounds((int) Math.round(position.x-radius),
                (int) Math.round(position.y-radius),
                (int) Math.round(position.x+radius),
                (int) Math.round(position.y+radius));
        image.draw(canvas);
    }

    public void update(long delta) {
        position.add(velocity.clone().scl(delta));
    }
}
