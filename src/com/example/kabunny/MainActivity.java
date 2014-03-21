package com.example.kabunny;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private String TAG = "CRO MainActivity";
	private MainView main_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
        
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        main_view = new MainView(getWindow().getContext());
        setContentView(main_view);
        
        
//        getWindow().setContentView(view);
	}

}
