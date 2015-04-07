package com.drawinggame;

public final class Game_Control_Enemy extends Game_Control
{
	public Game_Control_Enemy(Controller controlSet)
	{
		super(controlSet);
	}
	protected void frameCall()
	{
		super.frameCall();
		if(mana > spriteControl.manaPrices[3])
		{
			spriteControl.makeEnemy(3, 1100, 1100, false);
		}
	}
}