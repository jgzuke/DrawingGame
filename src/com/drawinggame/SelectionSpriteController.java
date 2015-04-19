
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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lx.interaction.dollar.Point;

import com.spritelib.SpriteDrawer;
public final class SelectionSpriteController extends SpriteDrawer
{
	protected Controller control;
	private int selected = 0;
	protected ArrayList<ArrayList<Enemy_Archer>> archers = new ArrayList<ArrayList<Enemy_Archer>>();
	protected ArrayList<ArrayList<Enemy_Mage>> mages = new ArrayList<ArrayList<Enemy_Mage>>();
	protected ArrayList<ArrayList<Enemy_Sheild>> sheilds = new ArrayList<ArrayList<Enemy_Sheild>>();
	protected ArrayList<ArrayList<Enemy>> allies = new ArrayList<ArrayList<Enemy>>();
	protected int [] layoutType = new int[4];
	private boolean [] organizing = {false, false, false, false};
	private static double spacing = 40;
	private static double spacingSlanted = Math.sqrt(2)*spacing/2;
	private double groupRadius = 0;
	float ratio = 0;
	private Context context;
	public SelectionSpriteController(Context contextSet, Controller controlSet)
	{
		super();
		control = controlSet;
		context = contextSet;
		for(int i = 0; i < 4; i++)
		{
			archers.add(new ArrayList<Enemy_Archer>());
			mages.add(new ArrayList<Enemy_Mage>());
			sheilds.add(new ArrayList<Enemy_Sheild>());
			allies.add(new ArrayList<Enemy>());
			selected = i;
			layoutType[i] = control.spriteController.groupDetails[i][3];
			for(int j = 0; j < control.spriteController.groupDetails[i][0]; j++) makeEnemy(0);
			for(int j = 0; j < control.spriteController.groupDetails[i][1]; j++) makeEnemy(1);
			for(int j = 0; j < control.spriteController.groupDetails[i][2]; j++) makeEnemy(2);
		}
		selected = 0;
	}
	protected void changeLayout(int newLayout)
	{
		layoutType[selected] = newLayout;
		control.spriteController.groupDetails[selected][3] = newLayout;
		formUp();
	}
	/**
	 * clears all arrays to restart game
	 */
	void clearObjectArrays()
	{
		allies.get(selected).clear();
	}
	private void changedSetting()
	{
		control.spriteController.groupDetails[selected][0] = sheilds.get(selected).size();
		control.spriteController.groupDetails[selected][1] = archers.get(selected).size();
		control.spriteController.groupDetails[selected][2] = mages.get(selected).size();
		control.spriteController.setPrices();
	}
	protected void deleteEnemies()
	{
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			if(allies.get(selected).get(i).selected)
			{
				if(allies.size() < 3)
				{
					Toast.makeText(context, "Need at least two units in a group", Toast.LENGTH_SHORT).show();
					break;
				}
				int type = allies.get(selected).get(i).humanType;
				if(type==0) sheilds.remove(allies.get(selected).get(i));
				if(type==1) archers.remove(allies.get(selected).get(i));
				if(type==2) mages.remove(allies.get(selected).get(i));
				allies.get(selected).remove(i);
				i --;
			}
		}
		changedSetting();
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
		if(allies.get(selected).size() > 14)
		{
			Toast.makeText(context, "Too many in this group", Toast.LENGTH_SHORT).show();
			return;
		}
		Enemy newEnemy = null;
		switch(type)
		{
		case 0:
			newEnemy = new Enemy_Sheild(control, 0, 0, true, 3);
			sheilds.get(selected).add((Enemy_Sheild) newEnemy);
			allies.get(selected).add(mages.get(selected).size()+archers.get(selected).size(), newEnemy);
			break;
		case 1:
			newEnemy = new Enemy_Archer(control, 0, 0, true, 4);
			archers.get(selected).add((Enemy_Archer) newEnemy);
			allies.get(selected).add(mages.get(selected).size(), newEnemy);
			break;
		case 2:
			newEnemy = new Enemy_Mage(control, 0, 0, true, 5);
			mages.get(selected).add((Enemy_Mage) newEnemy);
			allies.get(selected).add(0, newEnemy);
			break;
		}
		newEnemy.x = 400;
		newEnemy.y = 500;
		formUp();
		changedSetting();
	}
	/**
	 * calls all sprites frame methods
	 */
	protected void frameCall()
	{
		selected = control.gestureDetector.settingSelected;
		groupRadius = 75 + Math.sqrt(allies.get(selected).size())*spacing/4;
		ratio = (float)(250/groupRadius);
		if(allies.get(selected).size()==0) return;
		if(organizing[selected])
		{
			boolean doneOrganizing = true;
			for(int i = 0; i < allies.get(selected).size(); i ++)
			{
				if(allies.get(selected).get(i).hasDestination)
				{
					doneOrganizing = false;
				}
			}
			if(doneOrganizing)
			{
				turnAfterOrganize();
				organizing[selected] = false;
			}
		}
		for(int i = 0; i < allies.get(selected).size(); i ++)
		{
			allies.get(selected).get(i).frameCallSelection();
			if(allies.get(selected).get(i).hasDestination)
			{
				allies.get(selected).get(i).runTowardsDestination();
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
		organizing[selected] = true;
		Log.e("myid", "forming");
		if(layoutType[selected] == 0)
		{
			setGroupLayoutAttack();
		} else if(layoutType[selected] == 1)
		{
			setGroupLayoutNormal();
		} else if(layoutType[selected] == 2)
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
		for(double i = 1; i <= allies.get(selected).size(); i++)
		{
			double rows = Math.ceil(((double)archers.get(selected).size())/i) + Math.ceil(((double)mages.get(selected).size())/i) + Math.ceil(((double)sheilds.get(selected).size())/i);
			double score = 0.5*i + rows;
			if(score < bestScore)
			{
				bestRows = (int)rows;
				bestScore = score;
				archerRows = (int) Math.ceil(((double)archers.get(selected).size())/i);
				mageRows = (int) Math.ceil(((double)mages.get(selected).size())/i);
				sheildRows = (int) Math.ceil(((double)sheilds.get(selected).size())/i);
			}					//i is best number of people in a row
		}
		archersPerRow = (int) Math.ceil(((double)archers.get(selected).size())/archerRows);
		magesPerRow = (int) Math.ceil(((double)mages.get(selected).size())/mageRows);
		sheildsPerRow = (int) Math.ceil(((double)sheilds.get(selected).size())/sheildRows);
		ArrayList<Point> sheildPositions = new ArrayList<Point>();
		ArrayList<Point> archerPositions = new ArrayList<Point>();
		ArrayList<Point> magePositions = new ArrayList<Point>();
		double pX = (spacing/2) * (bestRows-1);
		double pY;
		for(int i = 0; i < sheildRows; i++)
		{
			pY = -(spacing/2) * (sheildsPerRow-1);
			if(i == sheildRows-1) pY += (spacing/2) * (sheildsPerRow*sheildRows - sheilds.get(selected).size());
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
			if(i == archerRows-1) pY += (spacing/2) * (archersPerRow*archerRows - archers.get(selected).size());
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
			if(i == mageRows-1) pY += (spacing/2) * (magesPerRow*mageRows - mages.get(selected).size());
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
		addX += 500;
		addY += 500;
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			allies.get(selected).get(i).hasDestination = true;
			allies.get(selected).get(i).destinationRotation = 0;
			allies.get(selected).get(i).speedCur = 5;			//faster to get in line
			allies.get(selected).get(i).destinationX = (int)(positions.get(i).X+addX);
			allies.get(selected).get(i).destinationY = (int)(positions.get(i).Y+addY);
		}
	}
	protected void startOrganizing(List<Point> sheildPositions,List<Point> archerPositions, List<Point> magePositions, double addX, double addY)
	{
		addX += 500;
		addY += 500;
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			allies.get(selected).get(i).hasDestination = true;
			allies.get(selected).get(i).destinationRotation = 0;
			allies.get(selected).get(i).speedCur = 5;			//faster to get in line
		}
		for(int i = 0; i < sheilds.get(selected).size(); i++)
		{
			sheilds.get(selected).get(i).destinationX = (int)(sheildPositions.get(i).X+addX);
			sheilds.get(selected).get(i).destinationY = (int)(sheildPositions.get(i).Y+addY);
		}
		for(int i = 0; i < archers.get(selected).size(); i++)
		{
			archers.get(selected).get(i).destinationX = (int)(archerPositions.get(i).X+addX);
			archers.get(selected).get(i).destinationY = (int)(archerPositions.get(i).Y+addY);
		}
		for(int i = 0; i < mages.get(selected).size(); i++)
		{
			mages.get(selected).get(i).destinationX = (int)(magePositions.get(i).X+addX);
			mages.get(selected).get(i).destinationY = (int)(magePositions.get(i).Y+addY);
		}
	}
	protected void turnAfterOrganize()
	{
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			allies.get(selected).get(i).speedCur = 3.5;
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
		int alliesLeft = allies.get(selected).size();
		while(moreUnits)
		{
			double pX = 0; // how far out to start
			double pY = spacing * layer;
			int skip = (layer*4 + 1) - alliesLeft; // layersize - allies.get(selected) left
			for(int i = 0; i < layer*2 + 1; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					if(locations.size() == allies.get(selected).size())
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
								locations.add(new Point(pX, -pY));
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
		int alliesLeft = allies.get(selected).size();
		while(moreUnits)
		{
			double pX = 0; // how far out to start
			double pY = spacingSlanted * layer * 2;
			int skip = (layer*4 + 1) - alliesLeft; // layersize - allies.get(selected) left
			for(int i = 0; i < layer*2 + 1; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					if(locations.size() == allies.get(selected).size())
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
								locations.add(new Point(pX, -pY));
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
	protected void drawSprites(Canvas g, Paint paint, ImageLibrary imageLibrary, double unitWidth, double unitHeight)
	{
		g.save();
		g.scale(ratio, ratio);
		g.translate((float)(unitWidth*75+unitHeight*5)/ratio - 500,(float)(unitHeight*60)/ratio - 500);//(float)(unitWidth*75+unitHeight*5), (float)(unitHeight*60));
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			if(allies.get(selected).get(i).selected) g.drawBitmap(control.imageLibrary.isSelected, (int)allies.get(selected).get(i).x-30, (int)allies.get(selected).get(i).y-30, paint);
		}
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			draw(allies.get(selected).get(i), g, paint);
		}
		g.restore();
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
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			if(allies.get(selected).get(i) != null)
			{
				if(enemyInsideCircle(points, allies.get(selected).get(i).x, allies.get(selected).get(i).y))
				{
					allies.get(selected).get(i).selected = true;
				}
			}
		}
	}
	protected boolean enemyInsideCircle(Vector<Point> p, double x, double y)
	{
		x = mapToScreenPointX(x);
		y = mapToScreenPointY(y);
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
		x = screenToMapPointX(x);
		y = screenToMapPointY(y);
		deselectEnemies();
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			if(allies.get(selected).get(i) != null)
			{
				if(Math.pow(x-allies.get(selected).get(i).x, 2) + Math.pow(y-allies.get(selected).get(i).y, 2) < 600)
				{
					allies.get(selected).get(i).selected = true;
				}
			}
		}
	}
	protected double screenToMapPointX(double X)
    {
		double unitWidth = control.gestureDetector.unitWidth;
		double unitHeight = control.gestureDetector.unitHeight;
		double trX = X - (unitWidth*75+unitHeight*5);
    	return trX/ratio + 500;
    }
	protected double screenToMapPointY(double Y)
    {
		double unitHeight = control.gestureDetector.unitHeight;
		double trY = Y - (unitHeight*60);
    	return trY/ratio + 500;
    }
	protected double mapToScreenPointX(double X)
    {
		double unitWidth = control.gestureDetector.unitWidth;
		double unitHeight = control.gestureDetector.unitHeight;
		return (X-500)*ratio + (unitWidth*75+unitHeight*5);
    }
	protected double mapToScreenPointY(double Y)
    {
		double unitHeight = control.gestureDetector.unitHeight;
		return (Y-500)*ratio + (unitHeight*60);
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
		for(int i = 0; i < allies.get(selected).size(); i++)
		{
			if(allies.get(selected).get(i) != null)
			{
				allies.get(selected).get(i).selected = false;
			}
		}
	}
}