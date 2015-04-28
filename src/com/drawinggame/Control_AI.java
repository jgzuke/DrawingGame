package com.drawinggame;
public class Control_AI
{
	protected static boolean archerFrame(Enemy_Archer archer, boolean retreating, boolean groupEngaged)
	{
		
		if(archer.action.equals("Shoot"))
		{
		} else if(archer.action.equals("Move"))
		{
		} else 			// INTERUPTABLE PART
		{
			if(retreating)
			{
				archer.runTowardsDestination();
			} else if(groupEngaged)
			{
				Enemy target = archer.myController.findClosestEnemy(archer);
				if(target == null) return false;
				double distanceToTarget = archer.distanceTo(target);
				if(distanceToTarget < 70 || archer.hp<600 && distanceToTarget<100)
				{
					archer.runAway(target);
				} else if(distanceToTarget<200)
				{
					archer.shoot(target);
				} else
				{
					archer.runTowards(target);
				}
			} else if(archer.hasDestination)
			{
				archer.runTowardsDestination();
			} else
			{
				return false;
			}
		}
		return true;
	}
	protected static boolean mageFrame(Enemy_Mage mage, boolean retreating, boolean groupEngaged)
	{
		if(mage.action.equals("Roll"))
		{
			return true;
		}
		Enemy target = mage.myController.findClosestEnemy(mage);
		if(target != null)
		{
			double distanceToTarget = mage.distanceTo(target);
			if(mage.energy>35&& distanceToTarget < 210)
			{
				mage.shoot(target);
			}
		}
		
		if(mage.checkDanger()>0)
		{
			if(mage.rollTimer<0)
			{
				mage.rollSidewaysDanger();
			} else
			{
				mage.runSidewaysDanger();
			}
		} else if(mage.action.equals("Move"))
		{
		} else				// INTERUPTABLE PART 
		{
			if(retreating)
			{
				mage.runTowardsDestination();
			} else if(groupEngaged)
			{
				if(target != null)
				{
					double distanceToTarget = mage.distanceTo(target);
					if(distanceToTarget<80)		// MAGES ALWAYS MOVING, DONT STOP TO SHOOT
					{
						mage.rollAway(target);
					} else if(distanceToTarget<120)
					{
						mage.runAway(target);
					} else if(distanceToTarget < 200)
					{
						mage.runAround(160, (int)distanceToTarget, target);
					} else
					{
						mage.runTowards(target);
					}
				}
			} else if(mage.hasDestination)
			{
				mage.runTowardsDestination();
			} else
			{
				return false;
			}
		}
		return true;
	}
	protected static boolean sheildFrame(Enemy_Sheild sheild, boolean retreating, boolean groupEngaged)
	{
		if(sheild.action.equals("Melee"))
		{
		} else if(sheild.action.equals("Sheild"))
		{
		} else if(sheild.checkDanger()>1)
		{
			sheild.block();
		} else if(sheild.action.equals("Move"))
		{
		} else
		{				// INTERUPTABLE PART
			if(retreating)
			{
				sheild.runTowardsDestination();
			} else if(groupEngaged)
			{
				Enemy target = sheild.myController.findClosestEnemy(sheild);
				if(target == null) return false;
				double distanceToTarget = sheild.distanceTo(target);
				if(distanceToTarget < 30)
				{
					sheild.attack(target);
				} else
				{
					sheild.runTowards(target);
				}
			} else if(sheild.hasDestination)
			{
				sheild.runTowardsDestination();
			} else
			{
				return false;
			}
		}
		return true;
	}
	protected static void archerDoneFiring(Enemy_Archer archer, boolean canShoot)
	{
		if(canShoot)
		{
			Enemy target = archer.myController.findClosestEnemy(archer);
			if(target == null) return;
			double distanceToTarget = archer.distanceTo(target);
			if(archer.hp>600&&distanceToTarget<220&&distanceToTarget>70)
			{
				archer.shoot(target);
			}
		}
	}
}