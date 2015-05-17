package com.drawinggame;

/**
 * Created by jgzuke on 15-05-15.
 */
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;

public abstract class GLRenderer implements Renderer {

    private boolean mFirstDraw;
    private boolean mSurfaceCreated;
    private int mWidth;
    private int mHeight;
    private long mLastTime;
    private int mFPS;
    protected SpriteController spriteController;
    protected LevelController levelController;
    protected TextureLibrary textureLibrary;

    public GLRenderer() {
        mFirstDraw = true;
        mSurfaceCreated = false;
        mWidth = -1;
        mHeight = -1;
        mLastTime = System.currentTimeMillis();
        mFPS = 0;
    }
    public void setControllers (Controller control) {
        spriteController = control.spriteController;
        levelController = control.levelController;
        textureLibrary = control.textureLibrary;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mSurfaceCreated = true;
        mWidth = -1;
        mHeight = -1;
        gl.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        onCreate(mWidth, mHeight, mSurfaceCreated);
        mSurfaceCreated = false;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        onDrawFrame(gl, mFirstDraw);
        if (mFirstDraw) {
            mFirstDraw = false;
        }
    }

    public int getFPS() {
        return mFPS;
    }


    protected void startDraw(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glLoadIdentity();

        /*gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);*/
    }

    protected void endDraw(GL10 gl)
    {
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
    }

    public abstract void onCreate(int width, int height, boolean contextLost);
    public abstract void onDrawFrame(GL10 gl, boolean firstDraw);
}
