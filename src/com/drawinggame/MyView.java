package com.drawinggame;

import java.util.ArrayList;
import java.util.Vector;

import lx.interaction.dollar.Point;

import android.content.Context;
import android.graphics.Bitmap;
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
	protected LevelController levelController;
	protected SpriteController spriteController;
	protected WallController wallController;
	protected MainActivity activity;
	protected ImageLibrary imageLibrary;
	private GestureDetector gestureDetector;
	private Handler mHandler = new Handler();
	private Path lastShape;
	private Path lastShapeDone;
	private Path aveShapeDone;
	private Vector<Point> average = new Vector<Point>();
	private int averagePoints = 0;
	private Paint paint;
	private Bitmap background;
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
    public Controller(Context contextSet, MainActivity activitySet, double[] screenDimensions)
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
		spriteController = new SpriteController(context, this);
		wallController = new WallController(context, this);
		levelController = new LevelController(this);
		
    }
    
    private void frameCall()
    {
    	
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
    	if(lastShape != null)
    	{
    		paint.setColor(Color.BLUE);
    		g.drawPath(lastShape, paint);
    		paint.setColor(Color.CYAN);
    		g.drawPath(lastShapeDone, paint);
    		paint.setColor(Color.MAGENTA);
    		g.drawPath(aveShapeDone, paint);
    	}
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
