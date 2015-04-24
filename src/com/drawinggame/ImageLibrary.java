/**
 * Loads, stores and resizes all graphics
 */
package com.drawinggame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
public final class ImageLibrary
{
	public String getting;
	public Resources res;
	public String packageName;
	public BitmapFactory.Options opts;
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
	protected Bitmap createMarker;
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
		opts = new BitmapFactory.Options();
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inTempStorage = new byte[16 * 1024];
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();
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
		Long time = System.nanoTime();
		shotAOEEnemy = loadImage("shootplayeraoe", 80, 80);
		shotEnemy = loadArray1D(5, "shootplayer", 35, 15);
		shotAOEPlayer = loadImage("shootenemyaoe", 80, 80);
		shotPlayer = loadArray1D(5, "shootenemy", 35, 15);
		Log.e("myid", "shots".concat(Long.toString((System.nanoTime()-time)/100000)));
		
		time = System.nanoTime();
		createMarker = loadImage("createswordsman", 44, 66);
		backDrop = loadImage("leveltile1", 400, 400);
		isSelected = loadImage("icon_isselected", 60, 60);
		Log.e("myid", "misc".concat(Long.toString((System.nanoTime()-time)/100000)));
		
		time = System.nanoTime();
		Bitmap sheet = loadImage("sprite_enemies", 4460, 240);
		enemyImages[0] = new Bitmap[55];
		enemyImages[3] = new Bitmap[55];
		for(int i = 0; i < 55; i++)
		{
			enemyImages[0][i] = Bitmap.createBitmap(sheet, i*80, 0, 110, 70);
			enemyImages[3][i] = Bitmap.createBitmap(sheet, i*80, 70, 110, 70);
		}
		enemyImages[1] = new Bitmap[49];
		enemyImages[4] = new Bitmap[49];
		for(int i = 0; i < 49; i++)
		{
			enemyImages[1][i] = Bitmap.createBitmap(sheet, i*60, 140, 80, 50);
			enemyImages[4][i] = Bitmap.createBitmap(sheet, i*60, 190, 80, 50);
		}
		enemyImages[2] = new Bitmap[31];
		enemyImages[5] = new Bitmap[31];
		for(int i = 0; i < 31; i++)
		{
			enemyImages[2][i] = Bitmap.createBitmap(sheet, i*30+2980, 140, 30, 34);
			enemyImages[5][i] = Bitmap.createBitmap(sheet, i*30+2980, 174, 30, 34);
		}
		Log.e("myid", "hm".concat(Long.toString((System.nanoTime()-time)/100000)));
		
		time = System.nanoTime();
		double w = phoneWidth;
		double h = phoneHeight;
		menu_top = loadImage("menu_top", (int)(w), (int)(h/5));
		menu_side = loadImage("menu_side", (int)(w/2 - h/10), (int)(h*4/5));
		menu_middle = loadImage("menu_middle", (int)(h/5), (int)(h*4/5));
		Log.e("myid", "menu".concat(Long.toString((System.nanoTime()-time)/100000)));
		
		time = System.nanoTime();
		icon_back = loadImage("icon_back", 150, 150);
		icon_delete = loadImage("icon_delete", 150, 150);
		icon_cancel = loadImage("icon_cancel", 150, 150);
		icon_menu = loadImage("icon_menu", 150, 150);
		Log.e("myid", "icons".concat(Long.toString((System.nanoTime()-time)/100000)));
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
	/**
	 * recycles desired array of images
	 * @param length length of array
	 * @param array array to recycle
	 */
	protected void recycleArray(Bitmap[] array)
	{
		int length = array.length;
		for(int i = 0; i < length; i++)
		{
			if(array[i] != null)
			{
				array[i].recycle();
				array[i] = null;
			}
		}
	}
	/**
	 * recycles desired array of images
	 * @param length length of array
	 * @param array array to recycle
	 */
	protected void recycleArray(Bitmap[][] array)
	{
		int length = array.length;
		for(int i = 0; i < length; i++)
		{
			if(array[i] != null)
			{
				int length2 = array[i].length;
				for(int j = 0; j < length2; j++)
				{
					if(array[i][j] != null)
					{
						array[i][j].recycle();
						array[i][j] = null;
					}
				}
			}
		}
	}
	/**
	 * Loads and resizes array of images
	 * @param length Length of array to load
	 * @param start Starting string which precedes array index to match resource name
	 * @param width End width of image being loaded
	 * @param height End height of image being loaded
	 */
	public Bitmap[] loadArray1D(int length, String start, int width, int height)
	{
		Bitmap sheet = loadImage(start, width*length, height);
		Bitmap[] newArray = new Bitmap[length];
		for(int i = 0; i < length; i++)
		{
			newArray[i] = Bitmap.createBitmap(sheet, i*width, 0, width, height);
		}
		return newArray;
	}
	/**
	 * Adds 0's before string to make it four digits long
	 * Animations done in flash which when exporting .png sequence end file name with four character number
	 * @return Returns four character version of number
	 */
	protected String correctDigits(int start, int digits)
	{
		String end = Integer.toString(start);
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
	/**
	 * Loads image of name given from resources and scales to specified width and height
	 * @return Returns bitmap loaded and resized
	 */
	public Bitmap loadImage(String imageName, int width, int height)
	{
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		//return BitmapFactory.decodeResource(res, imageNumber, opts);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), width, height, false);
	}
}