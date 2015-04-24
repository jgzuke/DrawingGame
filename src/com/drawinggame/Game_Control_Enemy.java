package com.drawinggame;

public final class Game_Control_Enemy extends Game_Control
{
	private int timer = 0;
	public Game_Control_Enemy(Controller controlSet)
	{
		super(controlSet);
	}
	protected void frameCall()
	{
		timer --;
		super.frameCall();
		if(mana > spriteControl.manaPrices[3] && timer < 0)
		{
			spriteControl.makeEnemy(3, 1100, 1100, false);
			timer = 30;
		}
		Control_Main e;
		for(int i = 0; i < spriteControl.enemyControllers.size(); i++)
		{
			e = enemyControllers.get(i);
		}
	}
}