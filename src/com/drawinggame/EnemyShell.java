/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.drawinggame;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.widget.Toast;

abstract public class EnemyShell extends Human
{
	protected int fromWall = 5;
	protected int runTimer = 0;
	protected int rollTimer = 0;
	protected int worth = 3;
	protected double velocityX;
	protected double velocityY;
	protected double lastX;
	protected double lastY;
	protected boolean checkedPlayerLast = true;
	protected Bitmap [] myImage;
	protected int imageIndex;
	protected int inDanger = 0;
	protected double[] closestDanger = new double[2];
	protected boolean HasLocation = false;
	protected double lastXSeen = 0;
	protected double lastYSeen = 0;
	protected boolean LOS = false;
	protected double distanceFound;
	private int dangerCheckCounter;
	protected boolean onPlayersTeam;
	protected boolean keyHolder = false;
	protected int radius = 20;
	protected double xMove;
	protected double yMove;
	protected int enemyType;
	protected int hadLOSLastTime=-1;
	private ArrayList<Enemy> enemies;
	private ArrayList<Enemy> allies;
	private ArrayList<Structure> enemyStructures;
	private ArrayList<Structure> allyStructures;
	private ArrayList<Proj_Tracker> proj_Trackers;
	private ArrayList<Proj_Tracker_AOE> proj_Tracker_AOEs;
	
