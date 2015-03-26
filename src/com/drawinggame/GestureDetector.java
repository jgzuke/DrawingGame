package com.drawinggame;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import lx.interaction.dollar.*;

public class GestureDetector implements OnTouchListener
{
	private MyView myView;
	private double screenWidth;
	private double screenHeight;
	private ArrayList<Integer> IDs = new ArrayList<Integer>();
	private ArrayList<Vector<Point>> pointsLists = new ArrayList<Vector<Point>>();
	private int actionMask;
	protected Recognizer recognizer;
	int ID = 0;
	
    public GestureDetector(MyView myViewSet)
    {
    	myView = myViewSet;
    	recognizer = new Recognizer();
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
			Vector<Point> newPointSet = new Vector<Point>(1000);	// Start a new array of points
			newPointSet.add(new Point((int)(e.getX(ID)), (int)(e.getY(ID))));				// Add array to list
			pointsLists.add(newPointSet);
			
        	ID = e.getPointerId(e.getActionIndex());				// Add pointer ID to ID list
        	IDs.add(ID);
        break;
        case MotionEvent.ACTION_UP:
        	myView.endShape(pointsLists.get(0), recognizer.Recognize(pointsLists.remove(0)));
        	pointsLists.clear();
        	IDs.clear();
        break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	ID = e.getPointerId(e.getActionIndex());
        	if(ID < e.getPointerCount())
        	{
        		Log.e("myid", "fingerDown");
        		Vector<Point> newAltPointSet = new Vector<Point>(1000);
	        	newAltPointSet.add(new Point((int)(e.getX(ID)), (int)(e.getY(ID))));
	        	pointsLists.add(newAltPointSet);
	        	
	        	IDs.add(ID);
        	}
        break;
        case MotionEvent.ACTION_MOVE:
        	for(int i = 0; i < IDs.size(); i++)
        	{
        		if(ID >= e.getPointerCount()) break;
	        	pointsLists.get(i).add(new Point((int)(e.getX(ID)), (int)(e.getY(ID))));
	        }
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	ID = e.getPointerId(e.getActionIndex());
        	if(ID >= e.getPointerCount()) break;
        	for(int i = 0; i < IDs.size(); i++)
        	{
        		if(ID == IDs.get(i))
	        	{
        			IDs.remove(i);
        			myView.endShape(pointsLists.get(i), recognizer.Recognize(pointsLists.remove(i)));
        		}
        	}
        break;
		}
		return true;
    }
	protected ArrayList<Vector<Point>> getPointsLists()
	{
		return pointsLists;
	}
}
