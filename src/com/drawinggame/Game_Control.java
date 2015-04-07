package com.drawinggame;

import java.util.ArrayList;

abstract public class Game_Control
{
	protected Controller control;
	protected int mana = 0;
	protected ArrayList<Control_Main> enemyControllers;
	protected ArrayList<Control_Main> allyControllers;
	protected ArrayList<Enemy> enemies;
	public Game_Control(Controller controlSet, boolean onPlayersTeam)
	{
		control = controlSet;
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
	protected void frameCall()
	{
		mana ++;
		if(mana > 1000) mana = 1000;
	}
}