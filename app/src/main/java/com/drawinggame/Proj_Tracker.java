/**
 * behavior for all projectiles
 */
package com.drawinggame;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.util.Log;

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
	protected byte alpha;
	private SpriteController spriteController;
	private ArrayList<Enemy> enemies;
	private boolean onPlayersTeam;
	private ArrayList<Structure> structures;
	public Proj_Tracker(Controller creator, int X, int Y, int Power, double Speed, double Rotation, SpriteController spriteControllerSet, Bitmap image, boolean isOnPlayersTeam)
	{
		super(X, Y, Rotation, image);
		spriteController = spriteControllerSet;
		video = creator.textureLibrary.shotPlayer;
		control = creator;
		speed = Speed;
		alpha = (byte) 254;
		xForward = Math.cos(Rotation/r2d) * Speed;
		yForward = Math.sin(Rotation/r2d) * Speed;
		if(control.wallController.checkHitBack(x, y, false))
		{
			explodeBack();
			return;
		}
		x +=(xForward);
		y +=(yForward);
		if(control.wallController.checkHitBack(x, y, false))
		{
			explodeBack();
			return;
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
		alpha -= 4;
		if(onPlayersTeam)
		{
			image = control.textureLibrary.shotPlayer[(int)(Math.random()*5)];
		} else
		{
			image = control.textureLibrary.shotEnemy[(int)(Math.random()*5)];
		}
		for(int i = 0; i < 4; i++)
		{
			realX += xForward/4;
			realY += yForward/4;
			hitTarget((int)realX, (int)realY);
			hitBack((int)realX, (int)realY);
		}
		x = (int) realX;
		y = (int) realY;
		if(alpha == 0 || alpha == 1 || alpha == 2 || alpha == 3)
		{
			explodeBack();
			Log.e("myid", "works");
		}
	}
	protected boolean goodTarget(Sprite s, int d)
	{
		xDif = s.x-x;
		yDif = s.y-y;
		double distance = Math.sqrt(Math.pow(xDif, 2)+Math.pow(yDif, 2));
		double newRotation = Math.atan2(yDif, xDif) * r2d;
		double needToTurn = Math.abs(rotation-newRotation);
		if(needToTurn>180) needToTurn = 360-needToTurn;
		if(needToTurn*distance<1500&&distance<d)
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
		if(onPlayersTeam)
		{
			control.spriteController.proj_TrackerAs.remove(this);
		} else
		{
			control.spriteController.proj_TrackerEs.remove(this);
		}
	}
	public void explode()
	{
		spriteController.createProj_TrackerAOE((int) realX, (int) realY, (power*alpha/400)+50, true, onPlayersTeam);
		if(onPlayersTeam)
		{
			control.spriteController.proj_TrackerAs.remove(this);
		} else
		{
			control.spriteController.proj_TrackerEs.remove(this);
		}
	}
	protected void hitTarget(int x, int y)
	{
		if(enemies == null || structures == null)
		{
			control.spriteController.proj_TrackerAs.remove(this);
			return;
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				xDif = x - enemies.get(i).x;
				yDif = y - enemies.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					enemies.get(i).getHit(power);
					explode();
					return;
				}
			}
		}
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null)
			{
				xDif = x - structures.get(i).x;
				yDif = y - structures.get(i).y;
				double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
				if(distance < 600)
				{
					structures.get(i).getHit((int)power);
					explode();
					return;
				}
			}
		}
	}
	protected void hitBack(int x, int y)
	{
		if(control.wallController.checkHitBack(x, y, false))
		{
			explodeBack();
		}
	}
}