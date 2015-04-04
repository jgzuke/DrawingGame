package com.drawinggame;

import java.util.ArrayList;

import lx.interaction.dollar.Point;

abstract public class Control_Main
{
	protected Controller control;
	protected boolean isGroup;
	protected boolean onPlayersTeam;
	protected ArrayList<Control_Main> enemyControllers;
	public Control_Main(Controller controlSet, boolean isOnPlayersTeam)
	{
		control = controlSet;
		onPlayersTeam = isOnPlayersTeam;
		if(onPlayersTeam)
		{
			enemyControllers = control.spriteController.enemyControllers;
		} else
		{
			enemyControllers = control.spriteController.allyControllers;
		}
	}
	abstract protected void frameCall();
	abstract protected void removeHuman(EnemyShell target);
	abstract protected void setDestination(Point p);
	abstract protected void cancelMove();
}