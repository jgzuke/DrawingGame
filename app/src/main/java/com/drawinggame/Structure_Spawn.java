/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.drawinggame;

public class Structure_Spawn extends Structure
{
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	int childType;
	public Structure_Spawn(Controller creator, double X, double Y, int ChildType, boolean isOnPlayersTeam)
	{
		super(X, Y, 25, 25, 0, creator.textureLibrary.structure_Spawn, isOnPlayersTeam);
		control = creator;
		hp = 6000;
		hpMax = hp;
		worth = 17;
		childType = ChildType;
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	protected void frameCall()
	{
		super.frameCall();
		if(timer == 100)
		{
			timer = 0;
			control.spriteController.makeEnemy(childType, (int)x, (int)y, onPlayersTeam);
			control.spriteController.createProj_TrackerAOE(x, y, 140, false, onPlayersTeam);
			control.soundController.playEffect("burst");
		}
	}
}