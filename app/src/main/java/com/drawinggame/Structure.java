/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.drawinggame;

import android.graphics.Bitmap;

abstract public class Structure extends Sprite
{
	public Structure(double X, double Y, int Width, int Height,
			double Rotation, Bitmap Image, boolean isOnPlayersTeam) {
		super(X, Y, Width, Height, Rotation, Image);
		onPlayersTeam = isOnPlayersTeam;
	}
	protected int hp;
	protected int hpMax;
	protected int timer = 0;
	protected int width;
	protected int height;
	protected int worth;
	protected boolean onPlayersTeam;
	Controller control;
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@ Override
	protected void frameCall()
	{
		timer++;
		hp+=5;
		if(hp>hpMax)
		{
			hp=hpMax;
		}
	}
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		hp -= damage;
		if(hp < 1)
		{
			hp = 0;
			if(onPlayersTeam)
			{
				control.spriteController.allyStructures.remove(this);
			} else
			{
				control.spriteController.enemyStructures.remove(this);
			}
			control.spriteController.createProj_TrackerAOE(x, y, 180, false, onPlayersTeam);
			control.soundController.playEffect("burst");
		}
	}
}