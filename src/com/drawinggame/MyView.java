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
	private Path lastShapeDone;
	private Path aveShapeDone;
	private Vector<Point> average = new Vector<Point>();
	private int averagePoints = 0;
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
	public void setLastShapeDone(Vector<Point> points)
	{
		//{137,139,135,141,133,144,132,146,130,149,128,151,126,155,123,160,120,166,116,171,112,177,107,183,102,188,100,191,95,195,90,199,86,203,82,206,80,209,75,213,73,213,70,216,67,219,64,221,61,223,60,225,62,226,65,225,67,226,74,226,77,227,85,229,91,230,99,231,108,232,116,233,125,233,134,234,145,233,153,232,160,233,170,234,177,235,179,236,186,237,193,238,198,239,200,237,202,239,204,238,206,234,205,230,202,222,197,216,192,207,186,198,179,189,174,183,170,178,164,171,161,168,154,160,148,155,143,150,138,148,136,148};
		for(int i = 0; i < 64; i ++)
		{
			if(i == points.size()) break;
			Point p1 = average.get(i);
			Point p2 = points.get(i);
			p1.X = ((p1.X*averagePoints)+(p2.X))/(averagePoints+1);
			p1.Y = ((p1.Y*averagePoints)+(p2.Y))/(averagePoints+1);
		}
		averagePoints ++;
		aveShapeDone = getPathFromVector(average, 300, 300);
		lastShapeDone = getPathFromVector(points, 300, 300);
	}
	public void setLastShape(Vector<Point> points)
	{
		lastShape = getPathFromVector(points);
	}
	public void endShape(String type, Point p)
    {
    	Toast.makeText(context, type.concat(" ").concat(Double.toString(p.Y)).concat(", ").concat(Double.toString(p.X)), Toast.LENGTH_SHORT).show();
    }
	public void click(Point p)
    {
    	Toast.makeText(context, "Click: ".concat(Double.toString(p.Y)).concat(", ").concat(Double.toString(p.X)), Toast.LENGTH_SHORT).show();
    	String start = "{";
		for(int i = 0; i < 64; i ++)
		{
			Point q = average.get(i);
			String toAdd = Integer.toString((int)q.X).concat(",").concat(Integer.toString((int)q.Y));
			if(i < 63) toAdd = toAdd.concat(",");
			else  toAdd = toAdd.concat("};");
			start = start.concat(toAdd);
		}
		Log.e("myid", start);
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
		for(int i = average.size(); i < 64; i ++)
		{
			average.add(new Point(0,0));
		}
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
