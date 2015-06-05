package com.drawinggame;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jgzuke on 15-05-18.
 */
public class ImageDrawer {
    private TextureLibrary textureLibrary;
    private FloatBuffer vertexBuffer;	// buffer holding the vertices
    private float width = 2.0f;
    private float height = 2.0f;

    private float vertices[] = {
            -width, -height,  0.0f,		// V1 - bottom left
            -width,  height,  0.0f,		// V2 - top left
            width, -height,  0.0f,		// V3 - bottom right
            width,  height,  0.0f			// V4 - top right
    };

    private FloatBuffer textureBuffer;
    private float texture[] = {
            // Mapping coordinates for the vertices
            0.0f, 1.0f,		// top left		(V2)
            0.0f, 0.0f,		// bottom left	(V1)
            1.0f, 1.0f,		// top right	(V4)
            1.0f, 0.0f		// bottom right	(V3)
    };
    private FloatBuffer textureBufferMage;

    private float textureMage[] = {
            0.0f, 0.5f,		// top left		(V2)
            0.0f, 0.0f,		// bottom left	(V1)
            1.0f/32, 0.5f,		// top right	(V4)
            1.0f/32, 0.0f		// bottom right	(V3)
    };

    /** The texture pointer */
    private int[] textures = new int[1];
    public ImageDrawer(TextureLibrary textureLibrarySet)
    {
        textureLibrary = textureLibrarySet;
        vertexBuffer = setFloatBuffer(vertices);
        textureBuffer = setFloatBuffer(texture);
        textureBufferMage = setFloatBuffer(textureMage);
    }

    private FloatBuffer setFloatBuffer(float [] array) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer toFill = byteBuffer.asFloatBuffer();
        toFill.put(array);
        toFill.position(0);
        return toFill;
    }



    /**
     * Load the texture for the square
     * @param gl
     */
    public void loadGLTexture(GL10 gl, Bitmap image) {
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
    }

    /**
     * Load the texture for the square
     * @param gl
     */
    public void loadGLTexture(GL10 gl, Sprite s) {
        Bitmap image = textureLibrary.mageImages;

        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
    }

    /** The draw method for the square with the GL context */
    public void draw(GL10 gl, Bitmap image) {
        loadGLTexture(gl, image);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
    }

    /** The draw method for the square with the GL context */
    public void draw(GL10 gl, Sprite s) {
        loadGLTexture(gl, s);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
    }
}
