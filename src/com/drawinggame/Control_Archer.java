package com.drawinggame;
public final class Control_Archer extends Control_Induvidual
{
	protected Enemy_Archer archer;
	public Control_Archer(Controller control, Enemy_Archer humanSet, boolean onPlayersTeam)
	{
		super(control, humanSet, onPlayersTeam);
		archer = humanSet;
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		Control_AI.archerFrame(archer, retreating, enemiesAround());
	}
	@Override
	protected void archerDoneFiring(Enemy_Archer archer)
	{
		Control_AI.archerDoneFiring(archer, enemiesAround() && !archer.hasDestination);
	}
}