package com.drawinggame;

import java.util.ArrayList;

import lx.interaction.dollar.Point;

abstract public class Control_Main
{
	protected Controller control;
	protected boolean isGroup;
	protected boolean onPlayersTeam;
	protected Point groupLocation;
	protected double groupRadius;
	protected ArrayList<Control_Main> enemyControllers;
	protected ArrayList<Enemy> enemies;
	public Control_Main(Controller controlSet, boolean isOnPlayersTeam)
	{
		control = controlSet;
		onPlayersTeam = isOnPlayersTeam;
		if(onPlayersTeam)
		{
			enemyControllers = control.spriteController.enemyControllers;
			enemies = control.spriteController.enemies;
		} else
		{
			enemyControllers = control.spriteController.allyControllers;
			enemies = control.spriteController.allies;
		}
	}
	protected boolean enemiesAround()
	{
		for(int i = 0; i < enemyControllers.size(); i++)
		{
			double distance = groupLocation.distanceTo(enemyControllers.get(i).groupLocation);
			if(distance < 300 + groupRadius + enemyControllers.get(i).groupRadius)
			{
				return true;
			}
		}
		return false;
	}
	protected Enemy findClosestEnemy(Enemy me)
	{
		double closest = Double.MAX_VALUE;
		int index = 0;
		for(int i = 0; i < enemies.size(); i++)
		{
			double distance = getDistance(me, enemies.get(i));
			if(distance < closest)
			{
				closest = distance;
				index = i;
			}
		}
		return enemies.get(index);
	}
	protected double getDistance(Enemy me, Enemy him)
	{
		return Math.sqrt(Math.pow(me.x-him.x, 2)+Math.pow(me.y-him.y, 2));
	}
	abstract protected void frameCall();
	abstract protected void removeHuman(EnemyShell target);
	abstract protected void setDestination(Point p);
	abstract protected void cancelMove();
}