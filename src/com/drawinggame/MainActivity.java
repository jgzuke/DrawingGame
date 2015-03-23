package com.drawinggame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private MyView myView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        myView = new MyView(this, this, screenDimensions());
        setContentView(myView);
    }
    /**
	 * sets screen variables as well as audio settings
	 */
	private void setWindow()
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	/**
	 * sets dimensions of screen
	 */
	private double[] screenDimensions()
	{
		double[] dims = {getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels};
		return dims;
	}
}
