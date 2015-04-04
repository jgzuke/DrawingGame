/**
 * Enemies and player, regains health, provides variables and universal getHit method
 */
package com.drawinggame;

import android.graphics.Bitmap;

import com.spritelib.Sprite;

public final class CreationMarker extends Sprite
{
	public CreationMarker(double X, double Y, Bitmap Image, SpriteController controlSet) {
		super(X, Y, 0, Image);
		control = controlSet;
	}
	protected int alpha = 255;
	private SpriteController control;
	/**
	 * Regains health, ends walk animation, plays animation
	 */
	@
	Override
	protected void frameCall()
	{
		alpha -=5;
		if (alpha < 10)
		{
			control.creationMarkers.remove(this);
		}
	}
	public int getAlpha() {
		return alpha;
	}
}