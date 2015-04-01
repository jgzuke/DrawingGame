package com.drawinggame;

abstract public class Control_Main
{
	protected Controller control;
	protected boolean isGroup;
	public Control_Main(Controller controlSet)
	{
		control = controlSet;
	}
	abstract protected void frameCall();
	abstract protected void setDestination(double x, double y);
}