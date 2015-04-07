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
	private Controller control;
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
	private int timeSinceDraw = 50;
	private int actionMask;
	protected Recognizer recognizer;
	protected String selectType = "none";
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
    	control = controllerSet;
    	recognizer = new Recognizer(this);
    	for(int i = average.size(); i < 64; i ++)
		{
			average.add(new Point(0,0));
		}
    }
    protected void drawGestures(Canvas g, Paint paint)
	{
    	g.saveLayerAlpha(0, 0, g.getWidth(), g.getHeight(), 0x66, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
    	timeSinceDraw++;
    	paint.setColor(Color.BLACK);
    	paint.setStyle(Style.STROKE);
    	paint.setStrokeWidth(4);
    	if(pointsList.size() != 0)
    	{
    		g.drawPath(getPathFromVector(pointsList), paint);
    	}
    	if(lastShape != null && timeSinceDraw < 50)
    	{
    		//TODO fade drawn gesture
    		//paint.setAlpha(255 - 5*timeSinceDraw);
    		paint.setColor(Color.GRAY);
    		g.drawPath(lastShape, paint);
    		paint.setAlpha(255);
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
	public void endShape(Point screenPoint)
    {
		Point p = screenToMapPoint(screenPoint);
		timeSinceDraw = 0;
    }
	public boolean checkMadeEnemy(String type, Point p)
    {
		if(type.equals("lineH"))
		{
			if(!control.wallController.checkHitBack(p.X, p.Y, true))
			{
				control.spriteController.makeEnemy(0, (int)p.X, (int)p.Y, -90, true);
			}
		} else if(type.equals("lineV"))
		{
			if(!control.wallController.checkHitBack(p.X, p.Y, true))
			{
				control.spriteController.makeEnemy(2, (int)p.X, (int)p.Y, -90, true);
			}
		} else if(type.equals("arrow"))
		{
			if(!control.wallController.checkHitBack(p.X, p.Y, true))
			{
				control.spriteController.makeEnemy(1, (int)p.X, (int)p.Y, -90, true);
			}
		} else
		{
			return false;
		}
		return true;
    }
	public void endShapeGroup(String type, Point screenPoint)
    {
		Point p = screenToMapPoint(screenPoint);
		timeSinceDraw = 0;
		if(checkMadeEnemy(type, p)) return;
		if(type.equals("n"))
		{
			((Control_Group)control.selected).setLayoutType(1);
		} else if(type.equals("s"))
		{
			((Control_Group)control.selected).setLayoutType(2);
		} else if(type.equals("v"))
		{
			((Control_Group)control.selected).setLayoutType(0);
		} else if(type.equals("c"))
		{
			control.selected.cancelMove();
			Log.e("myid", "c");
		}
    }
	public void endShapeSingle(String type, Point screenPoint)
    {
		Point p = screenToMapPoint(screenPoint);
		timeSinceDraw = 0;
		if(checkMadeEnemy(type, p)) return;
		if(type.equals("c"))
		{
			control.selected.cancelMove();
		}
    }
	public void endShapeNone(String type, Point screenPoint)
    {
		Point p = screenToMapPoint(screenPoint);
		timeSinceDraw = 0;
		if(checkMadeEnemy(type, p)) return;
    }
	public void click(Point pPhone)
    {
    	Point p = screenToMapPoint(pPhone);
    	if(selectType.equals("none"))
    	{
    		control.spriteController.selectEnemy(p.X, p.Y);
    	} else if(selectType.equals("single"))
    	{
    		if(clickedTopLeft(pPhone)) return;
    		if(control.spriteController.selectEnemy(p.X, p.Y)) return;
    		control.selected.setDestination(p);
    	} else if(selectType.equals("group"))
    	{
    		if(clickedTopLeft(pPhone)) return;
    		if(control.spriteController.selectEnemy(p.X, p.Y)) return;
    		control.selected.setDestination(p);
    	}
    }
    public boolean clickedTopLeft(Point p)
    {
    	if(p.X > 150 || p.Y > 150) return false;
    	control.spriteController.deselectEnemies();
    	return true;
    }
	public void endCircle(Vector<Point> points)
	{
		for(int i = 0; i < points.size(); i++)
		{
			screenToMapPointInPlace(points.get(i));
		}
		control.spriteController.selectCircle(points);
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
        	if(secondID == 0) recognizer.Recognize(pointsList, selectType);
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
        		playScreenSizeStart = control.graphicsController.playScreenSize;
        		mapXSlideStart = control.graphicsController.mapXSlide;
        		mapYSlideStart = control.graphicsController.mapYSlide;
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
        		firstPointStart.X = (int)(e.getX(firstID));
        		firstPointStart.Y = (int)(e.getY(firstID));
        		secondPointStart.X = (int)(e.getX(secondID));
        		secondPointStart.Y = (int)(e.getY(secondID));
        		playScreenSizeStart = control.graphicsController.playScreenSize;
        		mapXSlideStart = control.graphicsController.mapXSlide;
        		mapYSlideStart = control.graphicsController.mapYSlide;
        	}
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	pointersDown --;
        break;
		}
		return true;
    }
	protected Path getPathFromInt(int[] points, int x, int y)
    {
    	Path path = new Path();
		path.moveTo(points[0]+x, points[1]+y);
		for(int j = 2; j < points.length; j+=4)
		{
			path.quadTo(points[j]+x, points[j+1]+y, points[j+3]+x, points[j+4]+y);
		}
		path.lineTo(points[points.length-2]+x, points[points.length-1]+y);
		return path;
    }
    protected Path getPathFromVector(Vector<Point> points, int x, int y)
    {
    	Path path = new Path();
		Point p = (Point) points.get(0);
		path.moveTo((int)p.X+x, (int)p.Y+y);
		for(int j = 1; j < points.size() - 1; j+=2)
		{
			p = (Point) points.get(j);
	        Point n = points.get(j + 1);
	        path.quadTo((int)p.X+x, (int)p.Y+y, (int)n.X+x, (int)n.Y+y);
		}
		p = (Point) points.get(points.size() - 1);
		path.lineTo((int)p.X+x, (int)p.Y+y);
		return path;
    }
    protected Path getPathFromVector(Vector<Point> points)
    {
    	return getPathFromVector(points, 0, 0);
    }
    protected void screenToMapPointInPlace(Point p)
    {
    	double phoneToMap = control.graphicsController.playScreenSize;
    	p.X = control.graphicsController.mapXSlide + p.X*phoneToMap;
    	p.Y = control.graphicsController.mapYSlide + p.Y*phoneToMap;
    }
    protected Point screenToMapPoint(Point p)
    {
    	//control.graphicsController.playScreenSize = levelWidth / levelPixels = gamePixel per phone pixel;
		//control.graphicsController.mapXSlide in phone pixels;
		//control.graphicsController.mapYSlide in phone pixels;
    	double phoneToMap = control.graphicsController.playScreenSize;
    	double phoneX = control.graphicsController.mapXSlide + p.X*phoneToMap;
    	double phoneY = control.graphicsController.mapYSlide + p.Y*phoneToMap;
    	return new Point(phoneX, phoneY);
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
		double screenRatio = endSeperation/startSeperation;
		double newPlayScreenSize = playScreenSizeStart*screenScale;
		double playScreenSizeMax = control.graphicsController.playScreenSizeMax;
		int levelWidth = control.levelController.levelWidth;
		int levelHeight = control.levelController.levelHeight;
		double xFix = 0;
		double yFix = 0;
		if(newPlayScreenSize > playScreenSizeMax)
		{
			playScreenSizeStart *= playScreenSizeMax/newPlayScreenSize;
			newPlayScreenSize = playScreenSizeMax;
		} else if(newPlayScreenSize < 0.5) 
		{
			playScreenSizeStart *= 0.5/newPlayScreenSize;
			newPlayScreenSize = 0.5;
		} else
		{
			xFix = (1-screenRatio)*(mapXSlideStart+startXAverage)*playScreenSizeStart;
			yFix = (1-screenRatio)*(mapYSlideStart+startYAverage)*playScreenSizeStart;
		}
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
		control.graphicsController.playScreenSize = newPlayScreenSize;
		control.graphicsController.mapXSlide = newXSlide;
		control.graphicsController.mapYSlide = newYSlide;
    }
}
