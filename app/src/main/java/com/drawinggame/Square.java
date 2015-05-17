package com.drawinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
	// Our vertices.
	float width = 1.0f;
	private float vertices[] = {
			-width,  width, 0.0f,  // 0, Top Left
			-width, -width, 0.0f,  // 1, Bottom Left
			width, -width, 0.0f,  // 2, Bottom Right
			width,  width, 0.0f,  // 3, Top Right
		};
	
	// The order we like to connect them.
	private short[] indices = { 0, 1, 2, 0, 2, 3 };
	
	// Our vertex buffer.
	private FloatBuffer vertexBuffer;

    private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
    private float texture[] = {
            0.0f, 1.0f,     // top left     (V2)
            0.0f, 0.0f,     // bottom left  (V1)
            1.0f, 1.0f,     // top right    (V4)
            1.0f, 0.0f      // bottom right (V3)
    };


    // Our index buffer.
	private ShortBuffer indexBuffer;
	
	public Square() {
		// a float is 4 bytes, therefore we multiply the number if 
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// short is 2 bytes, therefore we multiply the number if 
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);

        ibb = ByteBuffer.allocateDirect(texture.length * 4);
        ibb.order(ByteOrder.nativeOrder());
        textureBuffer = ibb.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    private int[] textures = new int[1];
    public void loadGLTexture(GL10 gl, Bitmap image) {
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
    }
    /**
     * This function draws our square on screen.
     * @param gl
     */
    public void draw(GL10 gl, Bitmap image) {
        loadGLTexture(gl, image);
        draw(gl, textures[0]);
    }

    /**
	 * This function draws our square on screen.
	 * @param gl
	 */
	public void draw(GL10 gl, int texture) {
        //gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
	}
    /**
     * This function draws our square on screen.
     * @param gl
     */
    public void draw(GL10 gl) {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
    }
}
