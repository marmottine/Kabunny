package com.example.kabunny;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;


public class Frog {
	private String TAG = "CRO Frog";
	private Paint paint;
	private int radius;
	
	// Coordinates
	private int x;
	private int y;
	
	// TODO: get canvas dimensions
	// note that actual dim (WxH) on my tablet is 1280x736 or 800x1205,
	// depending on the orientation.
	// We may want to force either orientation.
	private int WIDTH = 800;
	private int HEIGHT = 736;
	
	
	// TODO: move this function!
	// from http://stackoverflow.com/a/363692/2386438
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
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
	
	
	public Frog() {
		Log.w(TAG, "ctor");

		radius = randInt(20, 40);
		x = randInt(radius, WIDTH - radius);
		y = randInt(radius, HEIGHT - radius);
		
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setARGB(220, randInt(0,  40), randInt(150, 255), randInt(0, 80));
	}
	
	public void draw(Canvas canvas) {
		Log.w(TAG, "draw. " + canvas.getWidth() + " x " + canvas.getHeight());
		canvas.drawCircle(x, y, radius, paint);
	}
}
