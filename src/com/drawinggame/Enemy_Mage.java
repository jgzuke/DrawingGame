/**
 * AI and variables for rogues
 */
package com.drawinggame;



public final class Enemy_Mage extends Enemy
{
	int shoot = 4;
	int energy = 90;
	public Enemy_Mage(Controller creator, double X, double Y, boolean isOnPayersTeam)
	{
		super(creator, X, Y, 700, 2, isOnPayersTeam);
		frame=0;
		baseHp(700);
		if(control.getRandomInt(3) == 0)
		{
			runRandom();
		}
		rotation = control.getRandomInt(360);
		rads = rotation/r2d;
		frames = makeFrames();
		
	}
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		shoot++;
		if(shoot>7) shoot = 7;
		energy++;
		if (energy>90) energy=90;
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	    stun   melee   sheild   hide   shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, {20, 31}, e, e, e, e, e};
		return temp;
	}
	protected void shoot(EnemyShell target)
	{
		shoot-=7;
		energy -= 35;
		int v = 10;		//projectile velocity
		double saveRads = rotation/r2d;
		aimAheadOfTarget(v*2, target);	// aim closer to player
		rads+=0.1;
		rads-=control.getRandomDouble()*0.2;	// add random factor to shot
		control.spriteController.createProj_Tracker(rads * r2d, v, 130, x, y, onPlayersTeam);
		control.soundController.playEffect("arrowrelease");
		rads = saveRads;
		rotation = rads*r2d;
	}
	/**
	 * select this enemy, take it out of a group if need be
	 */
	@Override
	protected void selectSingle()
	{
		if(!myController.isGroup) return;
		Control_Mage newControl = new Control_Mage(control, this, onPlayersTeam);
		if(onPlayersTeam)
		{
			control.spriteController.allyControllers.add(newControl);
		} else
		{
			control.spriteController.enemyControllers.add(newControl);
		}
	}
}