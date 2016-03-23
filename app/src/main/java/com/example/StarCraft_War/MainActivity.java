package com.example.StarCraft_War;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        // get screen pixel
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        Log.i("width,height,density", new String(""+metrics.widthPixels+","+metrics.heightPixels));
		setContentView(new Panel(this, metrics.heightPixels, metrics.widthPixels));
	}
}
