/**
 * AI and variables for rogues
 */
package com.drawinggame;


public final class Enemy_Cleric extends Enemy
{
	int shoot = 4;
	int energy = 90;
	Enemy target;
	public Enemy_Cleric(Controller creator, double X, double Y, double R, int HP, int ImageIndex, boolean isOnPayersTeam)
	{
		super(creator, X, Y, R, HP, ImageIndex, isOnPayersTeam);
		speedCur = 3;
		frame=0;
		baseHp(HP);
		worth = 5;
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
		shoot++;
		if(shoot>6) shoot = 6;
		energy++;
		if (energy>45) energy=45;
		super.frameCall();
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
		shoot-=6;
		energy -= 22;
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
	private void getTarget()
	{
		if(target == null)
		{
			for(int i = 0; i < control.spriteController.enemies.size(); i++)
			{
				Enemy enemy = control.spriteController.enemies.get(i);
				if(enemy.hp < enemy.hpMax && distanceTo(enemy.x, enemy.y)<200 && checkLOS((int)enemy.x, (int)enemy.y))
				{
					target = enemy;
					i = 999;
				}
			}
		}
	}
	private void healTarget()
	{
		turnToward(target.x, target.y);
		target.hp += 20;
		frame = 5;
		playing = false;
		if(target.hp > target.hpMax)
		{
			target.hp = target.hpMax;
			target = null;
		}
	}
}