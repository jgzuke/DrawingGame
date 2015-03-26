package com.drawinggame;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureDetector implements OnTouchListener {
	private MyView myView;
	private double screenWidth;
	private double screenHeight;
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
		int actionMask = e.getActionMasked();
		switch (actionMask)
		{
		    case MotionEvent.ACTION_DOWN:
		    break;
		    case MotionEvent.ACTION_MOVE:
		    break;
		    case MotionEvent.ACTION_UP:
		    break;
		    case MotionEvent.ACTION_POINTER_UP:
		    break;
		    case MotionEvent.ACTION_POINTER_DOWN:
		    break;
		}
		return true;
    }
}
