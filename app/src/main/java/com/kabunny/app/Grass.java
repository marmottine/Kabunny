package com.kabunny.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Random;

public class Grass {
    // Coordinates
    private int x;
    private int y;

    // TODO: get canvas dimensions
    // note that actual dim (WxH) on my tablet is 1280x736 or 800x1205,
    // depending on the orientation.
    // We may want to force either orientation.
    private int WIDTH = 720;
    private int HEIGHT = 720;

    private Drawable image;

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
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


    public Grass(Context context) {
        x = randInt(0, WIDTH);
        y = randInt(0, HEIGHT);

        image = context.getResources().getDrawable(R.drawable.grass);
    }

    public void draw(Canvas canvas) {
        image.setBounds(x, y, x+100, y+100);
        image.draw(canvas);
    }
}
