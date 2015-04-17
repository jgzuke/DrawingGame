package com.drawinggame;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
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
	private Paint paint = new Paint();
	private double playScreenSizeStart;
	private int mapXSlideStart;
	private int mapYSlideStart;
	private int phoneWidth;
	private int phoneHeight;
	protected double unitWidth;
	protected double unitHeight;
	private Vector<Point> pointsList = new Vector<Point>(1000);
	private int timeSinceDraw = 50;
	private int actionMask;
	protected int settingSelected = 0;
	protected Recognizer recognizer;
	protected String selectType = "none";
	int ID = 0;
	
	private Path lastShape;
	private Vector<Point> average = new Vector<Point>();
	private Context context;
	private int pointersDown = 0;
	
    public GestureDetector(Context contextSet, Controller controllerSet, int widthSet, int heightSet)
    {
    	paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
	    paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
	    paint.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
    	phoneWidth = widthSet;
    	phoneHeight = heightSet;
    	unitWidth = (double)phoneWidth/100;
		unitHeight = (double)phoneHeight/100;
    	context = contextSet;
    	control = controllerSet;
    	recognizer = new Recognizer(this);
    	for(int i = average.size(); i < 64; i ++)
		{
			average.add(new Point(0,0));
		}
    }
    protected void drawGestureSelected(Canvas g)
	{
    	g.drawPath(getPathFromVector(recognizer.templates.get(settingSelected).Points, (int)(unitWidth*25-unitHeight*5), (int)(unitHeight*60), 0.6*(unitWidth*50 - unitHeight*10)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(0).Points, (int)(unitWidth*50), (int)(unitHeight*30), 0.6*(unitHeight*20)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(1).Points, (int)(unitWidth*50), (int)(unitHeight*50), 0.6*(unitHeight*20)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(2).Points, (int)(unitWidth*50), (int)(unitHeight*70), 0.6*(unitHeight*20)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(3).Points, (int)(unitWidth*50), (int)(unitHeight*90), 0.6*(unitHeight*20)), paint);
    	drawGestures(g);
	}
    protected void drawGestures(Canvas g)
	{
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
    		g.saveLayerAlpha(0, 0, g.getWidth(), g.getHeight(), 255 - 5*timeSinceDraw, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
    		paint.setColor(Color.GRAY);
    		g.drawPath(lastShape, paint);
    		g.saveLayerAlpha(0, 0, g.getWidth(), g.getHeight(), 255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
    		
    	}
	}
	public void setLastShape(Vector<Point> points)
	{
		lastShape = getPathFromVector(points);
	}
	public boolean checkMadeEnemy(String type, Point p)
    {
		if(type.equals("lineH"))
		{
			if(!control.wallController.checkHitBack(p.X, p.Y, true))
			{
				control.spriteController.makeEnemy(0, (int)p.X, (int)p.Y, true);
			}
		} else if(type.equals("lineV"))
		{
			if(!control.wallController.checkHitBack(p.X, p.Y, true))
			{
				control.spriteController.makeEnemy(2, (int)p.X, (int)p.Y, true);
			}
		} else if(type.equals("arrow"))
		{
			if(!control.wallController.checkHitBack(p.X, p.Y, true))
			{
				control.spriteController.makeEnemy(1, (int)p.X, (int)p.Y, true);
			}
		} else
		{
			return false;
		}
		return true;
    }
	public void endShape(Vector<Point> points, Rectangle b)
	{
		if(b.X+b.Width < unitWidth*50-unitHeight*10 && b.Y > unitHeight*20)
		{
			recognizer.templates.get(settingSelected).Points = points;
		} else
		{
			Toast.makeText(context, "Out of bounds", Toast.LENGTH_SHORT).show();
		}
	}
	public void endShape(String type, Point screenPoint)
    {
		Point p = screenToMapPoint(screenPoint);
		timeSinceDraw = 0;
		if(control.paused)
		{
			Toast.makeText(context, "Too close to another gesture", Toast.LENGTH_SHORT).show();
			return;
		}
		if(checkMadeEnemy(type, p)) return;
		if(selectType.equals("none"))
	    {
			if(type.equals("0"))
			{
				control.spriteController.makeEnemy(3, (int)p.X, (int)p.Y, true);
			} else if(type.equals("1"))
			{
				control.spriteController.makeEnemy(4, (int)p.X, (int)p.Y, true);
			} else if(type.equals("2"))
			{
				control.spriteController.makeEnemy(5, (int)p.X, (int)p.Y, true);
			} else if(type.equals("3"))
			{
				control.spriteController.makeEnemy(6, (int)p.X, (int)p.Y, true);
			}
	    } else if(selectType.equals("single"))
	    {
	    	if(type.equals("c"))
			{
				control.selected.cancelMove();
			}
	    } else if(selectType.equals("group"))
	    {
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
			}
	    }
    }
	public void click(Point pPhone)
    {
    	Point p = screenToMapPoint(pPhone);
    	if(clickedTopLeft(pPhone)) return;
    	if(control.paused)
    	{
    		if(pPhone.Y > unitHeight*20)
    		{
    			if(Math.abs(pPhone.X-unitWidth*50) < unitHeight*10)
        		{
    				settingSelected = (int) (pPhone.Y/(unitHeight*20)) - 1;
        		}
    		}
    	} else if(selectType.equals("none"))
    	{
    		control.spriteController.selectEnemy(p.X, p.Y);
    	} else if(selectType.equals("single"))
    	{
    		if(control.spriteController.selectEnemy(p.X, p.Y)) return;
    		control.selected.setDestination(p);
    	} else if(selectType.equals("group"))
    	{
    		if(control.spriteController.selectEnemy(p.X, p.Y)) return;
    		control.selected.setDestination(p);
    	}
    }
    public boolean clickedTopLeft(Point p)
    {
    	if(p.X < 150 && p.Y < 150)
    	{
    		control.paused = !control.paused;
    		return true;
    	}
    	if(p.X < 300 && p.Y < 150 && !control.gestureDetector.selectType.equals("none"))
    	{
    		control.spriteController.deselectEnemies();
    		return true;
    	}
    	return false;
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
        	if(secondID == 0)
        	{
        		recognizer.Recognize(pointsList, selectType, !control.paused);
        	}
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
    protected Path getPathFromVector(Vector<Point> points, int x, int y, double width)
    {
    	Path path = new Path();
    	double ratio = width/250;
		Point p = (Point) points.get(0);
		path.moveTo((int)(p.X*ratio)+x, (int)(p.Y*ratio)+y);
		for(int j = 1; j < points.size() - 1; j+=2)
		{
			p = (Point) points.get(j);
	        Point n = points.get(j + 1);
	        path.quadTo((int)(p.X*ratio)+x, (int)(p.Y*ratio)+y, (int)(n.X*ratio)+x, (int)(n.Y*ratio)+y);
		}
		p = (Point) points.get(points.size() - 1);
		path.lineTo((int)(p.X*ratio)+x, (int)(p.Y*ratio)+y);
		return path;
    }
    protected Path getPathFromVector(Vector<Point> points, int x, int y)
    {
    	return getPathFromVector(points, x, y, 250);
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
