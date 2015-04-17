
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lx.interaction.dollar.Point;

import com.spritelib.Sprite;
import com.spritelib.SpriteDrawer;
public final class SelectionSpriteController extends SpriteDrawer
{
	protected Controller control;
	protected ArrayList<Enemy_Archer> archers = new ArrayList<Enemy_Archer>();
	protected ArrayList<Enemy_Mage> mages = new ArrayList<Enemy_Mage>();
	protected ArrayList<Enemy_Sheild> sheilds = new ArrayList<Enemy_Sheild>();
	protected ArrayList<Enemy> allies = new ArrayList<Enemy>();
	private boolean hasChangedMembers = false;
	protected int layoutType = 1;
	protected boolean organizing = false;
	private static double spacing = 40;
	private static double spacingSlanted = Math.sqrt(2)*spacing/2;
	private Context context;
	public SelectionSpriteController(Context contextSet, Controller controlSet)
	{
		super();
		control = controlSet;
		context = contextSet;
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
	protected void makeEnemy(int type)
	{
		if(allies.size() > 28)
		{
			Toast.makeText(context, "Too many in this group", Toast.LENGTH_SHORT).show();
			return;
		}
		Enemy newEnemy = null;
		switch(type)
		{
		case 0:
			newEnemy = new Enemy_Sheild(control, 0, 0, true, 3);
			sheilds.add((Enemy_Sheild) newEnemy);
			break;
		case 1:
			newEnemy = new Enemy_Archer(control, 0, 0, true, 4);
			archers.add((Enemy_Archer) newEnemy);
			break;
		case 2:
			newEnemy = new Enemy_Mage(control, 0, 0, true, 5);
			mages.add((Enemy_Mage) newEnemy);
			break;
		}
		newEnemy.x = 50;
		newEnemy.y = 50;
		allies.add(newEnemy);
		allies = new ArrayList<Enemy>();
		allies.addAll(mages);
		allies.addAll(archers);
		allies.addAll(sheilds);
		formUp();
	}
	/**
	 * calls all sprites frame methods
	 */
	protected void frameCall()
	{
		if(allies.size()==0) return;
		if(organizing)
		{
			boolean doneOrganizing = true;
			for(int i = 0; i < allies.size(); i ++)
			{
				if(allies.get(i).hasDestination)
				{
					doneOrganizing = false;
				}
			}
			if(doneOrganizing)
			{
				turnAfterOrganize();
				organizing = false;
			}
		}
	}
	
	/**
	 * forms this group around given x, y, rotation
	 * @param rotation
	 * @param newX
	 * @param newY
	 */
	protected void formUp()
	{
		organizing = true;
		if(layoutType == 0)
		{
			setGroupLayoutAttack();
		} else if(layoutType == 1)
		{
			setGroupLayoutNormal();
		} else if(layoutType == 2)
		{
			setGroupLayoutDefend();
		}
	}
	private void setGroupLayoutNormal()
	{
		double bestScore = Double.MAX_VALUE;
		int bestRows = 0;
		int archerRows = 0, mageRows = 0, sheildRows = 0;
		int archersPerRow = 0, magesPerRow = 0, sheildsPerRow = 0;
		for(double i = 1; i <= allies.size(); i++)
		{
			double rows = Math.ceil(((double)archers.size())/i) + Math.ceil(((double)mages.size())/i) + Math.ceil(((double)sheilds.size())/i);
			double score = 0.5*i + rows;
			if(score < bestScore)
			{
				bestRows = (int)rows;
				bestScore = score;
				archerRows = (int) Math.ceil(((double)archers.size())/i);
				mageRows = (int) Math.ceil(((double)mages.size())/i);
				sheildRows = (int) Math.ceil(((double)sheilds.size())/i);
			}					//i is best number of people in a row
		}
		archersPerRow = (int) Math.ceil(((double)archers.size())/archerRows);
		magesPerRow = (int) Math.ceil(((double)mages.size())/mageRows);
		sheildsPerRow = (int) Math.ceil(((double)sheilds.size())/sheildRows);
		ArrayList<Point> sheildPositions = new ArrayList<Point>();
		ArrayList<Point> archerPositions = new ArrayList<Point>();
		ArrayList<Point> magePositions = new ArrayList<Point>();
		double pX = (spacing/2) * (bestRows-1);
		double pY;
		for(int i = 0; i < sheildRows; i++)
		{
			pY = -(spacing/2) * (sheildsPerRow-1);
			if(i == sheildRows-1) pY += (spacing/2) * (sheildsPerRow*sheildRows - sheilds.size());
			for(int j = 0; j < sheildsPerRow; j++)
			{
				sheildPositions.add(new Point(pX, pY));
				pY += spacing;
			}
			pX -= spacing;
		}
		for(int i = 0; i < archerRows; i++)
		{
			pY = -(spacing/2) * (archersPerRow-1);
			if(i == archerRows-1) pY += (spacing/2) * (archersPerRow*archerRows - archers.size());
			for(int j = 0; j < archersPerRow; j++)
			{
				archerPositions.add(new Point(pX, pY));
				pY += spacing;
			}
			pX -= spacing;
		}
		for(int i = 0; i < mageRows; i++)
		{
			pY = -(spacing/2) * (magesPerRow-1);
			if(i == mageRows-1) pY += (spacing/2) * (magesPerRow*mageRows - mages.size());
			for(int j = 0; j < magesPerRow; j++)
			{
				magePositions.add(new Point(pX, pY));
				pY += spacing;
			}
			pX -= spacing;
		}
		Point average = getAveragePoint(sheildPositions, archerPositions, magePositions);
		startOrganizing(sheildPositions, archerPositions, magePositions, -average.X, -average.Y);
	}
	protected void startOrganizing(List<Point> positions, double addX, double addY)
	{
		for(int i = 0; i < allies.size(); i++)
		{
			allies.get(i).hasDestination = true;
			allies.get(i).destinationRotation = 0;
			allies.get(i).speedCur = 5;			//faster to get in line
			allies.get(i).destinationX = (int)(positions.get(i).X+addX);
			allies.get(i).destinationY = (int)(positions.get(i).Y+addY);
		}
	}
	protected void startOrganizing(List<Point> sheildPositions,List<Point> archerPositions, List<Point> magePositions, double addX, double addY)
	{
		Log.e("myid", "testqwet");
		for(int i = 0; i < allies.size(); i++)
		{
			allies.get(i).hasDestination = true;
			allies.get(i).destinationRotation = 0;
			allies.get(i).speedCur = 5;			//faster to get in line
		}
		for(int i = 0; i < sheilds.size(); i++)
		{
			sheilds.get(i).destinationX = (int)(sheildPositions.get(i).X+addX);
			sheilds.get(i).destinationY = (int)(sheildPositions.get(i).Y+addY);
		}
		for(int i = 0; i < archers.size(); i++)
		{
			archers.get(i).destinationX = (int)(archerPositions.get(i).X+addX);
			archers.get(i).destinationY = (int)(archerPositions.get(i).Y+addY);
		}
		for(int i = 0; i < mages.size(); i++)
		{
			mages.get(i).destinationX = (int)(magePositions.get(i).X+addX);
			mages.get(i).destinationY = (int)(magePositions.get(i).Y+addY);
		}
	}
	protected void turnAfterOrganize()
	{
		for(int i = 0; i < allies.size(); i++)
		{
			allies.get(i).speedCur = 3.5;
		}
	}
	protected Point getAveragePoint(ArrayList<Point> sheildPositions, ArrayList<Point> archerPositions, ArrayList<Point> magePositions)
	{
		double averageX = 0;
		double averageY = 0;
		for(int i = 0; i < magePositions.size(); i++)
		{
			averageX += magePositions.get(i).X;
			averageY += magePositions.get(i).Y;
		}
		for(int i = 0; i < archerPositions.size(); i++)
		{
			averageX += archerPositions.get(i).X;
			averageY += archerPositions.get(i).Y;
		}
		for(int i = 0; i < sheildPositions.size(); i++)
		{
			averageX += sheildPositions.get(i).X;
			averageY += sheildPositions.get(i).Y;
		}
		averageX /= (magePositions.size()+archerPositions.size()+sheildPositions.size());
		averageY /= (magePositions.size()+archerPositions.size()+sheildPositions.size());
		return new Point(averageX, averageY);
	}
	protected Point getAveragePoint(ArrayList<Point> positions)
	{
		double averageX = 0;
		double averageY = 0;
		for(int i = 0; i < positions.size(); i++)
		{
			averageX += positions.get(i).X;
			averageY += positions.get(i).Y;
		}
		averageX /= positions.size();
		averageY /= positions.size();
		return new Point(averageX, averageY);
	}
	private void setGroupLayoutDefend()
	{
		ArrayList<Point> locations = new ArrayList<Point>();
		int layer = 0;
		boolean moreUnits = true;
		int alliesLeft = allies.size();
		while(moreUnits)
		{
			double pX = 0; // how far out to start
			double pY = spacing * layer;
			int skip = (layer*4 + 1) - alliesLeft; // layersize - allies left
			for(int i = 0; i < layer*2 + 1; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					if(locations.size() == allies.size())
					{
						Point average = getAveragePoint(locations);
						startOrganizing(locations, -average.X, -average.Y);
						return;
					}
					if(i!=layer*2 || j != 0)
					{
						skip --;
						if(skip < 0)
						{
							alliesLeft--;
							if(j==0)
							{
								locations.add(new Point(pX, pY));
							} else
							{
								locations.add(new Point(pX, pY));
							}
						}
					}
				}
				if(i < layer)
				{
					pX += spacing;
				} else
				{
					pY -= spacing;
				}
			}
			layer ++;
		}
	}
	private void setGroupLayoutAttack()
	{
		ArrayList<Point> locations = new ArrayList<Point>();
		int layer = 0;
		boolean moreUnits = true;
		int alliesLeft = allies.size();
		while(moreUnits)
		{
			double pX = 0; // how far out to start
			double pY = spacingSlanted * layer * 2;
			int skip = (layer*4 + 1) - alliesLeft; // layersize - allies left
			for(int i = 0; i < layer*2 + 1; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					if(locations.size() == allies.size())
					{
						Point average = getAveragePoint(locations);
						startOrganizing(locations, -average.X, -average.Y);
						return;
					}
					if(i!=layer*2 || j != 0)
					{
						skip --;
						if(skip < 0)
						{
							alliesLeft--;
							if(j==0)
							{
								locations.add(new Point(pX, pY));
							} else
							{
								locations.add(new Point(pX, pY));
							}
						}
					}
				}
				pX += spacingSlanted;
				pY -= spacingSlanted;
			}
			layer ++;
		}
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
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null)
			{
				if(enemyInsideCircle(points, allies.get(i).x, allies.get(i).y))
				{
					allies.get(i).selected = true;
				}
			}
		}
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
	protected void selectEnemy(double x, double y)
	{
		deselectEnemies();
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null)
			{
				if(Math.pow(x-allies.get(i).x, 2) + Math.pow(y-allies.get(i).y, 2) < 600)
				{
					allies.get(i).selected = true;
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
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null)
			{
				allies.get(i).selected = false;
			}
		}
	}
}