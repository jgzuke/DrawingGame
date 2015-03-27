/**
 * behavior for rectangular walls
 */
package com.drawinggame;

import java.util.ArrayList;

public class Wall_Rectangle extends Wall
{
	private int x;
	private int y;
	protected int oRX1;
	protected int oRX2;
	protected int oRY1;
	protected int oRY2;
	/**
	 * sets variables and stores some in control object array
	 * @param creator control object
	 * @param ORX x position
	 * @param ORY y position
	 * @param wallWidth width of wall
	 * @param wallHeight height of wall
	 * @param HitPlayer whether wall interacts with the player
	 * @param Tall whether or not the wall is tall enough to stop projectiles
	 */
	public Wall_Rectangle(Controller creator, int ORX, int ORY, int wallWidth, int wallHeight, boolean Tall)
	{
		tall = Tall;
		oRX1 = ORX;
		oRY1 = ORY;
		oRX2 = ORX+wallWidth;
		oRY2 = ORY+wallHeight;
		x = (oRX1 + oRX2) / 2;
		y = (oRY1 + oRY2) / 2;
		control = creator;
		oRX1 -=humanWidth;
		oRX2 +=humanWidth;
		oRY1 -=humanWidth;
		oRY2 +=humanWidth;
	}
	/**
	 * checks whether wall hits player or enemies
	 */
        @ Override
        protected void frameCall()
        {
        	ArrayList<Enemy> enemies = control.spriteController.enemies;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				if(enemies.get(i).x > oRX1 && enemies.get(i).x < oRX2 && enemies.get(i).y > oRY1 && enemies.get(i).y < oRY2)
				{
					enemies.get(i).hitWall();
						double holdX;
						double holdY;
						if(enemies.get(i).x > x)
						{
							holdX = Math.abs(enemies.get(i).x - oRX2);
						} else
						{
							holdX = Math.abs(enemies.get(i).x - oRX1);
						}
						if(enemies.get(i).y > y)
						{
							holdY = Math.abs(enemies.get(i).y - oRY2);
						} else
						{
							holdY = Math.abs(enemies.get(i).y - oRY1);
						}
						while(enemies.get(i).rotation<0) enemies.get(i).rotation+=360;
						if((holdX) < (holdY))
						{
							if(enemies.get(i).x > x)
							{
								enemies.get(i).x = oRX2;
								if(enemies.get(i).rotation>90&&enemies.get(i).rotation<180)
								{
									enemies.get(i).rotation -=2;
								} else
								{
									enemies.get(i).rotation +=2;
								}
							}
							else
							{
								enemies.get(i).x = oRX1;
								if(enemies.get(i).rotation>0&&enemies.get(i).rotation<90)
								{
									enemies.get(i).rotation +=2;
								} else
								{
									enemies.get(i).rotation -=2;
								}
							}
						} else
						{
							if(enemies.get(i).y > y)
							{
								enemies.get(i).y = oRY2;
								if(enemies.get(i).rotation>180&&enemies.get(i).rotation<270)
								{
									enemies.get(i).rotation -=2;
								} else
								{
									enemies.get(i).rotation +=2;
								}
							}
							else
							{
								enemies.get(i).y = oRY1;
								if(enemies.get(i).rotation>0&&enemies.get(i).rotation<90)
								{
									enemies.get(i).rotation -=2;
								} else
								{
									enemies.get(i).rotation +=2;
								}
							}
						}
				}
			}
		}
	}
}