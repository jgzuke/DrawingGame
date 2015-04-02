/**
 * AI and variables for rogues
 */
package com.drawinggame;


public final class Enemy_Sheild extends Enemy
{
	public Enemy_Sheild(Controller creator, double X, double Y, double R, int HP, int ImageIndex, boolean isOnPayersTeam)
	{
		super(creator, X, Y, R, HP, ImageIndex, isOnPayersTeam);
		speedCur = 3.8;
		frame=0;
		baseHp(HP);
		worth = 5;
		if(control.getRandomInt(3) == 0)
		{
			runRandom();
		}
		rotation = control.getRandomInt(360);
		rads = rotation/r2d;
		frames = makeFrames();
	}
	@Override
	protected void frameCall()
	{
		super.frameCall();
		if(action.equals("Melee"))
		{
			attacking();
		} else if(action.equals("Sheild"))
		{
			blocking();
		}
	}
	private int[][] makeFrames()
	{
		//				 move	  roll	  stun	 melee		sheild	  hide	 shoot
		int[] e = {0, 0};
		int[][] temp = {{0, 19}, e, e, {20, 45, 27, 36}, {46, 55}, e, e};
		return temp;
	}
	protected void attacking()
	{
		for(int i = 2; i < frames[3].length; i++)
		{
			if(frame==frames[3][i])
			{
				meleeAttack(200, 25, 20);
			}
		}
	}
	protected void blocking()
	{
		turnToward();
		if(frame<55 && frame> 47)
		{
			checkDanger();
			if(inDanger>0) frame = 50;
		}
	}
	/**
	 * select this enemy, take it out of a group if need be
	 */
	@Override
	protected void selectSingle()
	{
		if(!myController.isGroup) return;
		Control_Sheild newControl = new Control_Sheild(control, this);
		control.spriteController.humanControllers.add(newControl);
	}
}