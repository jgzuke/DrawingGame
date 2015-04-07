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
		if(mage.action.equals("Roll"))
		{
		} else if(mage.action.equals("Move"))
		{
		} else
		{
			if(mage.hasDestination)
			{
				mage.runTowardsDestination();
			}
		}
	}
}