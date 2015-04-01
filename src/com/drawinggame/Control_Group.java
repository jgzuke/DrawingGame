package com.drawinggame;

import java.util.ArrayList;

public final class Control_Group extends Control_Main
{
	protected ArrayList<EnemyShell> humans;
	public Control_Group(Controller controlSet, ArrayList<EnemyShell> humansSet)
	{
		super(controlSet);
		humans = humansSet;
		isGroup = false;
	}
	protected void frameCall()
	{
		
	}
	@Override
	protected void setDestination(double x, double y)
	{
		/*human.hasDestination = true;
		human.destinationX = (int)x;
		human.destinationY = (int)y;*/
	}
	@Override
	protected void removeHuman(EnemyShell target)
	{
		humans.remove(target);
		if(humans.isEmpty())
		{
			deleted = true;
		}
	}
}