	int [][] frames;
	protected String action = "Nothing"; //"Nothing", "Move", "Alert", "Shoot", "Melee", "Roll", "Hide", "Sheild", "Stun"
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public EnemyShell(Controller creator, double X, double Y, double R, int HP, int ImageIndex, boolean isOnPlayersTeam)
	{
		super(X, Y, 0, 0, true, false, creator.imageLibrary.enemyImages[ImageIndex][0]);
		control = creator;
		x = X;
		y = Y;
		width = 30;
		height = 30;
		lastX = x;
		lastY = y;
		imageIndex = ImageIndex;
		enemyType = ImageIndex;
		myImage = creator.imageLibrary.enemyImages[ImageIndex];
		image = myImage[frame];
		onPlayersTeam = isOnPlayersTeam;
		if(isOnPlayersTeam)
		{
			enemies = control.spriteController.enemies;
			allies = control.spriteController.allies;
			enemyStructures = control.spriteController.enemyStructures;
			allyStructures = control.spriteController.allyStructures;
			proj_Trackers = control.spriteController.proj_TrackerEs;
			proj_Tracker_AOEs = control.spriteController.proj_TrackerE_AOEs;
		} else
		{
			enemies = control.spriteController.allies;
			allies = control.spriteController.enemies;
			enemyStructures = control.spriteController.allyStructures;
			allyStructures = control.spriteController.enemyStructures;
			proj_Trackers = control.spriteController.proj_TrackerAs;
			proj_Tracker_AOEs = control.spriteController.proj_TrackerA_AOEs;
		}
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@
	Override
	protected void frameCall()
	{
		otherActions();
		if(action.equals("Nothing"))
		{
			checkLOS();
			checkDanger();
			if(LOS)
			{
				frameLOS();
			} else
			{
				frameNoLOS();
			}
		}
		image = myImage[frame];
		rollTimer --;
		hadLOSLastTime--;
		velocityX = x - lastX;
		velocityY = y - lastY;
		lastX = x;
		lastY = y;
		hp += 4;
		super.frameCall();
		sizeImage();
		pushOtherPeople();
	}
	abstract protected void attacking();
	abstract protected void hiding();
	abstract protected void shooting();
	abstract protected void blocking();
	abstract protected void finishWandering();
	abstract protected void frameLOS();
	abstract protected void frameNoLOS();
	abstract protected void otherActions();
	/**
	 * checks who else this guy is getting in the way of and pushes em
	 */
	private void pushOtherPeople()
	{
		double movementX;
		double movementY;
		double moveRads;
		double xdif;
		double ydif;
		ArrayList<Enemy> enemies = control.spriteController.enemies;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null&& enemies.get(i).x != x)
			{
				xdif = x - enemies.get(i).x;
				ydif = y - enemies.get(i).y;
				if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < Math.pow(radius, 2))
				{
					moveRads = Math.atan2(ydif, xdif);
					movementX = (x - (Math.cos(moveRads) * radius) - enemies.get(i).x)/2;
					movementY = (y - (Math.sin(moveRads) * radius) - enemies.get(i).y)/2;
					enemies.get(i).x += movementX;
					enemies.get(i).y += movementY;
					x -= movementX;
					y -= movementY;
				}
			}
		}
	}
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage, EnemyShell target)
	{
		if(!deleted)
		{
			if(action.equals("Sheild")) damage /= 9;
			getEnemyLocation(target);
			if(action.equals("Hide")) action = "Nothing";
			damage /= 1.2;
			super.getHit(damage);
			if(deleted)
			{
				dieDrops();
			}
		}
	}
	/**
	 * Drops items and stuff if enemy dead
	 */
	protected void dieDrops()
	{
			control.spriteController.createProj_TrackerEnemyAOE(x, y, 140, false);
			control.soundController.playEffect("burst");
			control.itemControl.favor+= (double)worth/10;
	}
	protected boolean checkLOS(int px, int py)
	{
		return !control.wallController.checkObstructionsPoint((float)x, (float)y, px, py, false, fromWall);
	}
	/**
	 * Checks whether object can 'see' player
	 */
	protected void checkLOS(EnemyShell target)
	{
		int px = (int)target.x;
		int py = (int)target.y;
		if(!control.wallController.checkObstructionsPoint((float)x, (float)y, (float)px, (float)py, false, fromWall))
		{
			LOS = true;
			hadLOSLastTime = 25;
			lastXSeen = px;
			lastYSeen = py;
			checkedPlayerLast = false;
		} else
		{
			LOS = false;
		}
		HasLocation = hadLOSLastTime>0;
		if(HasLocation)	//tell others where player is
		{
			callPlayerLocation(target);
		}
	}
	/**
	 * tells other enemies where player is
	 */
	protected void callPlayerLocation(EnemyShell target)
	{
		for(int i = 0; i < control.spriteController.enemies.size(); i++)
		{
			Enemy enemy = control.spriteController.enemies.get(i);
			if(!enemy.HasLocation&&checkDistance(x, y, enemy.x, enemy.y)<200)
			{
				enemy.getEnemyLocation(target);
			}
		}
	}
	/**
	 * hears where player is
	 */
	protected void getEnemyLocation(EnemyShell target)
	{
		if(!LOS)
		{
			lastXSeen = target.x;
			lastYSeen = target.y;
			rads = Math.atan2((target.y - y), (target.x - x));
			rotation = rads * r2d;
		}
	}
	/**
	 * what happens when an enemy hits a wall
	 */
	protected void hitWall()
	{
		//TODO what do we do...
	}
	/**
	 * Checks whether any Proj_Trackers are headed for object
	 */
	protected void checkDanger()
	{   
		inDanger = 0;
		closestDanger[0] = 0;
		closestDanger[1] = 0;
		for(int i = 0; i < proj_Tracker_AOEs.size(); i++)
		{
			Proj_Tracker_AOE AOE = proj_Tracker_AOEs.get(i);
			if(AOE.timeToDeath>7 && Math.pow(x-AOE.x, 2)+Math.pow(y-AOE.y, 2)<Math.pow(AOE.widthDone+25, 2))
			{
				closestDanger[0]+=AOE.x;
				closestDanger[1]+=AOE.y;
				inDanger++;
			}
		}
		for(int i = 0; i < proj_Trackers.size(); i++)
		{
			Proj_Tracker shot = proj_Trackers.get(i);
			if(shot.goodTarget(this, 110))
			{
				closestDanger[0]+=shot.x*2;
				closestDanger[1]+=shot.y*2;
				inDanger+=2;
			}
		}
		closestDanger[0]/=inDanger;
		closestDanger[1]/=inDanger;
	}
	/**
	 * Checks distance to player
	 * @return Returns distance
	 */
	protected double distanceTo(EnemyShell target)
	{
		return checkDistance(x, y, target.x, target.y);
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double distanceTo(double toX, double toY)
	{
		return checkDistance(x, y, toX, toY);
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	protected void baseHp(int setHP)
	{
		hp = setHP;
		hpMax = hp;
	}
}