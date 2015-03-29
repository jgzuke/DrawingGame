/**
 * behavior for all projectiles
 */
package com.drawinggame;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

public final class Proj_Tracker extends Sprite
{
	protected boolean hitBack;
	protected double xForward;
	protected double yForward;
	protected int power;
	protected double xDif = 0;
	protected double yDif = 0;
	protected double realX;
	protected double realY;
	protected Bitmap [] video;
	private double r2d = 180/Math.PI;
	private double speed;
	private SpriteController spriteController;
	private ArrayList<Enemy> enemies;
	private boolean onPlayersTeam;
	private ArrayList<Structure> structures;
	Controller control;
	public Proj_Tracker(Controller creator, int X, int Y, int Power, double Speed, double Rotation, SpriteController spriteControllerSet, boolean isOnPlayersTeam)
	{
		super(X, Y, Rotation, creator.imageLibrary.shotPlayer[0]);
		spriteController = spriteControllerSet;
		video = creator.imageLibrary.shotPlayer;
		control = creator;
		speed = Speed;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		if(control.wallController.checkHitBack(x, y, false))
		{
			explodeBack();
		}
		x +=(xForward);
		y +=(yForward);
		if(control.wallController.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
		realX = x;
		realY = y;
		power = Power;
		while(rotation<0)
		{
			rotation+=360;
		}
		onPlayersTeam = isOnPlayersTeam;
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
	 * moves ball forward and decreases power
	 */
	@ Override
	protected void frameCall()
	{
		for(int i = 0; i < 8; i++)
		{
			realX += xForward/8;
			realY += yForward/8;
			hitTarget((int)realX, (int)realY);
			hitBack((int)realX, (int)realY);
		}
		x = (int) realX;
		y = (int) realY;
	}
	protected boolean goodTarget(Sprite s, int d)
	{
		xDif = s.x-x;
		yDif = s.y-y;
		double distance = Math.sqrt(Math.pow(xDif, 2)+Math.pow(yDif, 2));
		double newRotation = Math.atan2(yDif, xDif) * r2d;
		double needToTurn = Math.abs(rotation-newRotation);
		if(needToTurn>180) needToTurn = 360-needToTurn;
		if(needToTurn<20&&distance<d)
		{
			return !control.wallController.checkObstructionsPoint((int)x, (int)y, (int)s.x, (int)s.y, false, 10);
		}
		return false;
	}
	public double compareRot(double newRotation)
	{
		newRotation*=r2d;
		double fix = 400;
		while(newRotation<0) newRotation+=360;
		while(rotation<0) rotation+=360;
		if(newRotation>290 && rotation<70) newRotation-=360;
		if(rotation>290 && newRotation<70) rotation-=360;
		fix = newRotation-rotation;
		return fix;
	}
	public void explodeBack()
	{
		spriteController.createProj_TrackerAOE((int) realX, (int) realY, 30, false, onPlayersTeam);
		deleted = true;
	}
	public void explode()
	{
		spriteController.createProj_TrackerAOE((int) realX, (int) realY, power/2, true, onPlayersTeam);
		deleted = true;
	}
	protected void hitTarget(int x, int y)
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null && !deleted)
			{
				xDif = x - enemies.get(i).x;
				yDif = y - enemies.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					enemies.get(i).getHit((int)power);
					explode();
				}
			}
		}
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null && !deleted)
			{
				xDif = x - structures.get(i).x;
				yDif = y - structures.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					structures.get(i).getHit((int)power);
					explode();
				}
			}
		}
	}
	protected void hitBack(int x, int y)
	{
		if(control.wallController.checkHitBack(x, y, false) && !deleted)
		{
			explodeBack();
		}
	}
}