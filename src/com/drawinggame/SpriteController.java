
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
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

import lx.interaction.dollar.Point;

public final class SpriteController
{
	private Matrix rotateImages = new Matrix();
	protected static int magePrice = 85;
	protected static int archerPrice = 60;
	protected static int sheildPrice = 75;
	protected static double depreciation = 0.96;
	protected Controller control;
	protected ArrayList<Control_Main> allyControllers = new ArrayList<Control_Main>();
	protected ArrayList<Control_Main> enemyControllers = new ArrayList<Control_Main>();
	protected ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	protected ArrayList<Enemy> allies = new ArrayList<Enemy>();
	protected ArrayList<Structure> enemyStructures = new ArrayList<Structure>();
	protected ArrayList<Structure> allyStructures = new ArrayList<Structure>();
	protected ArrayList<Proj_Tracker> proj_TrackerEs = new ArrayList<Proj_Tracker>();
	protected ArrayList<Proj_Tracker> proj_TrackerAs = new ArrayList<Proj_Tracker>();
	protected ArrayList<Proj_Tracker_AOE> proj_TrackerA_AOEs = new ArrayList<Proj_Tracker_AOE>();
	protected ArrayList<Proj_Tracker_AOE> proj_TrackerE_AOEs = new ArrayList<Proj_Tracker_AOE>();
	protected ArrayList<CreationMarker> creationMarkers = new ArrayList<CreationMarker>();
	protected int[][] groupDetails = {{3, 2, 1, 0}, {3, 2, 3, 1}, {3, 2, 1, 2}, {3, 0, 4, 1}};
	protected int[][] groupDetailsEnemy = {{3, 2, 1, 0}, {3, 2, 3, 1}, {3, 2, 1, 2}, {3, 0, 4, 1}};
	//makeGroup(s, a, m, form, x, y, isOnPlayersTeam, toAdd, toAddController);
	protected Bitmap playerBlessing;
	protected int [] manaPrices = {sheildPrice, archerPrice, magePrice, 0, 0, 0, 0};
	protected int [] manaPricesEnemy = {sheildPrice, archerPrice, magePrice, 0, 0, 0, 0};
	protected Bitmap isSelected;
	protected Game_Control_Player playerGameControl;
	protected Game_Control_Enemy enemyGameControl;
	private Paint fogPaint = new Paint();
	/**
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public SpriteController(Context contextSet, Controller controlSet)
	{
		super();
		control = controlSet;
		playerGameControl = new Game_Control_Player(control);
		enemyGameControl = new Game_Control_Enemy(control);
		setPrices();
		setPricesEnemy();

		fogPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
		fogPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
		fogPaint.setStyle(Style.FILL_AND_STROKE);
		fogPaint.setColor(Color.argb(80, 0, 0, 0));
	}
	protected void setPrices()
	{
		for(int i = 0; i < 4; i++)
		{
			manaPrices[i+3] = (int)Math.pow(groupDetails[i][0]*sheildPrice + groupDetails[i][1]*archerPrice + groupDetails[i][2]*magePrice, depreciation);
		}//100, 60, 140
	}
	protected void setPricesEnemy()
	{
		for(int i = 0; i < 4; i++)
		{
			manaPricesEnemy[i+3] = (int)Math.pow(groupDetailsEnemy[i][0]*sheildPrice + groupDetailsEnemy[i][1]*archerPrice + groupDetailsEnemy[i][2]*magePrice, depreciation);
		}//100, 60, 140
	}
	/**
	 * clears all arrays to restart game
	 */
	protected void clearObjectArrays()
	{
		enemies.clear();
		enemyStructures.clear();
		allies.clear();
		allyStructures.clear();
		proj_TrackerEs.clear();
		proj_TrackerAs.clear();
		proj_TrackerA_AOEs.clear();
		proj_TrackerE_AOEs.clear();
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
			if(playerGameControl.mana < manaPrices[type])
			{
				Toast.makeText(control.context, "Not enough mana.", Toast.LENGTH_SHORT).show();
				return;
			}
			playerGameControl.mana -= manaPrices[type];
		} else
		{
			if(enemyGameControl.mana < manaPricesEnemy[type])
			{
				return;
			}
			enemyGameControl.mana -= manaPricesEnemy[type];
		}
		creationMarkers.add(new CreationMarker(x, y, control.imageLibrary.createMarker, this));		
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
			int [] deets;
			if(isOnPlayersTeam) deets = groupDetails[type-3];
			else deets = groupDetailsEnemy[type-3];
			makeGroup(deets[0], deets[1], deets[2], deets[3], x, y, isOnPlayersTeam, toAdd, toAddController);
			break;
		}
	}
	protected Enemy makeEnemyBasic(int type, int x, int y, boolean isOnPlayersTeam, ArrayList<Enemy> toAdd, ArrayList<Control_Main> toAddController)
	{
		int imageType = 0;
		if(isOnPlayersTeam) imageType += 3;
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
		playerGameControl.frameCall();
		enemyGameControl.frameCall();
		for(int i = 0; i < creationMarkers.size(); i++)
		{
			creationMarkers.get(i).frameCall();
		}
		for(int i = 0; i < proj_TrackerEs.size(); i++)
		{
			proj_TrackerEs.get(i).frameCall();
		}
		for(int i = 0; i < proj_TrackerAs.size(); i++)
		{
			proj_TrackerAs.get(i).frameCall();
		}
		for(int i = 0; i < proj_TrackerE_AOEs.size(); i++)
		{
			proj_TrackerE_AOEs.get(i).frameCall();
		}
		for(int i = 0; i < proj_TrackerA_AOEs.size(); i++)
		{
			proj_TrackerA_AOEs.get(i).frameCall();
		}
		for(int i = 0; i < allies.size(); i++)
		{
			allies.get(i).frameCall();
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			enemies.get(i).frameCall();
		}
		for(int i = 0; i < enemyStructures.size(); i++)
		{
			enemyStructures.get(i).frameCall();
		}
		for(int i = 0; i < allyStructures.size(); i++)
		{
			allyStructures.get(i).frameCall();
		}
		for(int i = 0; i < allyControllers.size(); i++)
		{
			allyControllers.get(i).frameCall();
		}
		for(int i = 0; i < enemyControllers.size(); i++)
		{
			enemyControllers.get(i).frameCall();
		}
	}
	/**
	 * draws all enemy health bars
	 * @param g canvas to draw to
	 */
	protected void drawHealthBars(Canvas g, Paint paint)
	{
		int minX;
		int maxX;
		int minY;
		int maxY;
		//int offset;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null && enemies.get(i).hp != enemies.get(i).hpMax)
			{
					minX = (int) enemies.get(i).x - 20;
					maxX = (int) enemies.get(i).x + 20;
					minY = (int) enemies.get(i).y - 30;
					maxY = (int) enemies.get(i).y - 20;
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					g.drawRect(minX, minY, minX + (40 * enemies.get(i).hp / enemies.get(i).hpMax), maxY, paint);
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(1);
					paint.setStyle(Paint.Style.STROKE);
					g.drawRect(minX, minY, maxX, maxY, paint);
			}
		}
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i) != null && allies.get(i).hp != allies.get(i).hpMax)
			{
					minX = (int) allies.get(i).x - 20;
					maxX = (int) allies.get(i).x + 20;
					minY = (int) allies.get(i).y - 30;
					maxY = (int) allies.get(i).y - 20;
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					g.drawRect(minX, minY, minX + (40 * allies.get(i).hp / allies.get(i).hpMax), maxY, paint);
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(1);
					paint.setStyle(Paint.Style.STROKE);
					g.drawRect(minX, minY, maxX, maxY, paint);
			}
		}
		for(int i = 0; i < enemyStructures.size(); i++)
		{
			if(enemyStructures.get(i) != null)
			{
				minX = (int) enemyStructures.get(i).x - 20;
				maxX = (int) enemyStructures.get(i).x + 20;
				minY = (int) enemyStructures.get(i).y - 30;
				maxY = (int) enemyStructures.get(i).y - 20;
				paint.setColor(Color.BLUE);
				paint.setStyle(Paint.Style.FILL);
				g.drawRect(minX, minY, minX + (40 * enemyStructures.get(i).hp / enemyStructures.get(i).hpMax), maxY, paint);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				g.drawRect(minX, minY, maxX, maxY, paint);
			}
		}
		for(int i = 0; i < allyStructures.size(); i++)
		{
			if(allyStructures.get(i) != null)
			{
				minX = (int) allyStructures.get(i).x - 20;
				maxX = (int) allyStructures.get(i).x + 20;
				minY = (int) allyStructures.get(i).y - 30;
				maxY = (int) allyStructures.get(i).y - 20;
				paint.setColor(Color.BLUE);
				paint.setStyle(Paint.Style.FILL);
				g.drawRect(minX, minY, minX + (40 * allyStructures.get(i).hp / allyStructures.get(i).hpMax), maxY, paint);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				g.drawRect(minX, minY, maxX, maxY, paint);
			}
		}
	}
	/**
	 * draws all the structures onto the map
	 * @param g canvas to use
	 * @param paint paint to use
	 * @param imageLibrary imageLibrary to use
	 */
	protected void drawStructures(Canvas g, Paint paint, ImageLibrary imageLibrary)
	{
		for(int i = 0; i < allyStructures.size(); i++)
		{
			drawFlat(allyStructures.get(i), g, paint);
		}
		for(int i = 0; i < enemyStructures.size(); i++)
		{
			drawFlat(enemyStructures.get(i), g, paint);
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
		Rect aoeRect = new Rect();
		boolean [][] visibilityMap = getVisibilityMap();
		for(int i = 0; i < creationMarkers.size(); i++)
		{
			paint.setAlpha(creationMarkers.get(i).getAlpha());
			drawFlat(creationMarkers.get(i), g, paint);
		}
		paint.setAlpha(255);
		for(int i = 0; i < allies.size(); i++)
		{
			if(allies.get(i).selected) g.drawBitmap(control.imageLibrary.isSelected, (int)allies.get(i).x-30, (int)allies.get(i).y-30, paint);
		}
		for(int i = 0; i < allies.size(); i++)
		{
			draw(allies.get(i), g, paint);
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			draw(enemies.get(i), g, paint);
		}
		for(int i = 0; i < proj_TrackerAs.size(); i++)
		{
			paint.setAlpha(proj_TrackerAs.get(i).alpha);
			draw(proj_TrackerAs.get(i), g, paint);
		}
		for(int i = 0; i < proj_TrackerEs.size(); i++)
		{
			paint.setAlpha(proj_TrackerEs.get(i).alpha);
			draw(proj_TrackerEs.get(i), g, paint);
		}
		for(int i = 0; i < proj_TrackerE_AOEs.size(); i++)
		{
			paint.setAlpha(proj_TrackerE_AOEs.get(i).alpha);
			aoeRect.top = (int)(proj_TrackerE_AOEs.get(i).y - (proj_TrackerE_AOEs.get(i).getHeight() / 2.5));
			aoeRect.bottom = (int)(proj_TrackerE_AOEs.get(i).y + (proj_TrackerE_AOEs.get(i).getHeight() / 2.5));
			aoeRect.left = (int)(proj_TrackerE_AOEs.get(i).x - (proj_TrackerE_AOEs.get(i).getWidth() / 2.5));
			aoeRect.right = (int)(proj_TrackerE_AOEs.get(i).x + (proj_TrackerE_AOEs.get(i).getWidth() / 2.5));
			drawRect(proj_TrackerE_AOEs.get(i).image, aoeRect, g, paint);
		}
		for(int i = 0; i < proj_TrackerA_AOEs.size(); i++)
		{
			paint.setAlpha(proj_TrackerA_AOEs.get(i).alpha);
			aoeRect.top = (int)(proj_TrackerA_AOEs.get(i).y - (proj_TrackerA_AOEs.get(i).getHeight() / 2.5));
			aoeRect.bottom = (int)(proj_TrackerA_AOEs.get(i).y + (proj_TrackerA_AOEs.get(i).getHeight() / 2.5));
			aoeRect.left = (int)(proj_TrackerA_AOEs.get(i).x - (proj_TrackerA_AOEs.get(i).getWidth() / 2.5));
			aoeRect.right = (int)(proj_TrackerA_AOEs.get(i).x + (proj_TrackerA_AOEs.get(i).getWidth() / 2.5));
			drawRect(proj_TrackerA_AOEs.get(i).image, aoeRect, g, paint);
		}
		drawFog(g, paint, visibilityMap);
	}
	protected void drawFog(Canvas g, Paint paint, boolean [][] visibleArea)
	{
		int wide = visibleArea.length;
		boolean[][] visibleEdge= new boolean[wide][wide];
		int[][] visibleEdgeCounts= new int[wide][wide];
		paint.setColor(Color.argb(100, 255, 0, 0));
		for(int i = 0; i < wide; i++)
		{
			for(int j = 0; j < wide; j ++)
			{
				if(visibleArea[i][j])
				{
					visibleEdge[i][j] = edgeOfVisibility(i, j, visibleArea, wide);
				}
			}
		}
		for(int i = 0; i < wide; i++)
		{
			for(int j = 0; j < wide; j ++)
			{
				if(visibleEdge[i][j])
				{
					visibleEdgeCounts[i][j] = connectingEdges(i, j, visibleEdge, wide);
					if(visibleEdgeCounts[i][j] == 1)
					{
						int pX = i*fogSize;
						int pY = j*fogSize;
						g.drawRect(pX, pY, pX + fogSize, pY + fogSize, paint);
					}
					if(visibleEdgeCounts[i][j] == 2)
					{
						paint.setColor(Color.argb(100, 0, 0, 255));
						int pX = i*fogSize;
						int pY = j*fogSize;
						g.drawRect(pX, pY, pX + fogSize, pY + fogSize, paint);
						paint.setColor(Color.argb(100, 255, 0, 0));
					}
				}
			}
		}
		int levelWidth = control.levelController.levelHeight;
		Path fullPath = new Path();
		fullPath.moveTo(-50, -50);
		fullPath.lineTo(levelWidth+50, -50);
		fullPath.lineTo(levelWidth+50, levelWidth+50);
		fullPath.lineTo(-50, levelWidth+50);
		fullPath.lineTo(-50, -50);
		addToEdgePath(fullPath, visibleEdgeCounts);
		fullPath.close();
		fogPaint.setColor(Color.argb(50, 0, 0, 0));
		fogPaint.setStyle(Style.FILL);
		g.drawPath(fullPath, fogPaint);
		fogPaint.setColor(Color.rgb(0, 0, 0));
		fogPaint.setStyle(Style.STROKE);
		fogPaint.setStrokeWidth(1);
		g.drawPath(fullPath, fogPaint);
	}
	private void addToEdgePath(Path path, int [][] edge)
	{
		int [] p = findEdgePath(edge);
		if(p == null) return;
		int [] firstP = p.clone();
		boolean pathDone = false;
		int x;
		int y;
		path.lineTo(p[0] * fogSize + fogSize/2, p[1] * fogSize + fogSize/2);
		while(!pathDone)
		{
			x = p[0] * fogSize + fogSize/2;
			y = p[1] * fogSize + fogSize/2;
			pathDone = findNextEdge(edge, p);
			if(!pathDone)
			{
				path.quadTo(x, y, p[0] * fogSize + fogSize/2, p[1] * fogSize + fogSize/2);
			} else
			{
				path.lineTo(x, y);
			}
			pathDone = findNextEdge(edge, p);
		}
		path.lineTo(firstP[0] * fogSize + fogSize/2, firstP[1] * fogSize + fogSize/2);
		path.close();
		addToEdgePath(path, edge);
	}
	private int lastDirection = -1; // 0:left, 1:up, 2:right, 3: down
	private boolean findNextEdge(int [][] edge, int [] p) // 0 left, 1 top, 2 right, 3 bottom
	{
		edge[p[0]][p[1]] --;
		boolean used = edge[p[0]][p[1]] == 0;
		if(lastDirection != 1 && p[1]+1 < edge.length && edge[p[0]][p[1]+1] > 0)
		{
			p[1] ++;
			lastDirection = 3;
		} else if(lastDirection != 3 && p[1]-1 >=0 && edge[p[0]][p[1]-1] > 0)
		{
			p[1] --;
			lastDirection = 1;
		} else if(lastDirection != 0 && p[0]+1 < edge.length && edge[p[0]+1][p[1]] > 0)
		{
			p[0] ++;
			lastDirection = 2;
		} else if(lastDirection != 2 && p[0]-1 >= 0 && edge[p[0]-1][p[1]] > 0)
		{
			p[0] --;
			lastDirection = 0;
		} else 
		{
			switch(lastDirection)
			{
			case 0:
				if(p[0]+1 < edge.length && edge[p[0]+1][p[1]] > 0)
				{
					p[0] ++;
					lastDirection = 2;
					return false;
				}
			case 1:
				if(p[1]+1 < edge.length && edge[p[0]][p[1]+1] > 0)
				{
					p[1] ++;
					lastDirection = 3;
					return false;
				}
			case 2:
				if(p[0]-1 >= 0 && edge[p[0]-1][p[1]] > 0)
				{
					p[0] --;
					lastDirection = 0;
					return false;
				}
			case 3:
				if(p[1]-1 >=0 && edge[p[0]][p[1]-1] > 0)
				{
					p[1] --;
					lastDirection = 1;
					return false;
				}
			}
			return true;
		}
		return false;
	} 
	private int[] findEdgePath(int [][] edge)
	{
		lastDirection = -1;
		for(int i = 0; i < edge.length; i++)
		{
			for(int j = 0; j < edge.length; j++)
			{
				if(edge[i][j] > 0)
				{
					int [] found = {i,j};
					return found;
				}
			}
		}
		return null;
	}
	protected int connectingEdges(int i, int j, boolean [][] visibleEdge, int wide)
	{
		int count = 0;
		if(i > 0 && visibleEdge[i-1][j]) count ++;
		if(j > 0 && visibleEdge[i][j-1]) count ++;
		if(i < wide-1 && visibleEdge[i+1][j]) count ++;
		if(j < wide-1 && visibleEdge[i][j+1]) count ++;
		return count - 1;
	}
	protected boolean edgeOfVisibility(int x, int y, boolean [][] a, int wide)
	{
		if(x == 0 || y == 0 || x == wide-1 || y == wide-1) return true; // is on the edge
		// if any near squares are visible
		return !(a[x-1][y-1] && a[x-1][y] && a[x-1][y+1] && a[x][y+1] && a[x+1][y+1] && a[x+1][y] && a[x+1][y-1] && a[x][y-1]);
	}
	protected int fogSize = 20;
	protected boolean[][] getVisibilityMap()
	{
		LevelController l = control.levelController;
		int wide = l.levelWidth/fogSize + 1;
		boolean[][] visibleArea= new boolean[wide][wide];
		for(int i = 0; i < allies.size(); i++)
		{
			int x = (int) (allies.get(i).x/fogSize);
			int y = (int) (allies.get(i).y/fogSize);
			double radius = 450 / fogSize;
			for(int j = 0; j < radius; j ++)
			{
				for(int k = 0; k < Math.sqrt(Math.pow(radius, 2) - Math.pow(j, 2)); k ++)
				{
					if(x+j<wide && y+k < wide) visibleArea[x + j][y + k] = true;
					if(x+j<wide && y-k >= 0) visibleArea[x + j][y - k] = true;
					if(x-j >= 0 && y+k < wide) visibleArea[x - j][y + k] = true;
					if(x-j >= 0 && y-k >= 0) visibleArea[x - j][y - k] = true;
				}
			}
		}
		return visibleArea;
	}
	/**
	 * creates an enemy power ball
	 * @param rotation rotation of Proj_Tracker
	 * @param xVel horizontal velocity of ball
	 * @param yVel vertical velocity of ball
	 * @param power power of ball
	 * @param x x position
	 * @param y y position
	 */
	protected void createProj_Tracker(double rotation, double Vel, int power, double x, double y, boolean onPlayersTeam)
	{
		if(onPlayersTeam) proj_TrackerAs.add(new Proj_Tracker(control, (int)x, (int)y, power, Vel, rotation, this, control.imageLibrary.shotPlayer[0], onPlayersTeam));
		else proj_TrackerEs.add(new Proj_Tracker(control, (int)x, (int)y, power, Vel, rotation, this, control.imageLibrary.shotPlayer[1], onPlayersTeam));
	}
	/**
	 * creates an emeny AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 * @param damaging whether it damages player
	 */
	protected void createProj_TrackerAOE(double x, double y, double power, boolean damaging, boolean onPlayersTeam)
	{
		if(onPlayersTeam) proj_TrackerA_AOEs.add(new Proj_Tracker_AOE(control, (int) x, (int) y, power, true, this, control.imageLibrary.shotAOEPlayer, damaging, onPlayersTeam));
		else proj_TrackerE_AOEs.add(new Proj_Tracker_AOE(control, (int) x, (int) y, power, true, this, control.imageLibrary.shotAOEEnemy, damaging, onPlayersTeam));
	}
	/**
	 * creates an enemy burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerBurst(double x, double y, double power, boolean onPlayersTeam)
	{
		if(onPlayersTeam) proj_TrackerA_AOEs.add(new Proj_Tracker_AOE(control, (int) x, (int) y, power, false, this, control.imageLibrary.shotAOEPlayer, true, onPlayersTeam));
		else proj_TrackerE_AOEs.add(new Proj_Tracker_AOE(control, (int) x, (int) y, power, false, this, control.imageLibrary.shotAOEEnemy, true, onPlayersTeam));
	}
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
					//if(countGroup > 28) break;
					group.add(allies.get(i));
				}
			}
		}
		selectGroup(group);
	}
	protected void selectGroup(Control_Main g1, Control_Main g2, boolean isOnPlayersTeam)
	{
		ArrayList<Enemy> group = new ArrayList<Enemy>();
		if(g1.isGroup)
		{
			group.addAll(((Control_Group)g1).humans);
		} else
		{
			group.add((Enemy) ((Control_Induvidual)g1).human);
		}
		if(g2.isGroup)
		{
			group.addAll(((Control_Group)g2).humans);
		} else
		{
			group.add((Enemy) ((Control_Induvidual)g1).human);
		}
		Control_Group enemyGroup = groupEnemies(group, isOnPlayersTeam);
		if(isOnPlayersTeam)
		{
			allyControllers.add(enemyGroup);
		} else
		{
			enemyControllers.add(enemyGroup);
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
	
	
	
	
	
	
	/**
	 * draws a sprite
	 */
	public void drawFlat(Sprite sprite, Canvas g, Paint p)
	{
		if(sprite!=null)
		{
			g.drawBitmap(sprite.image, (int)sprite.x-(sprite.width/2), (int)sprite.y-(sprite.height/2), p);
		}
	}
	/**
	 * draws a sprite
	 */
	public void drawFlat(Sprite sprite, Bitmap image, Canvas g, Paint p)
	{
		int w = image.getWidth();
		int h  = image.getHeight();
		if(sprite!=null)
		{
			g.drawBitmap(image, (int)sprite.x-(w/2), (int)sprite.y-(h/2), p);
		}
	}
	/**
	 * draws a sprite
	 */
	public void draw(Sprite sprite, Canvas g, Paint p)
	{
		if(sprite!=null)
		{
				rotateImages.reset();
				rotateImages.postTranslate(-sprite.width / 2, -sprite.height / 2);
				rotateImages.postRotate((float) sprite.rotation);
				rotateImages.postTranslate((float) sprite.x, (float) sprite.y);
				g.drawBitmap(sprite.image, rotateImages, p);
		}
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	public void drawRect(Bitmap image, Rect r, Canvas g, Paint p)
	{
		g.drawBitmap(image, null, r, p);
	}
}