/**
 * behavior for all sprites
 */
package com.drawinggame;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

abstract public class Sprite
{
	public double x;
	public double y;
	public double rotation;
	public int width;
	public int height;
	public boolean isVideo = false;
	public int frame = 0;
	public boolean playing = false;
	public Bitmap image = null;
	protected static Controller control;

    private float vertices[] = {
            -1.0f,  1.0f, 0.0f,  // 0, Top Left
            -1.0f, -1.0f, 0.0f,  // 1, Bottom Left
            1.0f, -1.0f, 0.0f,  // 2, Bottom Right
            1.0f,  1.0f, 0.0f,  // 3, Top Right
    };
    private short[] indices = { 0, 1, 2, 0, 2, 3 };
    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
	
	public Sprite(double X, double Y, int Width, int Height, double Rotation,
	int Frame, boolean IsVideo, boolean Playing, Bitmap Image)
	{
		x=X;
		y=Y;
		width=Width;
		height=Height;
		rotation=Rotation;
		frame=Frame;
		isVideo=IsVideo;
		playing=Playing;
		image = Image;
        setSize();
	}
	public Sprite(double X, double Y, double Rotation,
		int Frame, boolean IsVideo, boolean Playing, Bitmap Image)
	{
		x=X;
		y=Y;
		rotation=Rotation;
		frame=Frame;
		isVideo=IsVideo;
		playing=Playing;
		image = Image;
		sizeImage();
        setSize();
	}
	public Sprite(double X, double Y, int Width, int Height, double Rotation, Bitmap Image)
	{
		x=X;
		y=Y;
		width=Width;
		height=Height;
		rotation=Rotation;
		image = Image;
        setSize();
	}
	public Sprite(double X, double Y, double Rotation, Bitmap Image)
	{
		x=X;
		y=Y;
		rotation=Rotation;
		image = Image;
		sizeImage();
        setSize();
	}
    private void setSize()
    {
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
    }
	protected void swapImage(Bitmap newImage)
	{
		image = newImage;
	}
	protected void sizeImage()
	{
		if(image!=null)
		{
			width = image.getWidth();
			height = image.getHeight();
		}
	}
	/**
	 * called every frame, performs desired actions
	 */
	protected void frameCall()
	{
		if(isVideo&&playing) frame++;
	}
	/**
	 * draws sprite with OPEN GL
	 */
	protected void draw(GL10 gl)
	{
        // Counter-clockwise winding.
        gl.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        gl.glEnable(GL10.GL_CULL_FACE);
        // What faces to remove with the face culling.
        gl.glCullFace(GL10.GL_BACK);

        // Enabled the vertices buffer for writing and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                vertexBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // Disable face culling.
        gl.glDisable(GL10.GL_CULL_FACE);

        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -4);
	}
}