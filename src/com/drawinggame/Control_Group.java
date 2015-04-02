package com.drawinggame;

import java.util.ArrayList;

import android.util.Log;

import lx.interaction.dollar.Point;

public final class Control_Group extends Control_Main
{
	protected ArrayList<Enemy> humans;
	protected ArrayList<Enemy_Archer> archers = new ArrayList<Enemy_Archer>();
	protected ArrayList<Enemy_Mage> mages = new ArrayList<Enemy_Mage>();
	protected ArrayList<Enemy_Sheild> sheilds = new ArrayList<Enemy_Sheild>();
	protected int groupRotation;
	protected double groupX;
	protected double groupY;
	protected int saveMoveX;
	protected int saveMoveY; //how much to move after you are organized
	private static double r2d = 180/Math.PI;
	private boolean organizingToMove = false;
	protected byte currentForm = 0; //0 is norm, 1 is standGround, 2 is V or attack
	public Control_Group(Controller controlSet, ArrayList<Enemy> humansSet)
	{
		super(controlSet);
		humans = humansSet;
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).setController(this);
			if(humans.get(i).humanType==0)
			{
				sheilds.add((Enemy_Sheild) humans.get(i));
			} else if(humans.get(i).humanType==1)
			{
				archers.add((Enemy_Archer) humans.get(i));
			} else if(humans.get(i).humanType==2)
			{
				mages.add((Enemy_Mage) humans.get(i));
			}
		}
		groupX = averageX();
		groupY = averageY();
		groupRotation = averageRotation();
		formUp(groupRotation, groupX, groupY);
		isGroup = true;
	}
	protected void frameCall()
	{
		if(organizingToMove)
		{
			boolean doneOrganizing = true;
			for(int i = 0; i < humans.size(); i ++)
			{
				if(humans.get(i).hasDestination) doneOrganizing = false;
			}
			if(doneOrganizing)
			{
				startMovement();
			}
		}
		for(int i = 0; i < archers.size(); i ++) archerFrame(archers.get(i));
		for(int i = 0; i < mages.size(); i ++) mageFrame(mages.get(i));
		for(int i = 0; i < sheilds.size(); i ++) sheildFrame(sheilds.get(i));
		groupX = averageX();
		groupY = averageY();
		groupRotation = averageRotation();
	}
	protected void archerFrame(Enemy_Archer archer)
	{
		if(archer.action.equals("Shoot"))
		{
		} else if(archer.action.equals("Move"))
		{
		} else
		{
			if(archer.hasDestination)
			{
				archer.runTowardsDestination();
			}
		}
	}
	protected void mageFrame(Enemy_Mage mage)
	{
		if(mage.action.equals("Roll"))
		{
		} else if(mage.action.equals("Move"))
		{
		} else
		{
			if(mage.hasDestination)
			{
				mage.runTowardsDestination();
			}
		}
	}
	protected void sheildFrame(Enemy_Sheild sheild)
	{
		if(sheild.action.equals("Melee"))
		{
		} else if(sheild.action.equals("Sheild"))
		{
		} else if(sheild.action.equals("Move"))
		{
		} else
		{
			if(sheild.hasDestination)
			{
				sheild.runTowardsDestination();
			}
		}
	}
	/**
	 * forms this group around given x, y, rotation
	 * @param rotation
	 * @param newX
	 * @param newY
	 */
	protected void formUp(double rotation, double newX, double newY)
	{
		double bestScore = Double.MAX_VALUE;
		int bestRows = 0;
		int archerRows = 0, mageRows = 0, sheildRows = 0;
		int archersPerRow = 0, magesPerRow = 0, sheildsPerRow = 0;
		for(double i = 1; i <= humans.size(); i++)
		{
			double rows = Math.ceil(((double)archers.size())/i) + Math.ceil(((double)mages.size())/i) + Math.ceil(((double)sheilds.size())/i);
			double score = 0.5*i + rows;
			if(score < bestScore)
			{
				bestRows = (int)rows;
				bestScore = score;
				archerRows = (int) Math.ceil(((double)archers.size())/i);
				mageRows = (int) Math.ceil(((double)mages.size())/i);
				sheildRows = (int) Math.ceil(((double)sheilds.size())/i);
			}					//i is best number of people in a row
		}
		int spacing = 30;
		archersPerRow = (int) Math.ceil(((double)archers.size())/archerRows);
		magesPerRow = (int) Math.ceil(((double)mages.size())/mageRows);
		sheildsPerRow = (int) Math.ceil(((double)sheilds.size())/sheildRows);
		Log.e("myid", "archerRows: ".concat(   Integer.toString(archerRows)   ));
		Log.e("myid", "mageRows: ".concat(   Integer.toString(mageRows)   ));
		Log.e("myid", "sheildRows: ".concat(   Integer.toString(sheildRows)   ));
		Log.e("myid", "archersPerRow: ".concat(   Integer.toString(archersPerRow)   ));
		Log.e("myid", "magesPerRow: ".concat(   Integer.toString(magesPerRow)   ));
		Log.e("myid", "sheildsPerRow: ".concat(   Integer.toString(sheildsPerRow)   ));
		
		
		ArrayList<Point> sheildPositions = new ArrayList<Point>();
		ArrayList<Point> archerPositions = new ArrayList<Point>();
		ArrayList<Point> magePositions = new ArrayList<Point>();
		double pX = (spacing/2) * (bestRows-1);
		double pY;
		double cosT = Math.cos(rotation/r2d);
		double sinT = Math.sin(rotation/r2d);
		for(int i = 0; i < sheildRows; i++)
		{
			pY = -(spacing/2) * (sheildsPerRow-1);
			if(i == sheildRows-1) pY += (spacing/2) * (sheildsPerRow*sheildRows - sheilds.size());
			for(int j = 0; j < sheildsPerRow; j++)
			{
				sheildPositions.add(new Point(cosT*pX - sinT*pY + groupX, sinT*pX + cosT*pY + groupY));
				pY += spacing;
			}
			pX -= spacing;
		}
		for(int i = 0; i < archerRows; i++)
		{
			pY = -(spacing/2) * (archersPerRow-1);
			if(i == archerRows-1) pY += (spacing/2) * (archersPerRow*archerRows - archers.size());
			for(int j = 0; j < archersPerRow; j++)
			{
				archerPositions.add(new Point(cosT*pX - sinT*pY + groupX, sinT*pX + cosT*pY + groupY));
				pY += spacing;
			}
			pX -= spacing;
		}
		for(int i = 0; i < mageRows; i++)
		{
			pY = -(spacing/2) * (magesPerRow-1);
			if(i == mageRows-1) pY += (spacing/2) * (magesPerRow*mageRows - mages.size());
			for(int j = 0; j < magesPerRow; j++)
			{
				magePositions.add(new Point(cosT*pX - sinT*pY + groupX, sinT*pX + cosT*pY + groupY));
				pY += spacing;
			}
			pX -= spacing;
		}
		startOrganizing(sheildPositions, archerPositions, magePositions);
		saveMoveX = (int)(newX-groupX);
		saveMoveY = (int)(newY-groupY);
	}
	protected void startOrganizing(ArrayList<Point> sheildPositions, ArrayList<Point> archerPositions, ArrayList<Point> magePositions)
	{
		for(int i = 0; i < sheilds.size(); i++)
		{
			sheilds.get(i).hasDestination = true;
			sheilds.get(i).destinationX = (int)sheildPositions.get(i).X;
			sheilds.get(i).destinationY = (int)sheildPositions.get(i).Y;
		}
		for(int i = 0; i < archers.size(); i++)
		{
			archers.get(i).hasDestination = true;
			archers.get(i).destinationX = (int)archerPositions.get(i).X;
			archers.get(i).destinationY = (int)archerPositions.get(i).Y;
		}
		for(int i = 0; i < mages.size(); i++)
		{
			mages.get(i).hasDestination = true;
			mages.get(i).destinationX = (int)magePositions.get(i).X;
			mages.get(i).destinationY = (int)magePositions.get(i).Y;
		}
		organizingToMove = true;
	}
	protected void startMovement()
	{
		for(int i = 0; i < sheilds.size(); i++)
		{
			sheilds.get(i).hasDestination = true;
			sheilds.get(i).destinationX += saveMoveX;
			sheilds.get(i).destinationY += saveMoveY;
		}
		for(int i = 0; i < archers.size(); i++)
		{
			archers.get(i).hasDestination = true;
			archers.get(i).destinationX += saveMoveX;
			archers.get(i).destinationY += saveMoveY;
		}
		for(int i = 0; i < mages.size(); i++)
		{
			mages.get(i).hasDestination = true;
			mages.get(i).destinationX += saveMoveX;
			mages.get(i).destinationY += saveMoveY;
		}
		organizingToMove = false;
	}
	protected double averageX()
	{
		double sum = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sum += humans.get(i).x;
		}
		return sum/humans.size();
	}
	protected double averageY()
	{
		double sum = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sum += humans.get(i).y;
		}
		return sum/humans.size();
	}
	protected int averageRotation()
	{
		int sum = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sum += humans.get(i).rotation;
		}
		return sum/humans.size();
	}
	@Override
	protected void setDestination(double destX, double destY)
	{
		formUp(r2d*Math.atan2(destY-groupY, destX-groupX), destX, destY);
		/*human.hasDestination = true;
		human.destinationX = (int)x;
		human.destinationY = (int)y;*/
	}
	@Override
	protected void removeHuman(EnemyShell target)
	{
		if(target.humanType==0)
		{
			sheilds.remove(target);
		} else if(target.humanType==1)
		{
			archers.remove(target);
		} else if(target.humanType==2)
		{
			mages.remove(target);
		}
		humans.remove(target);
		if(humans.size() == 1)
		{
			humans.get(0).selectSingle();
			deleted = true;
		}
	}
}