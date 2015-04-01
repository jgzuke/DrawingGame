package com.drawinggame;

abstract public class Control_Main
{
	protected Controller control;
	protected boolean isGroup;
	protected boolean deleted;
	public Control_Main(Controller controlSet)
	{
		control = controlSet;
	}
	abstract protected void frameCall();
	abstract protected void removeHuman(EnemyShell target);
	abstract protected void setDestination(double x, double y);
}