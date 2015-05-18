package com.drawinggame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jgzuke on 15-05-15.
 */
public class TextureLibrary {
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
    public TextureLibrary(Context contextSet, Controller controlSet, int pWidth, int pHeight)
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
        shotAOEEnemy = loadImage("shootplayeraoe", 80, 80);
        shotEnemy = loadArray1D(5, "shootplayer", 35, 15);
        shotAOEPlayer = loadImage("shootenemyaoe", 80, 80);
        shotPlayer = loadArray1D(5, "shootenemy", 35, 15);
        createMarker = loadImage("createswordsman", 44, 66);
        backDrop = loadImage("leveltile1", 400, 400);
        isSelected = loadImage("icon_isselected", 60, 60);
        Bitmap sheet = loadImage("sprite_enemies", 6050, 240);
        enemyImages[0] = new Bitmap[55];
        enemyImages[3] = new Bitmap[55];
        for(int i = 0; i < 55; i++)
        {
            enemyImages[0][i] = Bitmap.createBitmap(sheet, i*110, 0, 110, 70);
            enemyImages[3][i] = Bitmap.createBitmap(sheet, i*110, 70, 110, 70);
        }
        enemyImages[1] = new Bitmap[49];
        enemyImages[4] = new Bitmap[49];
        for(int i = 0; i < 49; i++)
        {
            enemyImages[1][i] = Bitmap.createBitmap(sheet, i*80, 140, 80, 50);
            enemyImages[4][i] = Bitmap.createBitmap(sheet, i*80, 190, 80, 50);
        }
        enemyImages[2] = new Bitmap[31];
        enemyImages[5] = new Bitmap[31];
        for(int i = 0; i < 31; i++)
        {
            enemyImages[2][i] = Bitmap.createBitmap(sheet, i*30+3920, 140, 30, 34);
            enemyImages[5][i] = Bitmap.createBitmap(sheet, i*30+3920, 174, 30, 34);
        }
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
     * Loads and resizes array of images
     * @param length Length of array to load
     * @param start Starting string which precedes array index to match resource name
     * @param width End width of image being loaded
     * @param height End height of image being loaded
     */
    public Bitmap[] loadArray1D(int length, String start, int width, int height)
    {
        Bitmap sheet = loadImage(start, width * length, height);
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
