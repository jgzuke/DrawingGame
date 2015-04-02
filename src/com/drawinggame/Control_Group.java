package com.drawinggame;

import java.util.ArrayList;

public final class Control_Group extends Control_Main
{
	protected ArrayList<Enemy> humans;
	protected int groupRotation;
	protected double groupX;
	protected double groupY;
	protected byte currentForm = 0; //0 is norm, 1 is standGround, 2 is V or attack
	public Control_Group(Controller controlSet, ArrayList<Enemy> humansSet)
	{
		super(controlSet);
		humans = humansSet;
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).setController(this);
		}
		groupX = averageX();
		groupY = averageY();
		groupRotation = averageRotation();
		isGroup = true;
	}
	protected void frameCall()
	{
		
	}
	protected void formUp(int rotation, double newX, double newY)
	{
		
	}
	protected double averageX()
	{
		double sum = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sum += humans.get(i).x;
		}
		return sum/humans.size();
	}
	protected double averageY()
	{
		double sum = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sum += humans.get(i).y;
		}
		return sum/humans.size();
	}
	protected int averageRotation()
	{
		int sum = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sum += humans.get(i).rotation;
		}
		return sum/humans.size();
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
		if(humans.size() == 1)
		{
			humans.get(0).selectSingle();
			deleted = true;
		}
	}
}