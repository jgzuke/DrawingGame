package com.drawinggame;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.View;

public class MyView extends View {
	private Context context;
	private MainActivity activity;
	private ImageLibrary imageLibrary;
	private GestureDetector gestureDetector;
	private Handler mHandler = new Handler();
	protected Runnable frameCaller = new Runnable()
	{
		public void run()
		{
			frameCall();
			invalidate();
			mHandler.postDelayed(this, 40);
		}
	};	
	
	private double screenWidth;
	private double screenHeight;
    public MyView(Context contextSet, MainActivity activitySet, double[] screenDimensions)
    {
    	super(contextSet);
    	context = contextSet;
    	activity = activitySet;
    	screenWidth = screenDimensions[0];
    	screenHeight = screenDimensions[1];
    	gestureDetector = new GestureDetector(this);
		this.setOnTouchListener(gestureDetector);
    }
    private void frameCall()
    {
    	
    }
    @Override
	protected void onDraw(Canvas g)
	{
		//g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
		//drawNotPaused(g);
	}
}
