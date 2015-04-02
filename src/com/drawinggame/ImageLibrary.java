/**
 * Loads, stores and resizes all graphics
 */
package com.drawinggame;
import com.spritelib.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
public final class ImageLibrary extends ImageLoader
{
	protected Bitmap[][] enemyImages = new Bitmap[10][100]; //array holding videos for each enemy, null when uneeded
	protected Bitmap structure_Spawn;
	protected Bitmap isSelected;
	protected int isPlayerWidth;
	protected Bitmap[] shotPlayer;
	protected Bitmap shotAOEPlayer;
	protected Bitmap[] shotEnemy;
	protected Bitmap shotAOEEnemy;
	//protected Bitmap currentLevel;
	//protected Bitmap currentLevelTop;
	protected Bitmap backDrop;
	protected Bitmap[] createMarkers = new Bitmap[3];
	private Controller control;
	/**
	 * loads in images and optimizes settings for loading
	 * @param contextSet start activity for getting resources etc
	 * @param controlSet control object
	 */
	public ImageLibrary(Context contextSet, Controller controlSet)
	{
		super(contextSet);
		control = controlSet;
		loadAllImages();
	}
	/**
	 * loads all required images for all games
	 */
	protected void loadAllImages()
	{
		isSelected = loadImage("icon_isselected", 60, 60);
		shotAOEEnemy = loadImage("shootenemyaoe", 80, 80);
		shotEnemy = loadArray1D(5, "shootenemy", 35, 15);
		shotAOEPlayer = loadImage("shootplayeraoe", 80, 80);
		shotPlayer = loadArray1D(5, "shootplayer", 35, 15);
		
		createMarkers[0] = loadImage("createswordsman", 44, 66);
		createMarkers[1] = loadImage("createarcher", 35, 64);
		createMarkers[2] = loadImage("createmage", 36, 62);
		backDrop = loadImage("leveltile1", 100, 100);
		enemyImages[0]= loadArray1D(55, "goblin_swordsman", 110, 70);
		enemyImages[1]= loadArray1D(49, "goblin_archer", 80, 50);
		enemyImages[2]= loadArray1D(31, "goblin_mage", 30, 34);
	}
	/**
	 * loads level image layers and background image
	 * @param levelNum level to load
	 * @param width width of level
	 * @param height height of level
	 */
	protected void loadLevel(int levelNum, int width, int height)
	{
		/*if(currentLevel != null)
		{
			currentLevel.recycle();
			currentLevel = null;
		}
		if(currentLevelTop != null)
		{
			currentLevelTop.recycle();
			currentLevelTop = null;
		}*/
		if(backDrop!= null)
		{
			backDrop.recycle();
		}
		switch(levelNum)
		{
		case 1:
		case 2:
		case 3:
			backDrop = loadImage("leveltile1", 100, 100);
			break;
		case 5:
		case 6:
		case 7:
		case 8:
			backDrop = loadImage("leveltile2", 100, 100);
			break;
		default:
			backDrop = loadImage("leveltile1", 100, 100);
			break;
		}
		//currentLevel = loadImage("level"+Integer.toString(levelNum), width, height);
		//currentLevelTop = loadImage("leveltop"+Integer.toString(levelNum), width, height);
	}
	protected void recycleEnemies()
	{
		for(int i = 0; i < enemyImages.length; i++)
		{
			recycleArray(enemyImages[i]);
		}
	}
	/**
	 * recycles images to save memory
	 */
	protected void recycleImages()
	{
		/*if(currentLevel != null)
		{
			currentLevel.recycle();
			currentLevel = null;
		}
		if(currentLevelTop != null)
		{
			currentLevelTop.recycle();
			currentLevelTop = null;
		}*/
		recycleEnemies();
	}
}