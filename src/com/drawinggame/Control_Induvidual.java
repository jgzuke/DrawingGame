package com.drawinggame;

abstract public class Control_Induvidual extends Control_Main
{
	protected EnemyShell human;
	public Control_Induvidual(Controller controlSet, EnemyShell humanSet)
	{
		super(controlSet);
		human = humanSet;
		isGroup = false;
	}
	abstract protected void frameCall();
	@Override
	protected void setDestination(double x, double y)
	{
		human.hasDestination = true;
		human.destinationX = (int)x;
		human.destinationY = (int)y;
	}
}