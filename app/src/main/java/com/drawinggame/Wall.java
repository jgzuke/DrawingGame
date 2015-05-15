/**
 * variables for wall objects
 */
package com.drawinggame;
abstract public class Wall
{
	protected static Controller control;
	protected int playerRollWidth = 5;
	protected int humanWidth = 10;
	protected boolean tall;
	/**
	 * what happens every frame for walls interacting with players and enemies
	 */
	abstract protected void frameCall();
}