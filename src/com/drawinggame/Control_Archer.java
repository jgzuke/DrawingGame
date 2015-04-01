package com.drawinggame;
public final class Control_Archer extends Control_Induvidual
{
	protected Enemy_Archer archer;
	public Control_Archer(Controller control, Enemy_Archer humanSet)
	{
		super(control, humanSet);
		archer = humanSet;
	}
	@Override
	protected void frameCall()
	{
		if(archer.action.equals("Shoot"))
		{
		} else if(archer.action.equals("Move"))
		{
		} else
		{
			if(archer.hasDestination)
			{
				archer.runTowardsDestination();
			}
		}
	}
}