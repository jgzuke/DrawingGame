package com.drawinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.util.ResourceBundle;

/**
 * Created by jgzuke on 15-05-15.
 */
public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;

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
    private int manaColor = Color.rgb(30, 0, 170);
    private int manaColorDark = Color.rgb(20, 0, 110);
    private int manaColorCover = Color.argb(50, 255, 255, 255);
    private TextureLibrary textureLibrary;
    private Controller control;
    private SpriteController spriteController;
    private LevelController levelController;
    private int grassGreen = Color.rgb(51, 102, 0);
    private Context context;
    protected boolean [] drawSection = new boolean[4]; // top left middle right
    Bitmap saveCanvas = null;

    public MyGLSurfaceView(Context context) {
        super(context);
        initialize(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context contextSet)
    {
        context = contextSet;
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
    }

    public void setControllers(Controller controlSet) {
        control = controlSet;
        textureLibrary = control.textureLibrary;
        spriteController = control.spriteController;
        levelController = control.levelController;
        mRenderer.setControllers(controlSet);
    }

    protected void frameCall()
    {
        //invalidate();
    }

    @ Override
    public void onPause()
    {
        super.onPause();
    }

    @ Override
    public void onResume()
    {
        super.onResume();
    }

    public void onCreate(int width, int height, boolean contextLost)
    {

    }
}
