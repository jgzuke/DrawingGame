package com.drawinggame;
public final class Control_Sheild extends Control_Induvidual
{
	protected Enemy_Sheild sheild;
	public Control_Sheild(Controller control, Enemy_Sheild humanSet, boolean onPlayersTeam)
	{
		super(control, humanSet, onPlayersTeam);
		sheild = humanSet;
	}
	@Override
	protected void frameCall()
	{
		if(sheild.action.equals("Melee"))
		{
		} else if(sheild.action.equals("Sheild"))
		{
		} else if(sheild.action.equals("Move"))
		{
		} else
		{
			if(sheild.hasDestination)
			{
				sheild.runTowardsDestination();
			}
		}
	}
}