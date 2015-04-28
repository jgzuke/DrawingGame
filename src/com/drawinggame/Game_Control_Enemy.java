package com.drawinggame;

import lx.interaction.dollar.Point;

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
		if(mana > spriteControl.manaPricesEnemy[3] && timer < 0)
		{
			spriteControl.makeEnemy(3, control.levelController.levelWidth/2, control.levelController.levelHeight/2, false);
			timer = 30;
		}
		Control_Main e;
		for(int i = 0; i < allyControllers.size(); i++)
		{
			e = allyControllers.get(i);
			if(e.doingNothing)
			{
				if(e.isGroup)
				{
					groupFrame((Control_Group) e);
				} else
				{
					singleFrame((Control_Induvidual) e);
				}
			}
		}
	}
	private void groupFrame(Control_Group g)
	{
		Control_Main e;
		Point p = g.groupLocation;
		for(int i = 0; i < allyControllers.size(); i++)
		{
			e = allyControllers.get(i);
			Point p2 = e.groupLocation;
			if(e.doingNothing && p.X != p2.X)
			{
				if(g.getSize() + e.getSize() < 29 && Math.sqrt(Math.pow(p.X-p2.X, 2)+Math.pow(p.Y-p2.Y, 2)) < 100 + e.groupRadius + g.groupRadius)
				{
					control.spriteController.selectGroup(e, g, false);
				}
			}
		}
	}
	private void singleFrame(Control_Induvidual g)
	{
		
	}
}