package com.drawinggame;
public final class Control_Sheild extends Control_Induvidual
{
	protected Enemy_Sheild sheild;
	public Control_Sheild(Controller control, Enemy_Sheild humanSet, boolean onPlayersTeam)
	{
		super(control, humanSet, onPlayersTeam);
		sheild = humanSet;
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		doingNothing = !Control_AI.sheildFrame(sheild, retreating, enemiesAround());
	}
}