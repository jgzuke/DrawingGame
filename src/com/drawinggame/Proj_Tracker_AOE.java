/**
 * behavior for all AOE effects
 */
package com.drawinggame;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

public final class Proj_Tracker_AOE extends Sprite
{
	protected double r2d = 180 / Math.PI;
	protected boolean normal;
	protected double widthDone = 0;
	protected double xDif = 0;
	protected double yDif = 0;
	protected byte alpha;
	protected int alphaDown;
	protected int timeToDeath;
	protected SpriteController spriteController;
	private ArrayList<Enemy> enemies;
	private boolean onPlayersTeam;
	private ArrayList<Structure> structures;
	protected boolean damaging = true;
	/**
	 * sets position, size, and behaviors
	 * @param creator control object
	 * @param X starting x coordinate
	 * @param Y starting y coordinate
	 * @param Power power or size to start at
	 * @param Shrinking whether it is shrinking or growing
	 */
	Controller control;
	public Proj_Tracker_AOE(Controller creator, int X, int Y, double Power, boolean Shrinking, SpriteController spriteControllerSet, Bitmap image, boolean isDamaging, boolean isOnPlayersTeam)
	{
		super(X, Y, 2, 2, 0, image);
		spriteController = spriteControllerSet;
		normal = Shrinking;
		control = creator;
		x = X;
		y = Y;
		width = 2;
		height = 2;
		alpha = (byte) 254;
		if(!normal)
		{
			timeToDeath = 10;
		} else
		{
			timeToDeath = (int) (Math.pow(Power, 0.65) / 3.5)+5;
		}
		alphaDown = (int)(254/timeToDeath);
		onPlayersTeam = isOnPlayersTeam;
		damaging = isDamaging;
		if(onPlayersTeam)
		{
			enemies = spriteController.enemies;
			structures = spriteController.enemyStructures;
		} else
		{
			enemies = spriteController.allies;
			structures = spriteController.allyStructures;
		}
	}
	/**
	 * changes width and explodes at certain intervals
	 */
	@ Override
	protected void frameCall()
	{
		timeToDeath--;
		if(timeToDeath == 6)
		{
			damaging = false;
			if(!normal)
			{
				explode(110);
			}
		}
		if(timeToDeath == 3)
		{
			damaging = false;
			if(!normal)
			{
				explode(90);
			}
		}
		width += 10;
		height += 10;
		alpha -= alphaDown;
		if(timeToDeath == 0)
		{
			deleted = true;
		}
		widthDone = 7.5 + (width / 2);
		
		if(damaging)
		{
			for(int i = 0; i < enemies.size(); i++)
			{
				if(enemies.get(i) != null)
				{
					xDif = x - enemies.get(i).x;
					yDif = y - enemies.get(i).y;
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
					{
						double damage = 60;
						enemies.get(i).getHit((int)damage);
					}
				}
			}
			for(int i = 0; i < structures.size(); i++)
			{
				if(structures.get(i) != null)
				{
					xDif = x - structures.get(i).x;
					yDif = y - structures.get(i).y;
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
					{
						double damage = 60;
						structures.get(i).getHit((int)damage);
					}
				}
			}
		}
	}
	protected void explode(int power)
	{
		for(int i = 0; i<9; i++)
		{
			spriteController.createProj_TrackerAOE(x-15+control.getRandomInt(30), y-15+control.getRandomInt(30), power+22, true, onPlayersTeam);
		}
	}
	/**
	 * returns alpha value
	 * @return alpha value
	 */
	protected byte getAlpha() {
		return alpha;
	}
	/**
	 * returns 1.5 of width
	 * @return 1.5 of width
	 */
	protected double getWidth() {
		return width*1.5;
	}
	/**
	 * returns 1.5 of height
	 * @return 1.5 of height
	 */
	protected double getHeight() {
		return height*1.5;
	}
}