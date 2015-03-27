/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.drawinggame;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

abstract public class Structure extends Sprite
{
	public Structure(double X, double Y, int Width, int Height,
			double Rotation, Bitmap Image) {
		super(X, Y, Width, Height, Rotation, Image);
		// TODO Auto-generated constructor stub
	}
	protected int hp;
	protected int hpMax;
	protected int timer = 0;
	protected int width;
	protected int height;
	protected int worth;
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
		if(!deleted)
		{
			hp -= damage;
			if(hp < 1)
			{
				hp = 0;
				deleted = true;
				control.spriteController.createProj_TrackerEnemyAOE(x, y, 180, false);
				control.soundController.playEffect("burst");
			}
		}
	}
}