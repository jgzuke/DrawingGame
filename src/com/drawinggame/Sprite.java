/**
 * behavior for all sprites
 */
package com.drawinggame;

import android.graphics.Bitmap;
import android.util.Log;

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
	public boolean visible = true;
	protected static Controller control;
	//public boolean deleted = false;
	
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
	}
	public Sprite(double X, double Y, int Width, int Height, double Rotation, Bitmap Image)
	{
		x=X;
		y=Y;
		width=Width;
		height=Height;
		rotation=Rotation;
		image = Image;
	}
	public Sprite(double X, double Y, double Rotation, Bitmap Image)
	{
		x=X;
		y=Y;
		rotation=Rotation;
		image = Image;
		sizeImage();
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
}