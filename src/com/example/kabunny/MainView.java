package com.example.kabunny;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceView;

public class MainView extends SurfaceView {
	private String TAG = "CRO MainView";
	private List<Frog> frogs;
	private int num_frogs = 4;
	
	public MainView(Context context) {
		super(context);
		Log.w(TAG, "ctor");
		
		setWillNotDraw(false);
//		requestLayout();
//		invalidate();
		
		frogs = new ArrayList<Frog>(num_frogs);
		for (int i = 0; i < num_frogs; i++) {
			frogs.add(new Frog());
		}
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.w(TAG, "draw");

		for (Frog frog: frogs) {
			frog.draw(canvas);			
		}
	}
}
