package com.drawinggame;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.View;

public class MyView extends View {
	private Context context;
	private MainActivity activity;
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
    public MyView(Context contextSet, MainActivity activitySet)
    {
    	super(contextSet);
    	context = contextSet;
    	activity = activitySet;
    }
    private void frameCall()
    {
    	
    }
    @Override
	protected void onDraw(Canvas g)
	{
		//g.translate(screenMinX, screenMinY);
		//g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
		//drawNotPaused(g);
	}
}
