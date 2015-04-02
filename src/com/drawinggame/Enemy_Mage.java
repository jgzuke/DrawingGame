/**
 * AI and variables for rogues
 */
package com.drawinggame;



public final class Enemy_Mage extends Enemy
{
	int shoot = 4;
	int energy = 90;
	public Enemy_Mage(Controller creator, double X, double Y, double R, int HP, int ImageIndex, boolean isOnPayersTeam)
	{
		super(creator, X, Y, R, HP, ImageIndex, isOnPayersTeam);
		frame=0;
		baseHp(HP);
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
		if(shoot>4) shoot = 4;
		energy++;
		if (energy>45) energy=45;
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
		shoot-=4;
		energy -= 15;
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
		Control_Mage newControl = new Control_Mage(control, this);
		control.spriteController.humanControllers.add(newControl);
	}
}