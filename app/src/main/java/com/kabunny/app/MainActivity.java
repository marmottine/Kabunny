package com.kabunny.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "create");
    }

    public void onPlayButtonClick(View view) {
        Log.d(TAG, "Button pressed");
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
