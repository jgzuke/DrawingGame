package com.drawinggame;

import java.util.ArrayList;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureDetector implements OnTouchListener
{
	private MyView myView;
	private double screenWidth;
	private double screenHeight;
	private ArrayList<Integer> IDs = new ArrayList<Integer>();
	private ArrayList<ArrayList<int[]>> pointsLists = new ArrayList<ArrayList<int[]>>();
	private int actionMask;
	int[] coordinate = new int[2];
	int ID = 0;
    public GestureDetector(MyView myViewSet)
    {
    	myView = myViewSet;
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
			ArrayList<int[]> newPointSet = new ArrayList<int[]>();	// Start a new array of points
			coordinate[0] = (int)(e.getX());						// Add current point to array
        	coordinate[1] = (int)(e.getY());						
			newPointSet.add(coordinate.clone());					// Add array to list
			pointsLists.add(newPointSet);
			
        	ID = e.getPointerId(e.getActionIndex());				// Add pointer ID to ID list
        	IDs.add(ID);
        break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	ID = e.getPointerId(e.getActionIndex());
        	if(ID < e.getPointerCount())
        	{
	        	ArrayList<int[]> newAltPointSet = new ArrayList<int[]>();
	        	coordinate[0] = (int)(e.getX(ID));
	        	coordinate[1] = (int)(e.getY(ID));
	        	newAltPointSet.add(coordinate.clone());
	        	pointsLists.add(newAltPointSet);
	        	
	        	IDs.add(ID);
        	}
        break;
        case MotionEvent.ACTION_MOVE:
        	ID = e.getPointerId(e.getActionIndex());
        	for(int i = 0; i < IDs.size(); i++)
        	{
        		if(ID == IDs.get(i))
        		{
	        		coordinate[0] = (int)(e.getX(ID));
	        		coordinate[1] = (int)(e.getY(ID));
	        		pointsLists.get(i).add(coordinate.clone());
        		}
        	}
        break;
        case MotionEvent.ACTION_UP:
        	pointsLists.clear();
        	IDs.clear();
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	ID = e.getPointerId(e.getActionIndex());
        	for(int i = 0; i < IDs.size(); i++)
        	{
        		if(ID == IDs.get(i))
	        	{
        			IDs.remove(i);
        			pointsLists.remove(i);
	        	}
        	}
        break;
		}
		return true;
    }
	protected ArrayList<ArrayList<int[]>> getPointsLists()
	{
		return pointsLists;
	}
}
