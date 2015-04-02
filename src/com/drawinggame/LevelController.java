
/** Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
 * @param DifficultyLevel Chosen difficulty setting which dictates enemy reaction time and DifficultyLevelMultiplier
 * @param DifficultyLevelMultiplier Function of DifficultyLevel which changes enemy health, mana, speed
 * @param EnemyType Mage type of enemy
 * @param PlayerType Mage type of player
 * @param LevelNum Level chosen to fight on
 * @param player Player object that has health etc and generates movement handler
 * @param enemy Enemy object with health etc and ai
 * @param enemies Array of all enemies currently on screen excluding main mage enemy
 * @param proj_Trackers Array of all enemy or player proj_Trackers
 * @param proj_Trackers Array of all enemy or player Proj_Tracker explosions
 * @param spGraphicEnemy Handles the changing of main enemy's sp
 * @param spGraphicPlayer Handles the changing of player's sp
 * @param oRectX1 Array of all walls left x value
 * @param oRectX2 Array of all walls right x value
 * @param oRectY1 Array of all walls top y value
 * @param oRectY2 Array of all walls bottom x value
 * @param oCircX Array of all pillars middle x value
 * @param oCircY Array of all pillars middle y value
 * @param oCircRadius Array of all pillars radius
 * @param currentCircle Current index of oCircX to write to
 * @param currentRectangle Current index of oRectX1 to write to
 * @param teleportSpots Array of levels four teleport spots x and y for enemy mage
 * @param game Game object holding control.imageLibrary
 * @param context Main activity context for returns
 * @param aoeRect Rectangle to draw sized bitmaps
 * @param mHandler Timer for frameCaller
 * @param handleMovement Handles players movement attacks etc
 * @param screenMinX Start of game on screen horizontally
 * @param screenMinY Start of game on screen vertically
 * @param screenDimensionMultiplier
 * @param frameCaller Calls objects and controllers frameCalls
 */
package com.drawinggame;
import java.util.ArrayList;
import java.util.List;
public final class LevelController
{
	private Controller control;
	protected int levelNum = -1;
	protected int levelWidth = 800;
	protected int levelHeight = 800;
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public LevelController(Controller Control)
	{
		control = Control;
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void loadLevel(int toLoad)
	{
		endFightSection();
		levelNum = toLoad;
		WallController w = control.wallController;
		switch(levelNum)
		{
		case 1:
			//LEVEL
			levelWidth = 1400; // height of level
			levelHeight = 1400; // width of level
			
			if(control.graphicsController != null)
			{
				control.graphicsController.playScreenSize = 1;
			}
			//WALLS
			w.makeWall_Circle(771, 90, 18, true);
			w.makeWall_Circle(873, 280, 18, true);
			w.makeWall_Circle(1332, 134, 18, true);
			w.makeWall_Circle(511, 64, 18, true);
			w.makeWall_Circle(493, 152, 18, true);
			w.makeWall_Circle(419, 90, 18, true);
			w.makeWall_Circle(401, 64, 18, true);
			w.makeWall_Circle(327, 142, 18, true);
			w.makeWall_Circle(244, 36, 18, true);
			w.makeWall_Circle(459, 314, 18, true);
			w.makeWall_Circle(425, 258, 18, true);
			w.makeWall_Circle(327, 270, 18, true);
			w.makeWall_Circle(234, 322, 18, true);
			w.makeWall_Circle(180, 178, 18, true);
			w.makeWall_Circle(96, 90, 18, true);
			w.makeWall_Circle(104, 288, 18, true);
			w.makeWall_Circle(429, 439, 18, true);
			w.makeWall_Circle(280, 417, 18, true);
			w.makeWall_Circle(198, 503, 18, true);
			w.makeWall_Circle(327, 503, 18, true);
			w.makeWall_Circle(327, 669, 18, true);
			w.makeWall_Circle(935, 755, 18, true);
			w.makeWall_Circle(384, 955, 18, true);
			w.makeWall_Circle(180, 857, 18, true);
			w.makeWall_Circle(1496, 773, 18, true);
			w.makeWall_Circle(234, 1430, 18, true);
			w.makeWall_Circle(2211, 1514, 18, true);
			w.makeWall_Circle(2251, 1811, 18, true);
			w.makeWall_Circle(2688, 1719, 18, true);
			w.makeWall_Circle(1386, 2448, 18, true);
			w.makeWall_Circle(2003, 2187, 18, true);
			w.makeWall_Circle(1759, 2748, 18, true);
			w.makeWall_Circle(2087, 2456, 18, true);
			w.makeWall_Circle(2371, 2315, 18, true);
			w.makeWall_Circle(2560, 2205, 18, true);
			w.makeWall_Circle(2670, 2155, 18, true);
			w.makeWall_Circle(2234, 2704, 18, true);
			w.makeWall_Circle(2307, 2686, 18, true);
			w.makeWall_Circle(2412, 2576, 18, true);
			w.makeWall_Circle(2460, 2508, 18, true);
			w.makeWall_Circle(2588, 2456, 18, true);
			w.makeWall_Circle(2688, 2400, 18, true);
			w.makeWall_Circle(2605, 2558, 18, true);
			w.makeWall_Circle(2720, 2568, 18, true);
			w.makeWall_Circle(2728, 2632, 18, true);
			w.makeWall_Circle(2429, 2756, 18, true);
			w.makeWall_Circle(2477, 2721, 18, true);
			w.makeWall_Circle(2623, 2721, 18, true);
			w.makeWall_Circle(2524, 2847, 18, true);
			w.makeWall_Circle(2688, 2830, 18, true);
			break;
		case 3:
			//LEVEL
			levelWidth = 350; // height of level
			levelHeight = 250; // width of level
			control.graphicsController.playScreenSize = 250;
			//WALLS
			w.makeWall_Circle(126, 217, 39, false);
			w.makeWall_Rectangle(126, 171, 71, 110, true);
			break;
		}
		w.makePaths();
		makeEnemies(levelNum);
		control.imageLibrary.loadLevel(toLoad, levelWidth, levelHeight);
	}
	protected void makeEnemies(int toLoad)
	{
		SpriteController s = control.spriteController;
		switch(toLoad)
		{
		case 1:
			s.makeEnemy(0, 504, 677, -120, false);
			s.makeEnemy(0, 607, 623, -135, false);
			s.makeEnemy(2, 623, 671, -165, false);
			break;
		}
	}
	/**
	 * ends a fight section with no saved enemies
	 */
	private void endFightSection()
	{
		control.spriteController.clearObjectArrays();
		control.wallController.clearWallArrays();
	}
}