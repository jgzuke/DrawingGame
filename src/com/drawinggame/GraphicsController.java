
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
 * @param frameCaller Calls objects and controls frameCalls
 */
package com.drawinggame;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import com.spritelib.Sprite;
public final class GraphicsController extends View
{
	protected double playScreenSize = 1;
	protected double playScreenSizeMax = 1;
	protected int mapXSlide = 0;
	protected int mapYSlide = 0;
	protected int curXShift;
	protected int curYShift;
	protected int phoneWidth;
	protected int phoneHeight;
	protected double unitWidth;
	protected double unitHeight;
	protected Paint paint = new Paint();
	protected Matrix rotateImages = new Matrix();
	private Rect aoeRect = new Rect();
	private int healthColor = Color.rgb(170, 0, 0);
	private int cooldownColor = Color.rgb(0, 0, 170);
	private int manaColor = Color.rgb(30, 0, 170);
	private ImageLibrary imageLibrary;
	private Controller control;
	private SpriteController spriteController;
	private LevelController levelController;
	private Context context;
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public GraphicsController(Controller c, ImageLibrary i, SpriteController s, WallController w, LevelController l, Context co, double [] dims)
	{
		super(co);
		control = c;
		imageLibrary = i;
		spriteController = s;
		levelController = l;
		context = co;
		setUpPaintStuff(dims); // dims = width/height
	}
	private void setUpPaintStuff(double [] dimensions) // 
	{
		setKeepScreenOn(true); // so screen doesnt shut off when game is left inactive
		phoneWidth = (int)dimensions[0];
		phoneHeight = (int)dimensions[1];
		paint.setAntiAlias(true);
		paint.setDither(true);
		phoneWidth = (int) dimensions[0];
		phoneHeight = (int) dimensions[1];
		unitWidth = (double)phoneWidth/100;
		unitHeight = (double)phoneHeight/100;
		playScreenSize = (double)levelController.levelWidth/phoneWidth;
		playScreenSizeMax = playScreenSize;
	}
	protected void frameCall()
	{
		invalidate();
	}
	/**
	 * fixes hp bar so it is on screen
	 * @param minX small x value of bar
	 * @param maxX large x value of bar
	 * @return offset so bar is on screen
	 */
	protected int fixXBoundsHpBar(int minX, int maxX)
	{
		int offset = 0;
		if(minX < 90) // IF TOO FAR LEFT FIX
		{
			offset = 90 - minX;
		}
		else if(maxX > 390) // IF TOO FAR RIGHT FIX
		{
			offset = 390 - maxX;
		}
		return offset;
	}
	/**
	 * fixes hp bar so it is on screen
	 * @param minY small y value of bar
	 * @param maxY large y value of bar
	 * @return offset so bar is on screen
	 */
	protected int fixYBoundsHpBar(int minY, int maxY)
	{
		int offset = 0;
		if(minY < 10) // IF TOO UP LEFT FIX
		{
			offset = 10 - minY;
		}
		else if(maxY > 310) // IF TOO FAR DOWN FIX
		{
			offset = 310 - maxY;
		}
		return offset;
	}
	/**
	 * returns distance squared between two objects
	 * @param x1 first x position
	 * @param y1 first y position
	 * @param x2 second x position
	 * @param y2 second y position
	 * @return distance between points squared
	 */
	protected double distSquared(double x1, double y1, double x2, double y2)
	{
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	/**
	 * draws the level and objects in it
	 * @return bitmap of level and objects
	 */
	protected void drawLevel(Canvas g)
	{
		//g.drawBitmap(imageLibrary.currentLevel, 0, 0, paint);
		spriteController.drawStructures(g, paint, imageLibrary);
		spriteController.drawSprites(g, paint, imageLibrary);
		//g.drawBitmap(imageLibrary.currentLevelTop, 0, 0, paint);
		spriteController.drawHealthBars(g, paint);
	}
	@Override
	protected void onDraw(Canvas g)
	{
		if(control.paused)
		{
			drawPaused(g);
		} else
		{
			drawUnpaused(g);
		}
	}
	protected void drawUnpaused(Canvas g)
	{
		paint.setColor(Color.rgb(51, 102, 0));
		paint.setStyle(Style.FILL);
		g.drawRect(0, 0, phoneWidth, phoneHeight, paint);
		g.save();
		g.scale((float)(1/playScreenSize), (float)(1/playScreenSize));
		g.translate(-mapXSlide, -mapYSlide);
		drawLevel(g);
		g.restore();
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		g.drawRect(0, 0, 150, 150, paint);
		if(!control.gestureDetector.selectType.equals("none")) // something selected
		{
			g.drawRect(150, 0, 300, 150, paint);
		}
		int spacing = 10;
		int top = phoneHeight-spacing;
		int manaHeight = phoneHeight-150-2*spacing;
		paint.setColor(manaColor);
		paint.setStyle(Style.FILL);
		g.drawRect(spacing, top-(int)(manaHeight*(double)control.spriteController.playerGameControl.mana/1000), spacing*4, top, paint);
		g.drawRect(phoneWidth-spacing*4, top-(int)(manaHeight*(double)control.spriteController.enemyGameControl.mana/1000), phoneWidth-spacing, top, paint);
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		g.drawRect(spacing, top-manaHeight, spacing*4, phoneHeight-spacing, paint);
		g.drawRect(phoneWidth-spacing*4, top-manaHeight, phoneWidth-spacing, phoneHeight-spacing, paint);
		control.gestureDetector.drawGestures(g);
	}
	protected void drawPaused(Canvas g)
	{
		int selected = control.gestureDetector.settingSelected;
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Style.FILL);
		g.drawRect(0, 0, phoneWidth, phoneHeight, paint);
		
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		double uHeight = unitHeight*10;
		g.drawRect(0, 0, (int)(unitWidth*100), (int)(unitHeight*20), paint);
		g.drawRect((int)(unitWidth*50-uHeight), (int)(unitHeight*20), (int)(unitWidth*50+uHeight), (int)(unitHeight*40), paint);
		g.drawRect((int)(unitWidth*50-uHeight), (int)(unitHeight*40), (int)(unitWidth*50+uHeight), (int)(unitHeight*60), paint);
		g.drawRect((int)(unitWidth*50-uHeight), (int)(unitHeight*60), (int)(unitWidth*50+uHeight), (int)(unitHeight*80), paint);
		g.drawRect((int)(unitWidth*50-uHeight), (int)(unitHeight*80), (int)(unitWidth*50+uHeight), (int)(unitHeight*100), paint);
		paint.setColor(Color.BLUE);
		g.drawRect((int)(unitWidth*50-uHeight), (int)(unitHeight*20*(selected+1)), (int)(unitWidth*50+uHeight), (int)(unitHeight*20*(selected+2)), paint);
		control.gestureDetector.drawGestureSelected(g);
		
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		g.drawRect(0, 0, 150, 150, paint);
		control.selectionSpriteController.drawSprites(g, paint, imageLibrary, unitWidth, unitHeight);
	}
	/**
	 * Starts warning label
	 * @param warning
	 */
	protected void startWarningImediate(String warning)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setMessage(warning);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawBitmapRotated(Sprite sprite, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-sprite.width / 2, -sprite.height / 2);
		rotateImages.postRotate((float) sprite.rotation);
		rotateImages.postTranslate((float) sprite.x, (float) sprite.y);
		g.drawBitmap(sprite.image, rotateImages, paint);
		sprite = null;
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and only draws object if it is in view
	 */
	protected void drawBitmapLevel(Bitmap picture, int x, int y, Canvas g)
	{
		/*if(inView(x, y, picture.getWidth(), picture.getHeight()))*/ g.drawBitmap(picture, x, y, paint);
	}
}