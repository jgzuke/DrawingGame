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
		speedCur = 3.5;
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
			checkLOS(target);
			if(!LOS)
			{
				ArrayList<Sprite> allEnemies = new ArrayList<Sprite>();
				for(int i = 0; i < enemies.size(); i++)
				{
					double distance = checkDistance(x, y, enemies.get(i).x,  enemies.get(i).y);
					if(distance <= 50) return;
					if(distance < 160) allEnemies.add(enemies.get(i));
				}
				for(int i = 0; i < enemyStructures.size(); i++)
				{
					double distance = checkDistance(x, y, enemyStructures.get(i).x,  enemyStructures.get(i).y);
					if(distance <= 50) return;
					if(distance < 160) allEnemies.add(enemyStructures.get(i));
				}
				for(int i = 0; i < allEnemies.size(); i++)
				{
					checkLOS(allEnemies.get(i));
					if(LOS)
					{
						target = (EnemyShell) allEnemies.get(i);
						break;
					}
				}
				if(!LOS) return;
			}
			control.spriteController.createProj_Tracker(rotation, v, 130, x, y, onPlayersTeam);
			control.soundController.playEffect("arrowrelease");
			double distance = checkDistance(x, y, target.x,  target.y);
			if(hp>600&&distance<160&&distance>50) frame=25; // shoots again
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