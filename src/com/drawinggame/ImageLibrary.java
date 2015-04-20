/**
 * Loads, stores and resizes all graphics
 */
package com.drawinggame;
import com.spritelib.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
public final class ImageLibrary extends ImageLoader
{
	protected Bitmap[][] enemyImages = new Bitmap[6][100]; //array holding videos for each enemy, null when uneeded
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
	protected Bitmap[] createMarkers = new Bitmap[8];
	protected Bitmap menu_top;
	protected Bitmap menu_side;
	protected Bitmap menu_middle;
	protected Bitmap icon_back;
	protected Bitmap icon_delete;
	protected Bitmap icon_cancel;
	protected Bitmap icon_menu;
	private Controller control;
	private int phoneWidth;
	private int phoneHeight;
	/**
	 * loads in images and optimizes settings for loading
	 * @param contextSet start activity for getting resources etc
	 * @param controlSet control object
	 */
	public ImageLibrary(Context contextSet, Controller controlSet, int pWidth, int pHeight)
	{
		super(contextSet);
		control = controlSet;
		phoneWidth = pWidth;
		phoneHeight = pHeight;
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
		createMarkers[3] = loadImage("createswordsman", 44, 66);
		createMarkers[4] = loadImage("createarcher", 35, 64);
		createMarkers[5] = loadImage("createmage", 36, 62);
		createMarkers[6] = loadImage("createswordsman", 88, 132);
		createMarkers[7] = loadImage("createarcher", 88, 132);
		backDrop = loadImage("leveltile1", 400, 400);
		enemyImages[0]= loadArray1D(55, "goblin_swordsman", 110, 70);
		enemyImages[1]= loadArray1D(49, "goblin_archer", 80, 50);
		enemyImages[2]= loadArray1D(31, "goblin_mage", 30, 34);
		enemyImages[3]= loadArray1D(55, "human_swordsman", 110, 70);
		enemyImages[4]= loadArray1D(49, "human_archer", 80, 50);
		enemyImages[5]= loadArray1D(31, "human_mage", 30, 34);
		double w = phoneWidth;
		double h = phoneHeight;
		menu_top = loadImage("menu_top", (int)(w), (int)(h/5));
		menu_side = loadImage("menu_side", (int)(w/2 - h/10), (int)(h*4/5));
		menu_middle = loadImage("menu_middle", (int)(h/5), (int)(h*4/5));
		icon_back = loadImage("icon_back", 150, 150);
		icon_delete = loadImage("icon_delete", 150, 150);
		icon_cancel = loadImage("icon_cancel", 150, 150);
		icon_menu = loadImage("icon_menu", 150, 150);
	}
	/**
	 * loads level image layers and background image
	 * @param levelNum level to load
	 * @param width width of level
	 * @param height height of level
	 */
	protected void loadLevel(int levelNum, int width, int height)
	{
		if(backDrop!= null)
		{
			backDrop.recycle();
		}
		switch(levelNum)
		{
		case 1:
		case 2:
		case 3:
			backDrop = loadImage("leveltile1", 400, 400);
			break;
		case 5:
		case 6:
		case 7:
		case 8:
			backDrop = loadImage("leveltile2", 100, 100);
			break;
		default:
			backDrop = loadImage("leveltile1", 400, 400);
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