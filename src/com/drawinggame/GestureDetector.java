package com.drawinggame;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import lx.interaction.dollar.*;

public class GestureDetector implements OnTouchListener
{
	private Controller controller;
	private double screenWidth;
	private double screenHeight;
	private int firstID = 0;
	private int secondID = 0;
	private Point firstPoint;
	private Point secondPoint;
	private Point firstPointStart;
	private Point secondPointStart;
	private double playScreenSizeStart;
	private int mapXSlideStart;
	private int mapYSlideStart;
	private int phoneWidth;
	private int phoneHeight;
	private Vector<Point> pointsList = new Vector<Point>(1000);
	private int actionMask;
	protected Recognizer recognizer;
	int ID = 0;
	
	private Path lastShape;
	private Path lastShapeDone;
	private Path aveShapeDone;
	private Vector<Point> average = new Vector<Point>();
	private int averagePoints = 0;
	private Context context;
	private int pointersDown = 0;
	
    public GestureDetector(Context contextSet, Controller controllerSet, int widthSet, int heightSet)
    {
    	phoneWidth = widthSet;
    	phoneHeight = heightSet;
    	context = contextSet;
    	controller = controllerSet;
    	recognizer = new Recognizer(this);
    	for(int i = average.size(); i < 64; i ++)
		{
			average.add(new Point(0,0));
		}
    }
    protected void drawGestures(Canvas g, Paint paint)
	{
    	paint.setColor(Color.RED);
    	paint.setStyle(Style.STROKE);
    	paint.setStrokeWidth(4);
    	if(pointsList.size() != 0)
    	{
    		g.drawPath(getPathFromVector(pointsList), paint);
    	}
    	if(lastShape != null)
    	{
    		paint.setColor(Color.BLUE);
    		g.drawPath(lastShape, paint);
    	}
	}
    

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
    
	/**
	 * decides which gesture capture is appropriate to call, drags or changes to position are done here
	 */
	@Override
    public boolean onTouch(View v, MotionEvent e)
	{
		actionMask = e.getActionMasked();
		switch (actionMask)
		{
		case MotionEvent.ACTION_DOWN:
			ID = e.getPointerId(e.getActionIndex());				// Add pointer ID to ID list
        	pointsList.add(new Point((int)(e.getX(ID)), (int)(e.getY(ID))));				// Add array to list
			firstID = ID;
        	pointersDown = 1;
        	firstPoint = new Point((int)(e.getX(ID)), (int)(e.getY(ID)));
        break;
        case MotionEvent.ACTION_UP:
        	if(secondID == 0) recognizer.Recognize(pointsList);
        	pointsList.clear();
        	pointersDown = 0;
        	secondID = 0;
        	firstID = 0;
        break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	ID = e.getPointerId(e.getActionIndex());
        	pointersDown ++;
        	if(pointersDown == 2)
        	{
        		secondID = ID;
        		firstPoint = new Point((int)(e.getX(firstID)), (int)(e.getY(firstID)));
        		secondPoint = new Point((int)(e.getX(secondID)), (int)(e.getY(secondID)));
        		firstPointStart = new Point(firstPoint.X, firstPoint.Y);
        		secondPointStart = new Point(secondPoint.X, secondPoint.Y);
        		playScreenSizeStart = controller.graphicsController.playScreenSize;
        		mapXSlideStart = controller.graphicsController.mapXSlide;
        		mapYSlideStart = controller.graphicsController.mapYSlide;
        	}
        break;
        case MotionEvent.ACTION_MOVE:
        	if(pointersDown == 1 && secondID == 0) pointsList.add(new Point((int)(e.getX(firstID)), (int)(e.getY(firstID))));
        	if(pointersDown > 1)
        	{
        		firstPoint.X = (int)(e.getX(firstID));
        		firstPoint.Y = (int)(e.getY(firstID));
        		secondPoint.X = (int)(e.getX(secondID));
        		secondPoint.Y = (int)(e.getY(secondID));
        		scaleMap();
        	}
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	pointersDown --;
        break;
		}
		return true;
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
    protected void scaleMap()
    {
		double startXAverage = (firstPointStart.X + secondPointStart.X)/2;
		double startYAverage = (firstPointStart.Y + secondPointStart.Y)/2;
		double endXAverage = (firstPoint.X + secondPoint.X)/2;
		double endYAverage = (firstPoint.Y + secondPoint.Y)/2;
		double startSeperation = Math.sqrt(Math.pow(firstPointStart.X - secondPointStart.X, 2)+Math.pow(firstPointStart.Y - secondPointStart.Y, 2));
		double endSeperation = Math.sqrt(Math.pow(firstPoint.X - secondPoint.X, 2)+Math.pow(firstPoint.Y - secondPoint.Y, 2));
		double screenScale = startSeperation/endSeperation;
		double newPlayScreenSize = playScreenSizeStart*screenScale;
		double playScreenSizeMax = controller.graphicsController.playScreenSizeMax;
		int levelWidth = controller.levelController.levelWidth;
		int levelHeight = controller.levelController.levelHeight;
		if(newPlayScreenSize > playScreenSizeMax)
		{
			playScreenSizeStart *= playScreenSizeMax/newPlayScreenSize;
			newPlayScreenSize = playScreenSizeMax;
		}
		double scaledXAverage = startXAverage*screenScale;
		double scaledYAverage = startYAverage*screenScale;
		double xFix = 0;//startXAverage*screenScale*newPlayScreenSize;
		double yFix = 0;//startYAverage*screenScale*newPlayScreenSize;
		xFix += (endXAverage-startXAverage)*playScreenSizeStart;
		yFix += (endYAverage-startYAverage)*playScreenSizeStart;
		int newXSlide = (int)(mapXSlideStart - xFix);
		int newYSlide = (int)(mapYSlideStart - yFix);
		if(newXSlide < 0)
		{
			mapXSlideStart = (int) xFix;
			newXSlide = 0;
		}
		if(newYSlide < 0)
		{
			mapYSlideStart = (int) yFix;
			newYSlide = 0;
		}
		if(newXSlide > levelWidth - newPlayScreenSize*phoneWidth)
		{
			mapXSlideStart = (int)(levelWidth - newPlayScreenSize*phoneWidth + xFix);
			newXSlide = (int)(mapXSlideStart - xFix);
		}
		if(newYSlide > levelHeight - newPlayScreenSize*phoneHeight)
		{
			mapYSlideStart = (int)(levelHeight - newPlayScreenSize*phoneHeight + yFix);
			newYSlide = (int)(mapYSlideStart - yFix);
		}
		controller.graphicsController.playScreenSize = newPlayScreenSize;
		controller.graphicsController.mapXSlide = newXSlide;
		controller.graphicsController.mapYSlide = newYSlide;
    }
}
