package com.drawinggame;
public final class Control_Mage extends Control_Induvidual
{
	protected Enemy_Mage mage;
	public Control_Mage(Controller control, Enemy_Mage humanSet, boolean onPlayersTeam)
	{
		super(control, humanSet, onPlayersTeam);
		mage = humanSet;
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		doingNothing = !Control_AI.mageFrame(mage, retreating, enemiesAround());
	}
}