package com.drawinggame;

import lx.interaction.dollar.Point;

abstract public class Control_Induvidual extends Control_Main
{
	protected EnemyShell human;
	public Control_Induvidual(Controller controlSet, EnemyShell humanSet, boolean onPlayersTeam)
	{
		super(controlSet, onPlayersTeam);
		human = humanSet;
		human.setController(this);
		isGroup = false;
		groupLocation = new Point(human.x, human.y);
		groupRadius = 20;
	}
	protected void frameCall()
	{
		groupLocation.X = human.x;
		groupLocation.Y = human.y;
	}
	@Override
	protected void setDestination(Point p)
	{
		if(enemiesAround()) retreating = true;
		human.hasDestination = true;
		human.destinationX = (int)p.X;
		human.destinationY = (int)p.Y;
	}
	@Override
	protected void removeHuman(EnemyShell target)
	{
		if(onPlayersTeam)
		{
			control.spriteController.allyControllers.remove(this);
		} else
		{
			control.spriteController.enemyControllers.remove(this);
		}
	}
	@Override
	protected void cancelMove()
	{
		human.hasDestination = false;
	}
	@Override
	protected void archerDoneFiring(Enemy_Archer archer) {}
}