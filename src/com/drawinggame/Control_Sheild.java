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
		super.frameCall();
		if(sheild.action.equals("Melee"))
		{
		} else if(sheild.action.equals("Sheild"))
		{
		} else if(sheild.action.equals("Move"))
		{
		} else
		{				// INTERUPTABLE PART
			if(retreating)
			{
				sheild.runTowardsDestination();
			} else if(enemiesAround())
			{
				Enemy target = findClosestEnemy(sheild);
				if(target == null) return;
				double distanceToTarget = sheild.distanceTo(target);
				if(distanceToTarget < 30)
				{
					sheild.attack(target);
				} else if(sheild.checkDanger()>1)
				{
					sheild.block();
				} else
				{
					sheild.runTowards(target);
				}
			} else if(sheild.hasDestination)
			{
				sheild.runTowardsDestination();
			}
		}
	}
}