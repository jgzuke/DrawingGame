package com.drawinggame;

import java.util.ArrayList;

abstract public class Game_Control
{
	protected Controller control;
	protected SpriteController spriteControl;
	protected int mana = 1000;
	protected ArrayList<Control_Main> enemyControllers;
	protected ArrayList<Control_Main> allyControllers;
	protected ArrayList<Enemy> enemies;
	protected ArrayList<Enemy> allies;
	public Game_Control(Controller controlSet)
	{
		control = controlSet;
	}
	protected void setEnemies(boolean onPlayersTeam)
	{
		spriteControl = control.spriteController;
		if(onPlayersTeam)
		{
			enemyControllers = control.spriteController.enemyControllers;
			enemies = control.spriteController.enemies;
			allyControllers = control.spriteController.allyControllers;
			allies = control.spriteController.allies;
		} else
		{
			enemyControllers = control.spriteController.allyControllers;
			enemies = control.spriteController.allies;
			allyControllers = control.spriteController.enemyControllers;
			allies = control.spriteController.enemies;
		}
	}
	protected void frameCall()
	{
		mana ++;
		if(mana > 1000) mana = 1000;
	}
}