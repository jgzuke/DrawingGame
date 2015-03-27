package com.drawinggame;

import java.util.ArrayList;
import java.util.Vector;

import lx.interaction.dollar.Point;

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

public class MyView extends View
{
	private Context context;
	private MainActivity activity;
	private ImageLibrary imageLibrary;
	private GestureDetector gestureDetector;
	private Handler mHandler = new Handler();
	private Path lastShape;
	private Path lastShapeBeforeTurn;
	private Path lastShapeAfterTurn;
	private Path lastShapeAfterScale;
	private Path lastShapeAfterTranslate;
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
	public void setlastShapeBeforeTurn(Vector<Point> points)
	{
		lastShapeBeforeTurn = getPathFromVector(points);
	}
	public void setlastShapeAfterTurn(Vector<Point> points)
	{
		lastShapeAfterTurn = getPathFromVector(points);
	}
	public void setlastShapeAfterScale(Vector<Point> points)
	{
		lastShapeAfterScale = getPathFromVector(points);
	}
	public void setlastShapeAfterTranslate(Vector<Point> points)
	{
		lastShapeAfterTranslate = getPathFromVector(points);
	}
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
    
    protected void endShape(Vector<Point> points, String type)
    {
    	Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
    	lastShape = getPathFromVector(points);
    }
    @Override
	protected void onDraw(Canvas g)
	{
    	ArrayList<Vector<Point>> pointsList = gestureDetector.getPointsLists();
    	paint.setColor(Color.RED);
    	paint.setStyle(Style.STROKE);
    	paint.setStrokeWidth(4);
    	//Log.e("myid", "test");
    	for(int i = 0; i < pointsList.size(); i++)
    	{
    		g.drawPath(getPathFromVector(pointsList.get(i)), paint);
    	}
    	//g.drawPath(getPathFromVector(gestureDetector.recognizer.templates.get(0).Points, 300, 500), paint);
    	if(lastShape != null)
    	{
    		paint.setColor(Color.BLUE);
    		g.drawPath(lastShape, paint);
    		paint.setColor(Color.CYAN);
    		g.drawPath(lastShapeBeforeTurn, paint);
    		//paint.setColor(Color.GREEN);
    		//g.drawPath(lastShapeAfterTurn, paint);
    		paint.setColor(Color.GRAY);
    		g.drawPath(lastShapeAfterScale, paint);
    		paint.setColor(Color.YELLOW);
    		g.drawPath(lastShapeAfterTranslate, paint);
    	}
    	
    	
		//g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
		//drawNotPaused(g);
	}
    protected Path getPathFromVector(Vector<Point> points, int x, int y)
    {
    	Path path = new Path();
		Point p = (Point) points.get(0);
		path.moveTo((int)p.X+x, (int)p.Y+y);
		for(int j = 1; j < points.size(); j++)
		{
			p = (Point) points.get(j);
			path.lineTo((int)p.X+x, (int)p.Y+y);
		}
		return path;
    }
    protected Path getPathFromVector(Vector<Point> points)
    {
    	Path path = new Path();
		Point p = (Point) points.get(0);
		path.moveTo((int)p.X, (int)p.Y);
		for(int j = 1; j < points.size(); j++)
		{
			p = (Point) points.get(j);
			path.lineTo((int)p.X, (int)p.Y);
		}
		return path;
    }
}
