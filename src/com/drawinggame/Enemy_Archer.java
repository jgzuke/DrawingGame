/**
 * AI and variables for rogues
 */
package com.drawinggame;

import java.util.ArrayList;

import com.spritelib.Sprite;


public final class Enemy_Archer extends Enemy
{
	private EnemyShell target;
	public Enemy_Archer(double X, double Y, boolean isOnPayersTeam, int ImageIndex)
	{
		super(X, Y, 1700, ImageIndex, isOnPayersTeam);
		frame=0;
		baseHp(1700);
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
		if(action.equals("Shoot"))
		{
			shooting();
		}
	}
	protected void shooting()
	{
		int v = 10; //projectile velocity
		if(frame<34) //geting weapon ready+aiming
		{
			aimAheadOfTarget(v*2, target); //TODO add extra frames for when you aim
		} else if(frame==36) // shoots
		{
			control.spriteController.createProj_Tracker(rads * r2d, v, 130, x, y, onPlayersTeam);
			myController.archerDoneFiring(this);
		}
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	  stun	 melee		sheild	  hide	 shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, e, e, e, e, e, {20, 49}};
		return temp;
	}
	protected void shoot(EnemyShell targetSet)
	{
		target = targetSet;
		turnToward(target);
		action = "Shoot";
		frame=frames[6][0];
	}
}