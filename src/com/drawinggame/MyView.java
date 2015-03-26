package com.drawinggame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MyView extends View {
	private Context context;
	private MainActivity activity;
	private ImageLibrary imageLibrary;
	private GestureDetector gestureDetector;
	private Handler mHandler = new Handler();
	private Path lastShape;
	private Paint paint;
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
    	paint = new Paint();
    	frameCaller.run();
		this.setOnTouchListener(gestureDetector);
    }
    private void frameCall()
    {
    	
    }
    
    protected void endShape(ArrayList<int[]> points, String type)
    {
    	Toast.makeText(context, type, Toast.LENGTH_SHORT);
    	lastShape = new Path();
    	lastShape.moveTo(points.get(0)[0], points.get(0)[1]);
		for(int j = 1; j < points.size(); j++)
		{
			lastShape.lineTo(points.get(j)[0], points.get(j)[1]);
		}
    }
    @Override
	protected void onDraw(Canvas g)
	{
    	ArrayList<ArrayList<int[]>> pointsList = gestureDetector.getPointsLists();
    	paint.setColor(Color.RED);
    	paint.setStyle(Style.STROKE);
    	paint.setStrokeWidth(4);
    	//Log.e("myid", "test");
    	for(int i = 0; i < pointsList.size(); i++)
    	{
    		ArrayList<int[]> points = pointsList.get(i);
    		//Log.e("myid", Integer.toString(points.size()));
    		Path path = new Path();
    		path.moveTo(points.get(0)[0], points.get(0)[1]);
    		for(int j = 1; j < points.size(); j++)
    		{
    			path.lineTo(points.get(j)[0], points.get(j)[1]);
    		}
    		g.drawPath(path, paint);
    	}
    	if(lastShape != null)
    	{
    		g.drawPath(lastShape, paint);
    	}
    	
    	
		//g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
		//drawNotPaused(g);
	}
}
