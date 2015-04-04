package com.drawinggame;

import lx.interaction.dollar.Point;

abstract public class Control_Induvidual extends Control_Main
{
	protected EnemyShell human;
	public Control_Induvidual(Controller controlSet, EnemyShell humanSet)
	{
		super(controlSet);
		human = humanSet;
		human.setController(this);
		isGroup = false;
	}
	abstract protected void frameCall();
	@Override
	protected void setDestination(Point p)
	{
		human.hasDestination = true;
		human.destinationX = (int)p.X;
		human.destinationY = (int)p.Y;
	}
	@Override
	protected void removeHuman(EnemyShell target)
	{
		deleted = true;
	}
}