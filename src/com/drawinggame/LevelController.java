
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
		if(levelNum != -1)
		{
			endFightSection();
		}
		levelNum = toLoad;
		WallController w = control.wallController;
		control.imageLibrary.recycleEnemies();
		switch(levelNum)
		{
		case 1:
			//LEVEL
			levelWidth = 800; // height of level
			levelHeight = 800; // width of level
			
			if(control.graphicsController != null)
			{
				control.graphicsController.playScreenSize = 450;
			}
			//ENEMIES
			control.imageLibrary.loadEnemy(55, "goblin_swordsman", 110, 70, 0); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 3);
			control.imageLibrary.loadEnemy(31, "goblin_mage", 30, 34, 2);
			control.imageLibrary.loadEnemy(31, "goblin_cleric", 30, 34, 5);
			//WALLS
			w.makeWall_Rectangle(-85, -182, 111, 528, true);
			w.makeWall_Rectangle(-85, 455, 111, 528, true);
			w.makeWall_Rectangle(-142, 241, 192, 105, true);
			w.makeWall_Rectangle(-142, 456, 192, 105, true);
			w.makeWall_Rectangle(343, 762, 307, 338, true);
			w.makeWall_Rectangle(383, 717, 66, 168, true);
			w.makeWall_Rectangle(515, 731, 87, 158, true);
			w.makeWall_Rectangle(530, 696, 13, 70, true);
			w.makeWall_Rectangle(575, 696, 13, 70, true);
			w.makeWall_Rectangle(668, 489, 32, 12, false);
			w.makeWall_Rectangle(671, 374, 22, 69, true);
			w.makeWall_Rectangle(696, 495, 10, 47, false);
			w.makeWall_Rectangle(664, 535, 37, 11, false);
			w.makeWall_Rectangle(696, 592, 10, 47, false);
			w.makeWall_Rectangle(741, 594, 10, 47, false);
			w.makeWall_Rectangle(687, 634, 58, 11, false);
			w.makeWall_Rectangle(701, 588, 45, 12, false);
			w.makeWall_Rectangle(671, 374, 22, 69, false);
			w.makeWall_Rectangle(660, 409, 22, 94, false);
			w.makeWall_Rectangle(642, 483, 22, 125, false);
			w.makeWall_Rectangle(663, 535, 22, 139, false);
			w.makeWall_Rectangle(649, 602, 22, 48, false);
			w.makeWall_Rectangle(685, 616, 20, 181, false);
			w.makeWall_Rectangle(663, 708, 105, 181, false);
			w.makeWall_Circle(396, 74, 8, true);
			w.makeWall_Circle(478, 45, 8, true);
			w.makeWall_Circle(607, 81, 8, true);
			w.makeWall_Circle(719, 45, 8, true);
			w.makeWall_Circle(698, 138, 8, true);
			w.makeWall_Circle(605, 204, 8, true);
			w.makeWall_Circle(772, 212, 8, true);
			w.makeWall_Circle(737, 247, 8, true);

			break;
		case 2:
			levelWidth = 800; // height of level
			levelHeight = 800; // width of level
			if(control.graphicsController != null)
			{
				control.graphicsController.playScreenSize = 650;
			}
			w.makeWall_Rectangle(-171, -214, 196, 483, true);
			w.makeWall_Rectangle(-190, 236, 241, 109, true);
			w.makeWall_Rectangle(-190, 450, 241, 109, true);
			w.makeWall_Rectangle(-171, 523, 196, 483, true);
			w.makeWall_Rectangle(-237, -104, 535, 130, true);
			w.makeWall_Rectangle(236, -150, 109, 200, true);
			w.makeWall_Rectangle(506, -104, 535, 130, true);
			w.makeWall_Rectangle(453, -150, 109, 200, true);
			w.makeWall_Rectangle(772, -214, 196, 483, true);
			w.makeWall_Rectangle(747, 236, 241, 109, true);
			w.makeWall_Rectangle(747, 450, 241, 109, true);
			w.makeWall_Rectangle(772, 523, 196, 483, true);
			w.makeWall_Rectangle(-237, 771, 535, 130, true);
			w.makeWall_Rectangle(236, 747, 109, 200, true);
			w.makeWall_Rectangle(506, 771, 535, 130, true);
			w.makeWall_Rectangle(453, 747, 109, 200, true);
			w.makeWall_Rectangle(-94, -276, 196, 483, true);
			w.makeWall_Rectangle(578, 17, 279, 86, true);
			w.makeWall_Rectangle(659, 148, 196, 25, true);
			w.makeWall_Rectangle(719, 681, 328, 179, true);
			w.makeWall_Rectangle(-64, 627, 194, 54, true);
			w.makeWall_Rectangle(633, 681, 20, 179, true);
			w.makeWall_Rectangle(-64, 731, 194, 210, true);
			w.makeWall_Rectangle(158, -104, 27, 223, true);
			break;
		case 4:
			levelWidth = 930; // height of level
			levelHeight = 780; // width of level
			control.graphicsController.playScreenSize = 300;
			control.imageLibrary.loadEnemy(55, "goblin_swordsman", 110, 70, 0); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 1);
			control.imageLibrary.loadEnemy(31, "goblin_mage", 30, 34, 2);
			control.imageLibrary.loadEnemy(31, "goblin_cleric", 30, 34, 5);
			w.makeWall_Ring(319, 507, 253, 273, true); // outer circle
			w.makeWall_Pass(-6, 398, 80, 211, true, 0);
			w.makeWall_Pass(350, 164, 34, 111, true, 0);
			w.makeWall_Pass(530, 497, 159, 31, true, 0);
			
			w.makeWall_Ring(319, 507, 176, 196, true); // inner circle
			w.makeWall_Pass(294, 654, 32, 67, true, 1);
			
			w.makeWall_Ring(343, 137, 78, 98, true);   // top left
			w.makeWall_Pass(350, 164, 34, 111, true, 2);
			w.makeWall_Pass(382, 106, 204, 31, true, 2);
			
			w.makeWall_Ring(709, 218, 176, 196, true); // top right
			w.makeWall_Pass(382, 106, 204, 31, true, 3);
			w.makeWall_Pass(657, 338, 32, 174, true, 3);
			
			
			w.makeWall_Circle(28, 413, 81, true);
			w.makeWall_Rectangle(436, 137, 92, 24, true);
			w.makeWall_Rectangle(327, 224, 23, 37, true);
			w.makeWall_Rectangle(429, 82, 117, 24, true);
			w.makeWall_Rectangle(386, 216, 53, 51, true);
			w.makeWall_Rectangle(560, 473, 87, 24, true);
			w.makeWall_Rectangle(558, 528, 145, 24, true);
			w.makeWall_Rectangle(635, 397, 23, 84, true);
			w.makeWall_Rectangle(691, 406, 23, 134, true);
			w.makeWall_Rectangle(-8, 542, 143, 27, true);
			w.makeWall_Rectangle(265, 682, 29, 133, true);
			w.makeWall_Rectangle(326, 686, 29, 30, true);
			
			
			break;
		case 3:
			//LEVEL
			levelWidth = 350; // height of level
			levelHeight = 250; // width of level
			control.graphicsController.playScreenSize = 250;
			//ENEMIES
			control.imageLibrary.loadEnemy(65, "goblin_rogue", 60, 40, 4); // length, name,width, height, index
			control.imageLibrary.loadEnemy(49, "goblin_archer", 80, 50, 3);
			//WALLS
			w.makeWall_Circle(126, 217, 39, false);
			w.makeWall_Circle(60, 51, 39, false);
			w.makeWall_Rectangle(-50, -6, 74, 250, true);
			w.makeWall_Rectangle(-8, 2, 70, 84, true);
			w.makeWall_Rectangle(45, -41, 103, 84, true);
			w.makeWall_Rectangle(129, -13, 68, 103, true);
			w.makeWall_Rectangle(142, -49, 68, 103, true);
			w.makeWall_Rectangle(264, -47, 68, 103, true);
			w.makeWall_Rectangle(282, -9, 68, 103, true);
			w.makeWall_Rectangle(325, 37, 68, 141, true);
			w.makeWall_Rectangle(282, 161, 68, 103, true);
			w.makeWall_Rectangle(181, 193, 129, 103, true);
			w.makeWall_Rectangle(-67, 169, 129, 103, true);
			w.makeWall_Rectangle(37, 214, 129, 103, true);
			w.makeWall_Rectangle(126, 171, 71, 110, true);
			break;
		case 5:
			levelWidth = 276;
			levelHeight = 303;
			control.graphicsController.playScreenSize = 276;
			w.makeWall_Rectangle(-49, -6, 74, 331, true);
			w.makeWall_Rectangle(-43, -66, 398, 92, true);
			w.makeWall_Rectangle(252, 0, 74, 331, true);
			w.makeWall_Rectangle(-54, 278, 244, 86, true);
			w.makeWall_Rectangle(128, 150, 41, 141, true);
			w.makeWall_Rectangle(236, 150, 31, 141, true);
			break;
		case 6:
			levelWidth = 304;
			levelHeight = 248;
			control.graphicsController.playScreenSize = 248;
			w.makeWall_Rectangle(-49, 13, 74, 143, true);
			w.makeWall_Rectangle(-28, -66, 398, 92, true);
			w.makeWall_Rectangle(279, -11, 74, 205, true);
			w.makeWall_Rectangle(-59, 117, 187, 12, true);
			w.makeWall_Rectangle(102, 223, 194, 37, true);
			w.makeWall_Rectangle(265, 181, 31, 141, true);
			w.makeWall_Rectangle(-49, -22, 177, 71, true);
			w.makeWall_Rectangle(-59, 166, 187, 33, true);
			w.makeWall_Rectangle(74, 181, 70, 67, true);
			break;
		case 7:
			levelWidth = 174;
			levelHeight = 208;
			control.graphicsController.playScreenSize = 174;
			w.makeWall_Rectangle(-49, -33, 74, 331, true);
			w.makeWall_Rectangle(-120, -66, 398, 92, true);
			w.makeWall_Rectangle(149, 164, 74, 94, true);
			w.makeWall_Rectangle(-23, 183, 244, 86, true);
			w.makeWall_Rectangle(149, -20, 104, 131, true);
			break;
		case 8:
			levelWidth = 162;
			levelHeight = 125;
			control.graphicsController.playScreenSize = 125;
			w.makeWall_Rectangle(-49, -66, 74, 331, true);
			w.makeWall_Rectangle(70, -66, 165, 92, true);
			w.makeWall_Rectangle(138, -29, 74, 331, true);
			w.makeWall_Rectangle(-35, 100, 244, 86, true);
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
			s.makeEnemy(0, 504, 677, -120, true);
			s.makeEnemy(3, 678, 518, 180, true);
			s.makeEnemy(5, 730, 551, -165, true);
			s.makeEnemy(0, 607, 623, -135, true);
			s.makeEnemy(3, 724, 617, 180, true);
			s.makeEnemy(2, 623, 671, -165, true);
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