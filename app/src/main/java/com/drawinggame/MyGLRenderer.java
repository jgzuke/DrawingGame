package com.drawinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements Renderer {
    private int angle;
    private int height = 100;
    private Context context;
    private SpriteController spriteController;
    private LevelController levelController;
    private TextureLibrary textureLibrary;

    public MyGLRenderer(Context contextSet) {
        context = contextSet;
    }

    public void setControllers (Controller control) {
        spriteController = control.spriteController;
        levelController = control.levelController;
        textureLibrary = control.textureLibrary;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        startDraw(gl);
        gl.glTranslatef(0, 0, -height);
        for(Sprite s: spriteController.allies) {
            drawSprite(gl, s);
        }
        for(Sprite s: spriteController.enemies) {
            drawSprite(gl, s);
        }
        drawSprite(gl, 0, 0, 0, textureLibrary.createMarker);
        endDraw(gl);
    }

    protected void startDraw(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

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
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    public void drawSprite(GL10 gl, Sprite s)
    {
        float x = (float) (s.x - levelController.levelWidth/2);
        float y = (float) (s.y - levelController.levelWidth/2);
        float ratio = 80.0f/levelController.levelWidth;
        drawSprite(gl, x * ratio, y * ratio, (float) s.rotation, s.image);
    }

    public void drawSprite(GL10 gl, float x, float y, float r, Bitmap image)
    {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0);
        gl.glRotatef(r, 0, 0, -height);
        textureLibrary.draw(gl, image);
        gl.glPopMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }
        gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        gl.glLoadIdentity(); 					//Reset The Projection Matrix
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        gl.glLoadIdentity(); 					//Reset The Modelview Matrix
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
        gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
        gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }
}
