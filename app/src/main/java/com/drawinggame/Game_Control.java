package com.drawinggame;

import java.util.ArrayList;

abstract public class Game_Control
{
	protected Controller control;
	protected SpriteController spriteControl;
	protected double mana;
	protected double manaGain = 0.2;
	protected int startMana = 200;
	protected double maxMana = 500;
	protected double score = 0;
	protected ArrayList<Control_Main> enemyControllers;
	protected ArrayList<Control_Main> allyControllers;
	protected ArrayList<Enemy> enemies;
	protected ArrayList<Enemy> allies;
	public Game_Control(Controller controlSet)
	{
		control = controlSet;
		mana = startMana;
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
		mana += manaGain;
		if(mana > maxMana) mana = maxMana;
	}
	protected void reset()
	{
		score = 0;
		mana = startMana;
		setManaStuff();
	}
	protected void score(double points)
	{
		score += points;
		mana += score;
		setManaStuff();
	}
	protected void setManaStuff()
	{
		manaGain = 0.3 + score/1000;
		maxMana = 500 + score/10;
	}
}