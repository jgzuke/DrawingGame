package com.drawinggame;
public final class Control_Mage extends Control_Induvidual
{
	protected Enemy_Mage mage;
	public Control_Mage(Controller control, Enemy_Mage humanSet, boolean onPlayersTeam)
	{
		super(control, humanSet, onPlayersTeam);
		mage = humanSet;
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		if(mage.action.equals("Roll"))
		{
		} else if(mage.action.equals("Move"))
		{
		} else				// INTERUPTABLE PART 
		{
			if(retreating)
			{
				if(mage.checkDanger()>0)
				{
					if(mage.rollTimer<0)
					{
						mage.rollSidewaysDanger();
					} else
					{
						mage.runSidewaysDanger();
					}
				} else 
				{
					mage.runTowardsDestination();
				}
			} else if(enemiesAround())
			{
				Enemy target = findClosestEnemy(mage);
				if(target == null) return;
				double distanceToTarget = mage.distanceTo(target);
				if(distanceToTarget<60)		// MAGES ALWAYS MOVING, DONT STOP TO SHOOT
				{
					mage.rollAway(target);
				} else if(mage.checkDanger()>0)
				{
					if(mage.rollTimer<0)
					{
						mage.rollSidewaysDanger();
					} else
					{
						mage.runSidewaysDanger();
					}
				} else if(distanceToTarget<100)
				{
					mage.runAway(target);
				} else if(distanceToTarget < 160)
				{
					mage.runAround(120, (int)distanceToTarget, target);
				} else
				{
					mage.runTowards(target);
				}
				
				if(mage.shoot>3&&mage.energy>35&& distanceToTarget < 160)
				{
					mage.shoot(target);
				}
			} else if(mage.hasDestination)
			{
				mage.runTowardsDestination();
			}
		}
	}
}