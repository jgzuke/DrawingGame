
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
	private int spacing = 10;
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
		g.drawBitmap(imageLibrary.icon_menu, 0, 0, paint);
		if(control.selected != null)g.drawBitmap(imageLibrary.icon_cancel, 150, 0, paint);
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
	protected boolean [] drawSection = new boolean[4]; // top left middle right
	Bitmap saveCanvas = null;
	protected void drawPaused(Canvas h)
	{
		if(saveCanvas == null)
		{
			saveCanvas = Bitmap.createBitmap(phoneWidth, phoneHeight, Config.ARGB_8888);
		}
		Canvas g = new Canvas(saveCanvas);
		if(control.gestureDetector.shouldDrawGesture())
		{
			for(int i = 0; i < 4; i++) drawSection[i] = true;
		}
		if(drawSection[1])
		{
			drawPaused_Left(g);
			drawSection[1] = false;
		}
		if(drawSection[3])
		{
			drawPaused_Right(g);
			drawSection[3] = false;
		}
		control.gestureDetector.drawGestures(g);
		if(drawSection[2])
		{
			drawPaused_Middle(g);
			drawSection[2] = false;
		}
		if(drawSection[0])
		{
			drawPaused_Top(g);
			drawSection[0] = false;
		}
		h.drawBitmap(saveCanvas, 0, 0, paint);
	}
	private void drawPaused_Top(Canvas g)
	{
		// background and button
		g.drawBitmap(imageLibrary.menu_top, 0, 0, paint);
		g.drawBitmap(imageLibrary.icon_back, 0, 0, paint);
	}
	private void drawPaused_Left(Canvas g)
	{
		double uHeight = unitHeight*10;
		// background
		g.drawBitmap(imageLibrary.menu_side, 0, (int)(unitHeight*20), paint);
		
		// gesture
		control.gestureDetector.drawGesturePausedBig(g);
	}
	private void drawPaused_Middle(Canvas g)
	{
		int selected = control.selectionSpriteController.selected;
		
		double uHeight = unitHeight*10;
		// background
		g.drawBitmap(imageLibrary.menu_middle, (int)(unitWidth*50-uHeight), (int)(unitHeight*20), paint);
		
		// gestures small
		control.gestureDetector.drawGesturePausedSmall(g);
		
		// mana prices
		int leftS = (int)(unitWidth*50+uHeight-spacing*3);
		int rightS = (int)(unitWidth*50+uHeight-spacing);
		paint.setColor(manaColor);
		paint.setStyle(Style.FILL);
		for(int i = 0; i < 4; i++)
		{
			double bottomS = unitHeight*20*(i+2)-spacing;
			double ratioS = control.selectionSpriteController.getGroupPrice(i)/1000;
			double topS = bottomS - ratioS*(unitHeight*20 - 2*spacing);
			g.drawRect(leftS, (int)(topS), rightS, (int)(bottomS), paint);
		}
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		for(int i = 0; i < 4; i++)
		{
			double topS = unitHeight*20*(i+1)+spacing;
			double bottomS = unitHeight*20*(i+2)-spacing;
			g.drawRect(leftS, (int)(topS), rightS, (int)(bottomS), paint);
		}
		
		// selection square
		paint.setColor(Color.BLUE);
		g.drawRect((int)(unitWidth*50-uHeight), (int)(unitHeight*20*(selected+1)), (int)(unitWidth*50+uHeight), (int)(unitHeight*20*(selected+2)), paint);
		
		// small gestures
		
	}
	private void drawPaused_Right(Canvas g)
	{
		double uHeight = unitHeight*10;
		// brackground and button
		g.drawBitmap(imageLibrary.menu_side, (int)(unitWidth*50+uHeight), (int)(unitHeight*20), paint);
		g.drawBitmap(imageLibrary.icon_delete, phoneWidth-150, (int)(unitHeight*20), paint);

		// enemies
		control.selectionSpriteController.drawSprites(g, paint, imageLibrary, unitWidth, unitHeight);
		
		// mana price
		double top = unitHeight*20+spacing;
		double bottom = unitHeight*100-spacing;
		double left = unitWidth*50+uHeight+spacing;
		double right = unitWidth*50+uHeight+spacing*4;
		double full = control.selectionSpriteController.getGroupPrice()/1000;
		double manaTop = bottom - full*(bottom-top);
		paint.setColor(manaColor);
		paint.setStyle(Style.FILL);
		g.drawRect((int)left, (int)manaTop, (int)right, (int)bottom, paint);
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		g.drawRect((int)left, (int)top, (int)right, (int)bottom, paint);
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