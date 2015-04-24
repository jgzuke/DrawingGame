/**
 * Enemies and player, regains health, provides variables and universal getHit method
 */
package com.drawinggame;

import android.graphics.Bitmap;

abstract public class Human extends Sprite
{
	public Human(double X, double Y, double Rotation, int Frame,
			boolean IsVideo, boolean Playing, Bitmap Image, boolean isOnPlayersTeam) {
		super(X, Y, Rotation, Frame, IsVideo, Playing, Image);
		onPlayersTeam = isOnPlayersTeam;
	}
	public Human(double X, double Y, int Width, int Height, double Rotation,
			int Frame, boolean IsVideo, boolean Playing, Bitmap Image, boolean isOnPlayersTeam) {
		super(X, Y, Width, Height, Rotation, Frame, IsVideo, Playing, Image);
		onPlayersTeam = isOnPlayersTeam;
	}
	protected int hp;
	protected int hpMax;
	protected double r2d = 180 / Math.PI;
	protected double rads;
	protected double speedCur = 3.5;
	protected boolean onPlayersTeam;
	protected Controller control;
	/**
	 * Regains health, ends walk animation, plays animation
	 */
	@
	Override
	protected void frameCall()
	{
		if(hp > hpMax)
		{
			hp = hpMax;
		}
	}
}