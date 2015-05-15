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
	private Point firstPoint = new Point(0,0);
	private Point secondPoint = new Point(0,0);;
	private Paint paint = new Paint();
	private int phoneWidth;
	private int phoneHeight;
	protected double unitWidth;
	protected double unitHeight;
	private Vector<Point> pointsList = new Vector<Point>(100);
	private Long timeOfDraw = (long) 0;
	private int actionMask;
	protected int settingSelected = 0;
	protected Vector<Point> lastPoints = new Vector<Point>(100);
	protected Recognizer recognizer;
	protected String selectType = "none";
	protected Long fadeTime = (long) 50000;
	private MyGLSurfaceView graphics = null;
	int ID = 0;
	
	private Path lastShape;
	private Vector<Point> average = new Vector<Point>();
	public Context context;
	private int pointersDown = 0;
	
	private Point averageStartPoint = new Point(0,0);
	private double averageStartDist = 0;
	
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
    protected void setGraphics(MyGLSurfaceView graphicsSet)
    {
    	graphics = graphicsSet;
    }
    protected void drawGesturePausedBig(Canvas g)
	{
    	paint.setColor(Color.BLACK);
    	paint.setStrokeWidth(7);
    	g.drawPath(getPathFromVector(recognizer.templates.get(settingSelected).Points, (int)(unitWidth*25-unitHeight*5), (int)(unitHeight*60), 0.6*(unitWidth*50 - unitHeight*10)), paint);
    }
    protected void drawGesturePausedSmall(Canvas g)
	{
    	paint.setColor(Color.WHITE);
    	paint.setStrokeWidth(4);
    	g.drawPath(getPathFromVector(recognizer.templates.get(0).Points, (int)(unitWidth*50 - 10), (int)(unitHeight*30), 0.55*(unitHeight*20)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(1).Points, (int)(unitWidth*50 - 10), (int)(unitHeight*50), 0.55*(unitHeight*20)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(2).Points, (int)(unitWidth*50 - 10), (int)(unitHeight*70), 0.55*(unitHeight*20)), paint);
    	g.drawPath(getPathFromVector(recognizer.templates.get(3).Points, (int)(unitWidth*50 - 10), (int)(unitHeight*90), 0.55*(unitHeight*20)), paint);
	}
    protected boolean shouldDrawGesture()
    {
    	return pointsList.size() != 0 || lastShape != null && System.nanoTime() - timeOfDraw < fadeTime + 10000;
    }
    protected void drawGestures(Canvas g)
	{
    	paint.setStyle(Style.STROKE);
    	paint.setStrokeWidth(4);
    	if(pointsList.size() != 0)
    	{
    		paint.setColor(Color.BLACK);
    		g.drawPath(getPathFromVector(pointsList), paint);
    	}
    	if(lastShape != null && System.nanoTime() - timeOfDraw < fadeTime)
    	{
    		paint.setColor(Color.argb(127, 0, 0, 0));
    		//g.saveLayerAlpha(0, 0, g.getWidth(), g.getHeight(), 255 - 5*timeSinceDraw, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
    		//paint.setColor(Color.GRAY);
    		g.drawPath(lastShape, paint);
    		
    		paint.setColor(Color.BLACK);
    		//g.saveLayerAlpha(0, 0, g.getWidth(), g.getHeight(), 255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
    	}
	}
	public void setLastShape(Vector<Point> points)
	{
		lastPoints = points;
		lastShape = getPathFromVector(points);
	}
	public boolean checkMadeEnemy(String type, Point p)
    {
		int num = -1;
		if(type.equals("lineH"))
		{
			num = 0;
		} else if(type.equals("lineV"))
		{
			num = 2;
		} else if(type.equals("arrow"))
		{
			num = 1;
		} else if(type.equals("0"))
		{
			num = 3;
		} else if(type.equals("1"))
		{
			num = 4;
		} else if(type.equals("2"))
		{
			num = 5;
		} else if(type.equals("3"))
		{
			num = 6;
		} else
		{
			return false;
		}
		control.spriteController.makeEnemy(num, (int)p.X, (int)p.Y, true);
		return true;
    }
	public void endShape(Vector<Point> points, Rectangle b)
	{
		if(control.paused)
		{
			if(b.X+b.Width < unitWidth*50-unitHeight*10 && b.Y > unitHeight*20)
			{
				recognizer.templates.get(settingSelected).replacePoints(points, context);
				graphics.drawSection[1] = true;
				graphics.drawSection[2] = true;
				return;
			}
			if(b.X < unitWidth*50-unitHeight*10)
			{
				Toast.makeText(context, "Out of bounds", Toast.LENGTH_SHORT).show();
			}
		}
	}
	public boolean endShapePaused(String type, Point screenPoint, Rectangle b, Vector<Point> points)
	{
		if(b.X+b.Width < unitWidth*50-unitHeight*10 && b.Y > unitHeight*20)
		{
			if(recognizer.templates.get(settingSelected).Name.equals(type))
			{
				recognizer.templates.get(settingSelected).replacePoints(points, context);
				graphics.drawSection[1] = true;
				graphics.drawSection[2] = true;
			} else
			{
				Toast.makeText(context, "Too close to another gesture", Toast.LENGTH_SHORT).show();
			}
		} else if(b.X > unitWidth*50+unitHeight*10 && b.Y > unitHeight*20)
		{
			if(type.equals("lineH"))
			{
				control.selectionSpriteController.makeEnemy(0);
			} else if(type.equals("lineV"))
			{
				control.selectionSpriteController.makeEnemy(2);
			} else if(type.equals("arrow"))
			{
				control.selectionSpriteController.makeEnemy(1);
			} else if(type.equals("n"))
			{
				control.selectionSpriteController.changeLayout(1);
			} else if(type.equals("s"))
			{
				control.selectionSpriteController.changeLayout(2);
			} else if(type.equals("v"))
			{
				control.selectionSpriteController.changeLayout(0);
			} else
			{
				return false;
			}
		} else
		{
			Toast.makeText(context, "Out of bounds", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	public boolean endShape(String type, Point screenPoint, Rectangle b, Vector<Point> points)
    {
		Point p = screenToMapPoint(screenPoint);
		timeOfDraw = System.nanoTime();
		if(control.paused)
		{
			return endShapePaused(type, screenPoint, b, points);
		}
		if(checkMadeEnemy(type, p)) return true;
		if(selectType.equals("none"))
	    {
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
		return true;
    }
	public void click(Point pPhone)
    {
    	if(clickedTopLeft(pPhone)) return;
    	if(control.paused)
    	{
    		if(pPhone.Y > unitHeight*20)
    		{
    			if(Math.abs(pPhone.X-unitWidth*50) < unitHeight*10)
        		{
    				settingSelected = (int) (pPhone.Y/(unitHeight*20)) - 1;
    				control.selectionSpriteController.selectedManaRatio = 0;
    				graphics.drawSection[1] = true;
    				graphics.drawSection[2] = true;
    				graphics.drawSection[3] = true;
        		}
    			if(pPhone.X>phoneWidth-150 && pPhone.Y < (unitHeight*20)+150)
        		{
    				control.selectionSpriteController.deleteEnemies();
        		} else
        		{
        			control.selectionSpriteController.selectEnemy(pPhone.X, pPhone.Y);
        		}
    		}
    	} else
    	{
    		Point p = screenToMapPoint(pPhone);
    		if(control.spriteController.selectEnemy(p.X, p.Y)) return;
	    	if(!selectType.equals("none"))
	    	{
	    		control.selected.setDestination(p);
	    	}
    	}
    }
    public boolean clickedTopLeft(Point p)
    {
    	if(p.X < 150 && p.Y < 150)
    	{
    		control.paused = !control.paused;
    		if(control.paused)
    		{
    			graphics.drawSection[0] = true;
				graphics.drawSection[1] = true;
				graphics.drawSection[2] = true;
				graphics.drawSection[3] = true;
    		}
    		timeOfDraw = (long) 0;
    		return true;
    	}
    	if(!control.paused)
    	{
    		if(p.X < 300 && p.Y < 150 && !control.gestureDetector.selectType.equals("none"))
    		{
    			control.spriteController.deselectEnemies();
    			return true;
    		}
    		if(p.X < 450 && p.Y < 150 && !control.gestureDetector.selectType.equals("none"))
    		{
    			stopAction();
    			return true;
    		}
    		if(p.X > phoneWidth-150 && p.Y < 150)
    		{
    			restart();
    			return true;
    		}
    	}
    	return false;
    }
    public void restart()
    {
    	control.spriteController.restart();
    }
    public void stopAction()
    {
    	control.selected.cancelMove();
    }
	public void endCircle(Vector<Point> points, Vector<Point> pointsOrig, Rectangle b)
	{
		if(control.paused)
		{
			if(b.X+b.Width < unitWidth*50-unitHeight*10 && b.Y > unitHeight*20)
			{
				recognizer.templates.get(settingSelected).replacePoints(points, context);
				graphics.drawSection[1] = true;
				graphics.drawSection[2] = true;
				return;
			}
			if(b.X < unitWidth*50-unitHeight*10)
			{
				Toast.makeText(context, "Out of bounds", Toast.LENGTH_SHORT).show();
				return;
			}
			control.selectionSpriteController.selectCircle(pointsOrig);
		} else
		{
			for(int i = 0; i < pointsOrig.size(); i++)
			{
				screenToMapPointInPlace(pointsOrig.get(i));
			}
			control.spriteController.selectCircle(pointsOrig);
		}
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
			if(ID >= e.getPointerCount()) return true;
        	pointsList.add(new Point((int)(e.getX(ID)), (int)(e.getY(ID))));				// Add array to list
			firstID = ID;
        	pointersDown = 1;
        	firstPoint.X = e.getX(firstID);
    		firstPoint.Y = e.getY(firstID);
        break;
        case MotionEvent.ACTION_UP:
        	if(secondID == 0)
        	{
        		recognizer.Recognize(pointsList, selectType);
        	}
        	pointsList.clear();
        	pointersDown = 0;
        	secondID = 0;
        	firstID = 0;
        break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	ID = e.getPointerId(e.getActionIndex());
        	if(ID >= e.getPointerCount()) return true;
        	pointersDown ++;
        	if(pointersDown == 2)
        	{
        		secondID = ID;
        		firstPoint.X = e.getX(firstID);
        		firstPoint.Y = e.getY(firstID);
        		secondPoint.X = e.getX(secondID);
        		secondPoint.Y = e.getY(secondID);
        		Point fMap = screenToMapPoint(firstPoint);
        		Point sMap = screenToMapPoint(secondPoint);
        		averageStartPoint.X = (fMap.X + sMap.X)/2;
        		averageStartPoint.Y = (fMap.Y + sMap.Y)/2;
        		averageStartDist = distBetweenPoints(fMap, sMap);
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
        		Point fMap = screenToMapPoint(firstPoint);
        		Point sMap = screenToMapPoint(secondPoint);
        		averageStartPoint.X = (fMap.X + sMap.X)/2;
        		averageStartPoint.Y = (fMap.Y + sMap.Y)/2;
        		averageStartDist = distBetweenPoints(fMap, sMap);
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
    	double phoneToMap = graphics.playScreenSize;
    	p.X = graphics.mapXSlide + p.X*phoneToMap;
    	p.Y = graphics.mapYSlide + p.Y*phoneToMap;
    }
    protected Point screenToMapPoint(Point p)
    {
    	double phoneX = graphics.mapXSlide + p.X*graphics.playScreenSize;
    	double phoneY = graphics.mapYSlide + p.Y*graphics.playScreenSize;
    	return new Point(phoneX, phoneY);
    }
    protected double distBetweenPoints(Point p, Point q)
    {
    	return Math.sqrt(Math.pow(p.X-q.X, 2) + Math.pow(p.Y-q.Y, 2));
    }
    protected void scaleMap()
    {
		double seperation = distBetweenPoints(firstPoint, secondPoint);
		/* distances have to be equal
		* dist end = dist start
		* dist end = graphics.playScreenSize * seperation
		* graphics.playScreenSize * seperation = dist start
		* graphics.playScreenSize = dist start/seperation
		*/
		if(seperation == 0) return;
		graphics.playScreenSize = averageStartDist/seperation;
		
		if(graphics.playScreenSize > graphics.playScreenSizeMax)
		{
			graphics.playScreenSize = graphics.playScreenSizeMax;
		} else if(graphics.playScreenSize < 0.5) 
		{
			graphics.playScreenSize = 0.5;
		}
    	
		double aveX = (firstPoint.X+secondPoint.X)*graphics.playScreenSize/2;
		double aveY = (firstPoint.Y+secondPoint.Y)*graphics.playScreenSize/2;
		/*
		 * aveX + graphics.mapXSlide = averageStartPoint.X
		 * graphics.mapXSlide = averageStartPoint.X - aveX
		 */
		graphics.mapXSlide = (int) (averageStartPoint.X - aveX);
		graphics.mapYSlide = (int) (averageStartPoint.Y - aveY);
		
		if(graphics.mapXSlide < 0)
		{
			graphics.mapXSlide = 0;
		}
		if(graphics.mapYSlide < 0)
		{
			graphics.mapYSlide = 0;
		}
		int maxW = (int)(control.levelController.levelWidth - graphics.playScreenSize*phoneWidth);
		int maxH = (int)(control.levelController.levelHeight - graphics.playScreenSize*phoneHeight);
		if(graphics.mapXSlide > maxW)
		{
			graphics.mapXSlide = maxW;
		}
		if(graphics.mapYSlide > maxH)
		{
			graphics.mapYSlide = maxH;
		}
    }
}
