/**
 * AI and variables for rogues
 */
package com.drawinggame;

import java.util.ArrayList;

import com.spritelib.Sprite;


public final class Enemy_Archer extends Enemy
{
	private EnemyShell target;
	public Enemy_Archer(Controller creator, double X, double Y, double R, int HP, int ImageIndex, boolean isOnPayersTeam)
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
	/**
	 * select this enemy, take it out of a group if need be
	 */
	@Override
	protected void selectSingle()
	{
		if(!myController.isGroup) return;
		Control_Archer newControl = new Control_Archer(control, this, onPlayersTeam);
		if(onPlayersTeam)
		{
			control.spriteController.allyControllers.add(newControl);
		} else
		{
			control.spriteController.enemyControllers.add(newControl);
		}
	}
}