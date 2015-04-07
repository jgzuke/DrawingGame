package com.drawinggame;
public final class Control_Archer extends Control_Induvidual
{
	protected Enemy_Archer archer;
	public Control_Archer(Controller control, Enemy_Archer humanSet, boolean onPlayersTeam)
	{
		super(control, humanSet, onPlayersTeam);
		archer = humanSet;
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		if(archer.action.equals("Shoot"))
		{
		} else if(archer.action.equals("Move"))
		{
		} else 			// INTERUPTABLE PART
		{
			if(retreating)
			{
				archer.runTowardsDestination();
			} else if(enemiesAround())
			{
				Enemy target = findClosestEnemy(archer);
				if(target == null) return;
				double distanceToTarget = archer.distanceTo(target);
				if(distanceToTarget < 50 || archer.hp<600 && distanceToTarget<100)
				{
					archer.runAway(target);
				} else if(distanceToTarget<140)
				{
					archer.shoot(target);
				} else
				{
					archer.runTowards(target);
				}
			} else if(archer.hasDestination)
			{
				archer.runTowardsDestination();
			}
		}
	}
	@Override
	protected void archerDoneFiring(Enemy_Archer archer)
	{
		if(enemiesAround() && !archer.hasDestination)
		{
			Enemy target = findClosestEnemy(archer);
			if(target == null) return;
			double distanceToTarget = archer.distanceTo(target);
			if(archer.hp>600&&distanceToTarget<160&&distanceToTarget>50)
			{
				archer.shoot(target);
			}
		}
	}
}