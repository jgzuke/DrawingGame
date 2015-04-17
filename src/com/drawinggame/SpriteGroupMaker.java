
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
 * @param game Game object holding imageLibrary
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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

import lx.interaction.dollar.Point;

import com.spritelib.Sprite;
import com.spritelib.SpriteDrawer;
public final class SpriteGroupMaker extends SpriteDrawer
{
	protected Controller control;
	protected Control_Main groupController;
	protected ArrayList<Enemy> allies = new ArrayList<Enemy>();
	
	protected int [] manaPrices = {50, 30, 70};
	/**
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public SpriteGroupMaker(Context contextSet, Controller controlSet)
	{
		super();
		control = controlSet;
	}
	/**
	 * clears all arrays to restart game
	 */
	void clearObjectArrays()
	{
		allies.clear();
	}
	/**
	 * creates person
	 * @param type whether they are warrior, archer, mage etc
	 * @param x x to walk to
	 * @param y y to wakl to
	 * @param r rotation
	 * @param isOnPlayersTeam which team they are on
	 */
	protected void makeEnemy(int type, int x, int y, boolean isOnPlayersTeam)
	{
		if(isOnPlayersTeam)
		{
			if(playerGameControl.mana < manaPrices[type]) return;
			playerGameControl.mana -= manaPrices[type];
		} else
		{
			if(enemyGameControl.mana < manaPrices[type]) return;
			enemyGameControl.mana -= manaPrices[type];
		}
		ArrayList<Enemy> toAdd;
		ArrayList<Control_Main> toAddController;
		if(isOnPlayersTeam)
		{
			toAdd = allies;
			toAddController = allyControllers;
		} else 
		{
			toAddController = enemyControllers;
			toAdd = enemies;
		}
		switch(type)
		{
		case 0:
		case 1:
		case 2:
			makeEnemyBasic(type, x, y, isOnPlayersTeam, toAdd, toAddController);
			break;
		case 3:
		case 4:
		case 5:
		case 6:
			makeGroup(groupDetails[type-3][0], groupDetails[type-3][1], groupDetails[type-3][2], groupDetails[type-3][3], x, y, isOnPlayersTeam, toAdd, toAddController);
			break;
		}
	}
	protected Enemy makeEnemyBasic(int type, int x, int y, boolean isOnPlayersTeam, ArrayList<Enemy> toAdd, ArrayList<Control_Main> toAddController)
	{
		int imageType = 0;
		if(isOnPlayersTeam) imageType += 3;
		creationMarkers.add(new CreationMarker(x, y, control.imageLibrary.createMarkers[imageType], this));
		Enemy newEnemy = null;
		switch(type)
		{
		case 0:
			newEnemy = new Enemy_Sheild(control, x, y, isOnPlayersTeam, 0+imageType);
			toAddController.add(new Control_Sheild(control, (Enemy_Sheild) newEnemy, isOnPlayersTeam));
			toAdd.add(newEnemy);
			break;
		case 1:
			newEnemy = new Enemy_Archer(control, x, y, isOnPlayersTeam, 1+imageType);
			toAddController.add(new Control_Archer(control, (Enemy_Archer) newEnemy, isOnPlayersTeam));
			toAdd.add(newEnemy);
			break;
		case 2:
			newEnemy = new Enemy_Mage(control, x, y, isOnPlayersTeam, 2+imageType);
			toAddController.add(new Control_Mage(control, (Enemy_Mage) newEnemy, isOnPlayersTeam));
			toAdd.add(newEnemy);
			break;
		}
		return newEnemy;
	}
	protected void makeGroup(int sheilds, int archers, int mages, int formation, int x, int y, boolean isOnPlayersTeam, ArrayList<Enemy> toAdd, ArrayList<Control_Main> toAddController)
	{
		if(isOnPlayersTeam) creationMarkers.add(new CreationMarker(x, y, control.imageLibrary.createMarkers[6], this));
		else creationMarkers.add(new CreationMarker(x, y, control.imageLibrary.createMarkers[7], this));
		ArrayList<Enemy> newGroup = new ArrayList<Enemy>();
		Control_Group enemyGroup = null;
		for(int i = 0; i < mages; i++)
		{
			newGroup.add(makeEnemyBasic(2, x, y, isOnPlayersTeam, toAdd, toAddController));
		}
		for(int i = 0; i < archers; i++)
		{
			newGroup.add(makeEnemyBasic(1, x, y, isOnPlayersTeam, toAdd, toAddController));
		}
		for(int i = 0; i < sheilds; i++)
		{
			newGroup.add(makeEnemyBasic(0, x, y, isOnPlayersTeam, toAdd, toAddController));
		}
		enemyGroup = groupEnemies(newGroup, isOnPlayersTeam);
		toAddController.add(enemyGroup);
		enemyGroup.setDestination(new Point(x, y));
		enemyGroup.setLayoutType(formation);
	}
	/**
	 * calls all sprites frame methods
	 */
	protected void frameCall()
	{
		for(int i = 0; i < allies.size(); i++)
		{
			allies.get(i).frameCall();
		}
		groupController.frameCall();
	}
	/**
	 * draws all the sprites onto the canvas
	 * @param g canvas to use
	 * @param paint paint to use
	 * @param imageLibrary imageLibrary to use
	 */
	protected void drawSprites(Canvas g, Paint paint, ImageLibrary imageLibrary)
	{
		for(int i = 0; i < allies.size(); i++)
		{
			draw(allies.get(i), g, paint);
		}
	}
	@Override
	protected boolean onScreen(double x, double y, int width, int height) {return true;}
	/**
	 * selects enemy with click
	 * @param x click x
	 * @param y click y
	 * @return whether anything was selected
	 */
	protected void selectCircle(Vector<Point> points)
	{
		deselectEnemies();
		ArrayList<Enemy> group = new ArrayList<Enemy>();
		int countGroup = 0;
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null)
			{
				if(enemyInsideCircle(points, allies.get(i).x, allies.get(i).y))
				{
					countGroup ++;
					if(countGroup > 28) break;
					group.add(allies.get(i));
				}
			}
		}
		selectGroup(group);
	}
	protected boolean enemyInsideCircle(Vector<Point> p, double x, double y)
	{
		boolean lastAbove = p.get(0).Y < y;
		boolean lastToLeft = p.get(0).X < x;
		boolean above, toLeft;
		boolean hitTop = false;
		boolean hitBottom = false;
		boolean hitLeft = false;
		boolean hitRight = false;
		for(int i = 1; i < p.size(); i++)
		{
			above = p.get(i).Y < y;
			toLeft = p.get(i).X < x;
			if(toLeft!=lastToLeft)
			{
				hitTop = (above && lastAbove) || hitTop;
				hitBottom = ((!above) && (!lastAbove)) || hitBottom;
			}
			if(above!=lastAbove)
			{
				hitLeft = (toLeft && lastToLeft) || hitLeft;
				hitRight = ((!toLeft) && (!lastToLeft)) || hitRight;
			}
			lastAbove = above;
			lastToLeft = toLeft;
		}
		return hitTop&&hitBottom&&hitLeft&&hitRight;
	}
	/**
	 * selects enemy with click
	 * @param x click x
	 * @param y click y
	 * @return whether anything was selected
	 */
	protected boolean selectEnemy(double x, double y)
	{
		Enemy selectedEnemy = null;
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null)
			{
				if(Math.pow(x-allies.get(i).x, 2) + Math.pow(y-allies.get(i).y, 2) < 600)
				{
					selectedEnemy = allies.get(i);
					break;
				}
			}
		}
		if(selectedEnemy!=null)
		{
			if(selectedEnemy.myController.isGroup)
			{
				if(control.selected == null || !selectedEnemy.myController.equals(control.selected))
				{
					deselectEnemies();
					((Control_Group)selectedEnemy.myController).setSelected();
					control.gestureDetector.selectType = "group";
					control.selected = selectedEnemy.myController;
					return true;
				}
			}
			deselectEnemies();
			if(selectedEnemy.myController.isGroup)
			{
				selectedEnemy.hasDestination = false;
			}
			selectedEnemy.selected = true;
			selectedEnemy.selectSingle();
			selectedEnemy.speedCur=3.5;
			control.gestureDetector.selectType = "single";
			control.selected = selectedEnemy.myController;
			return true;
		}
		return false;
	}
	protected void selectGroup(ArrayList<Enemy> group)
	{
		if(group.size() == 0) return;
		if(group.size() == 1)
		{
			group.get(0).selected = true;
			group.get(0).selectSingle();
			control.selected = group.get(0).myController;
			control.gestureDetector.selectType = "single";
			return;
		}			// More than one selected
		// check if one group dominates the selected people
		control.gestureDetector.selectType = "group";
		
		// check if this group is made from an old one, and use its destination
		ArrayList<Control_Group> priorGroups = new ArrayList<Control_Group>();
		ArrayList<Integer> groupCounts = new ArrayList<Integer>();
		for(int i = 0; i < group.size(); i++)
		{
			if(group.get(i).myController.isGroup)
			{
				boolean alreadyHere = false;
				for(int j = 0; j < priorGroups.size(); j++)
				{
					if(priorGroups.get(j).equals(group.get(i).myController))
					{
						groupCounts.set(j, groupCounts.get(j) + 1);
						alreadyHere = true;
						break;
					}	
				}
				if(!alreadyHere)
				{
					priorGroups.add((Control_Group) group.get(i).myController);
					groupCounts.add(1);
				}
			}
		}
		
		Control_Group newGroup = new Control_Group(control, group, true);
		allyControllers.add(newGroup);
		control.selected = newGroup;
		for(int i = 0; i < group.size(); i++)
		{
			group.get(i).selected = true;
		}
		for(int i = 0; i < groupCounts.size(); i++)		// if this group is more than half of the new, use its settings
		{
			if((double)groupCounts.get(i)/(double)group.size() > 0.6)
			{
				if(priorGroups.get(i).hasDestination)
				{
					newGroup.setDestination(priorGroups.get(i).destLocation.clone());
				}
			}
		}
	}
	protected Control_Group groupEnemies(ArrayList<Enemy> group, boolean onPlayersTeam)
	{
		Control_Group newGroup = new Control_Group(control, group, onPlayersTeam);
		return newGroup;
	}
	/**
	 * deslects all enemies
	 */
	protected void deselectEnemies()
	{
		control.selected = null;
		control.gestureDetector.selectType = "none";
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null)
			{
				allies.get(i).selected = false;
			}
		}
	}
}