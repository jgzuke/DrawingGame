package com.drawinggame;

import java.util.ArrayList;

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
	private ArrayList<Dollar> dollars = new ArrayList<Dollar>();
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
        	
        	Dollar dollar = new Dollar(Dollar.GESTURES_DEFAULT);
        	dollar.setActive(true);
        	dollar.pointerPressed(coordinate[0], coordinate[1]);
        	dollars.add(dollar);
        break;
        case MotionEvent.ACTION_UP:
        	int [] coordsTemp = pointsLists.get(0).get(pointsLists.get(0).size()-1);
        	dollars.get(0).pointerReleased(coordsTemp[0], coordsTemp[1]);
        	myView.endShape(pointsLists.remove(0), dollars.get(0).getName());
        	dollars.clear();
        	pointsLists.clear();
        	IDs.clear();
        break;
        case MotionEvent.ACTION_POINTER_DOWN:
        	ID = e.getPointerId(e.getActionIndex());
        	if(ID < e.getPointerCount())
        	{
        		Log.e("myid", "fingerDown");
	        	ArrayList<int[]> newAltPointSet = new ArrayList<int[]>();
	        	coordinate[0] = (int)(e.getX(ID));
	        	coordinate[1] = (int)(e.getY(ID));
	        	newAltPointSet.add(coordinate.clone());
	        	pointsLists.add(newAltPointSet);
	        	
	        	IDs.add(ID);
	        	
	        	Dollar dollarAlt = new Dollar(Dollar.GESTURES_DEFAULT);
	        	dollarAlt.setActive(true);
	        	dollarAlt.pointerPressed(coordinate[0], coordinate[1]);
	        	dollars.add(dollarAlt);
        	}
        break;
        case MotionEvent.ACTION_MOVE:
        	for(int i = 0; i < IDs.size(); i++)
        	{
        		if(ID >= e.getPointerCount()) break;
	        	coordinate[0] = (int)(e.getX(IDs.get(i)));
	        	coordinate[1] = (int)(e.getY(IDs.get(i)));
	        	pointsLists.get(i).add(coordinate.clone());
	        	dollars.get(i).pointerDragged(coordinate[0], coordinate[1]);
        	}
        break;
        case MotionEvent.ACTION_POINTER_UP:
        	ID = e.getPointerId(e.getActionIndex());
        	if(ID >= e.getPointerCount()) break;
        	for(int i = 0; i < IDs.size(); i++)
        	{
        		if(ID == IDs.get(i))
	        	{
        			int [] coordsTempAlt = pointsLists.get(i).get(pointsLists.get(i).size()-1);
                	dollars.get(i).pointerReleased(coordsTempAlt[0], coordsTempAlt[1]);
        			IDs.remove(i);
        			myView.endShape(pointsLists.remove(i), dollars.get(i).getName());
        			dollars.remove(i);
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
