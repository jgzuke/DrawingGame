/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.drawinggame;

import java.util.ArrayList;

import com.spritelib.Sprite;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.Toast;

abstract public class EnemyShell extends Human {
	protected int fromWall = 5;
	protected boolean selected = false;
	protected int runTimer = 0;
	protected int rollTimer = 0;
	protected double velocityX;
	protected double velocityY;
	protected double lastX;
	protected double lastY;
	protected Bitmap[] myImage;
	protected int inDanger = 0;
	protected Point closestDanger = new Point();
	protected double distanceFound;
	protected int radius = 20;
	protected double xMove;
	protected double yMove;
	protected boolean hasDestination = true;
	protected int destinationX;
	protected int destinationY;
	protected int destinationRotation;
	protected int humanType;
	protected ArrayList < Enemy > enemies;
	protected ArrayList < Enemy > allies;
	protected ArrayList < Structure > enemyStructures;
	protected ArrayList < Structure > allyStructures;
	protected ArrayList < Proj_Tracker > proj_Trackers;
	protected ArrayList < Proj_Tracker_AOE > proj_Tracker_AOEs;
	protected Control_Main myController;
	int[][] frames;
	protected String action = "Nothing"; //"Nothing", "Move", "Alert", "Shoot", "Melee", "Roll", "Hide", "Sheild", "Stun"
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public EnemyShell(Controller creator, double X, double Y, double R, int HP, int ImageIndex, boolean isOnPlayersTeam) {
		super(X, Y, 0, 0, true, false, creator.imageLibrary.enemyImages[ImageIndex][0], isOnPlayersTeam);
		humanType = ImageIndex;
		control = creator;
		destinationX = (int) X;
		destinationY = (int) Y;
		if (isOnPlayersTeam) {
			x = 100;
			y = control.levelController.levelHeight - 100;
		} else {
			x = control.levelController.levelWidth - 100;
			y = 100;
		}

		width = 30;
		height = 30;
		lastX = x;
		lastY = y;
		myImage = creator.imageLibrary.enemyImages[ImageIndex];
		image = myImage[frame];
		if (isOnPlayersTeam) {
			enemies = control.spriteController.enemies;
			allies = control.spriteController.allies;
			enemyStructures = control.spriteController.enemyStructures;
			allyStructures = control.spriteController.allyStructures;
			proj_Trackers = control.spriteController.proj_TrackerEs;
			proj_Tracker_AOEs = control.spriteController.proj_TrackerE_AOEs;
		} else {
			enemies = control.spriteController.allies;
			allies = control.spriteController.enemies;
			enemyStructures = control.spriteController.allyStructures;
			allyStructures = control.spriteController.enemyStructures;
			proj_Trackers = control.spriteController.proj_TrackerAs;
			proj_Tracker_AOEs = control.spriteController.proj_TrackerA_AOEs;
		}
	}
	/**
	 * sets new object as controller
	 */
	protected void setController(Control_Main myControllerSet) {
		if (myController != null) {
			myController.removeHuman(this);
		}
		myController = myControllerSet;
	}
	abstract protected void selectSingle();
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */@
	Override
	protected void frameCall() {
		if (x < 10) x = 10;
		if (x > control.levelController.levelWidth - 10) x = (control.levelController.levelWidth - 10);
		if (y < 10) y = 10;
		if (y > control.levelController.levelHeight - 10) y = (control.levelController.levelHeight - 10);

		otherActions();
		image = myImage[frame];
		rollTimer--;
		velocityX = x - lastX;
		velocityY = y - lastY;
		lastX = x;
		lastY = y;
		hp += 4;
		super.frameCall();
		sizeImage();
		pushOtherPeople();
	}
	abstract protected void otherActions();
	/**
	 * checks who else this guy is getting in the way of and pushes em
	 */
	private void pushOtherPeople() {
		double movementX;
		double movementY;
		double moveRads;
		double xdif;
		double ydif;
		ArrayList < Enemy > enemies = control.spriteController.enemies;
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i) != null && enemies.get(i).x != x) {
				xdif = x - enemies.get(i).x;
				ydif = y - enemies.get(i).y;
				if (Math.pow(xdif, 2) + Math.pow(ydif, 2) < Math.pow(radius, 2)) {
					moveRads = Math.atan2(ydif, xdif);
					movementX = (x - (Math.cos(moveRads) * radius) - enemies.get(i).x) / 2;
					movementY = (y - (Math.sin(moveRads) * radius) - enemies.get(i).y) / 2;
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
	protected void getHit(double damage, EnemyShell target) {
		if (!deleted) {
			if (action.equals("Sheild")) damage /= 9;
			if (action.equals("Hide")) action = "Nothing";
			damage /= 1.2;
			super.getHit(damage);
			if (deleted) {
				dieDrops();
			}
		}
	}
	/**
	 * Drops items and stuff if enemy dead
	 */
	protected void dieDrops() {
		control.spriteController.createProj_TrackerAOE(x, y, 140, false, onPlayersTeam);
		control.soundController.playEffect("burst");
	}
	protected boolean checkLOS(int px, int py) {
		return !control.wallController.checkObstructionsPoint((float) x, (float) y, px, py, false, fromWall);
	}
	/**
	 * Checks whether object can 'see' player
	 */
	protected boolean checkLOS(Sprite target) {
		int px = (int) target.x;
		int py = (int) target.y;
		return !control.wallController.checkObstructionsPoint((float) x, (float) y, (float) px, (float) py, false, fromWall);
	}
	/**
	 * what happens when an enemy hits a wall
	 */
	protected void hitWall() {
		//TODO what do we do...
	}
	/**
	 * Checks whether any Proj_Trackers are headed for object
	 */
	protected void checkDanger() {
		inDanger = 0;
		closestDanger.x = 0;
		closestDanger.y = 0;
		for (int i = 0; i < proj_Tracker_AOEs.size(); i++) {
			Proj_Tracker_AOE AOE = proj_Tracker_AOEs.get(i);
			if (AOE.timeToDeath > 7 && Math.pow(x - AOE.x, 2) + Math.pow(y - AOE.y, 2) < Math.pow(AOE.widthDone + 25, 2)) {
				closestDanger.x += AOE.x;
				closestDanger.y += AOE.y;
				inDanger++;
			}
		}
		for (int i = 0; i < proj_Trackers.size(); i++) {
			Proj_Tracker shot = proj_Trackers.get(i);
			if (shot.goodTarget(this, 110)) {
				closestDanger.x += shot.x * 2;
				closestDanger.y += shot.y * 2;
				inDanger += 2;
			}
		}
		closestDanger.x /= inDanger;
		closestDanger.y /= inDanger;
	}
	/**
	 * Checks distance to player
	 * @return Returns distance
	 */
	protected double distanceTo(EnemyShell target) {
		return checkDistance(x, y, target.x, target.y);
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double distanceTo(double toX, double toY) {
		return checkDistance(x, y, toX, toY);
	}
	/**
	 * Checks distance to destination
	 * @return Returns distance
	 */
	protected double distanceToDestination() {
		return checkDistance(x, y, destinationX, destinationY);
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double checkDistance(double fromX, double fromY, double toX, double toY) {
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	protected void baseHp(int setHP) {
		hp = setHP;
		hpMax = hp;
	}
}