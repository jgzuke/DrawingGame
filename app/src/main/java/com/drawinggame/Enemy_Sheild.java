/**
 * AI and variables for rogues
 */
package com.drawinggame;


public final class Enemy_Sheild extends Enemy
{
	public Enemy_Sheild(double X, double Y, boolean isOnPayersTeam, int ImageIndex)
	{
		super(X, Y, 5000, ImageIndex, isOnPayersTeam);
		frame=0;
		baseHp(5000);
		if(control.getRandomInt(3) == 0)
		{
			runRandom();
		}
		rotation = control.getRandomInt(360);
		rads = rotation/r2d;
		frames = makeFrames();
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		if(action.equals("Melee"))
		{
			attacking();
		} else if(action.equals("Sheild"))
		{
			blocking();
		}
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	  stun	 melee		sheild	  hide	 shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, e, e, {20, 45, 27, 36}, {46, 55}, e, e};
		return temp;
	}
	protected void attacking()
	{
		for(int i = 2; i < frames[3].length; i++)
		{
			if(frame==frames[3][i])
			{
				meleeAttack(400, 25, 20);
			}
		}
	}
	protected void blocking()
	{
		turnToward();
		if(frame<55 && frame> 47)
		{
			if(checkDanger()>0) frame = 50;
		}
	}
	protected void block()
	{
		turnToward();
		action = "Sheild";
		frame=frames[4][0];
	}
	protected void attack(EnemyShell target)
	{
		turnToward(target);
		action = "Melee";
		frame=frames[3][0];
	}
}