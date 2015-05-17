package com.drawinggame;

import android.graphics.Bitmap;

import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer extends GLRenderer {
    private Square square;
    private int angle;
    private int height = 80;
    int[] textures = new int[10];
    float textureCoordinates[] = {0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f };
    public MyGLRenderer() {
        square = new Square();
    }

    @Override
    public void onCreate(int width, int height, boolean contextLost) {

    }

    @Override
    public void onDrawFrame(GL10 gl, boolean firstDraw) {
        if(firstDraw) {
            gl.glGenTextures(1, textures, 0);
        }
        startDraw(gl);
        gl.glTranslatef(0, 0, -height);

        for(Sprite s: spriteController.allies) {
            drawSprite(gl, s);
        }
        for(Sprite s: spriteController.enemies) {
            drawSprite(gl, s);
        }

        endDraw(gl);
    }

    public void drawSprite(GL10 gl, Sprite s)
    {
        float x = (float) (s.x - levelController.levelWidth/2);
        float y = (float) (s.y - levelController.levelWidth/2);
        float ratio = 100.0f/levelController.levelWidth;
        drawSprite(gl, x * ratio, y * ratio, (float) s.rotation, s.image);
    }

    public void drawSprite(GL10 gl, float x, float y, float r, Bitmap image)
    {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0);
        gl.glRotatef(r, 0, 0, -height);
        square.draw(gl);
        gl.glPopMatrix();
    }
}
