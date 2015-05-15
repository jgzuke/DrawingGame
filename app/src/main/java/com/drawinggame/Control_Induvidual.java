package com.drawinggame;

import lx.interaction.dollar.Point;

public final class Control_Induvidual extends Control_Main
{
	protected EnemyShell human;
	protected int type; //0: sheild 1: archer 2: mage
	public Control_Induvidual(Controller controlSet, EnemyShell humanSet, boolean onPlayersTeam, int typeSet)
	{
		super(controlSet, onPlayersTeam);
		human = humanSet;
		human.setController(this);
		isGroup = false;
		groupLocation = new Point(human.x, human.y);
		groupRadius = 20;
		type = typeSet;
	}
	protected void frameCall()
	{
		groupLocation.X = human.x;
		groupLocation.Y = human.y;
		switch(type)
		{
		case 0:
			doingNothing = !Control_AI.sheildFrame((Enemy_Sheild)human, retreating, enemiesAround());
			break;
		case 1:
			doingNothing = !Control_AI.archerFrame((Enemy_Archer)human, retreating, enemiesAround());
			break;
		case 2:
			doingNothing = !Control_AI.mageFrame((Enemy_Mage)human, retreating, enemiesAround());
			break;
		}
	}
	@Override
	protected void archerDoneFiring(Enemy_Archer archer)
	{
		if(type==1)Control_AI.archerDoneFiring((Enemy_Archer)human, enemiesAround() && !archer.hasDestination);
	}
	@Override
	protected void setDestination(Point p)
	{
		if(enemiesAround()) retreating = true;
		human.setDestination((int)p.X, (int)p.Y);
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
	protected int getSize()
	{
		return 1;
	}
}