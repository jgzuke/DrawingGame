package com.drawinggame;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import lx.interaction.dollar.Point;

public final class Control_Group extends Control_Main
{
	protected ArrayList<Enemy> humans;
	protected ArrayList<Enemy_Archer> archers = new ArrayList<Enemy_Archer>();
	protected ArrayList<Enemy_Mage> mages = new ArrayList<Enemy_Mage>();
	protected ArrayList<Enemy_Sheild> sheilds = new ArrayList<Enemy_Sheild>();
	protected int groupRotationAfterOrganize;
	protected int groupRotation;
	protected double groupX;
	protected double groupY;
	private static double r2d = 180/Math.PI;
	private boolean hasDestination = false;
	private boolean hasLostMembers = false;
	private int layoutType = 0;
	private double destX;
	private double destY;
	private boolean organizing = false;
	protected byte currentForm = 0; //0 is norm, 1 is standGround, 2 is V or attack
	private static double spacing = 30;
	private static double spacingSlanted = Math.sqrt(2)*30/2;
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
		Log.e("myid", "test12");
		formUp();
		isGroup = true;
	}
	protected void frameCall()
	{
		if(organizing)
		{
			boolean doneOrganizing = true;
			for(int i = 0; i < humans.size(); i ++)
			{
				if(humans.get(i).hasDestination) doneOrganizing = false;
			}
			if(doneOrganizing)
			{
				if(hasDestination)
				{
					startMovement();
				} else
				{
					turnAfterOrganize();
				}
				organizing = false;
			}
		}
		for(int i = 0; i < archers.size(); i ++) archerFrame(archers.get(i));
		for(int i = 0; i < mages.size(); i ++) mageFrame(mages.get(i));
		for(int i = 0; i < sheilds.size(); i ++) sheildFrame(sheilds.get(i));
		groupX = averageX();
		groupY = averageY();
		groupRotation = averageRotation();
		if(hasDestination)
		{
			if(Math.sqrt(Math.pow(groupX-destX, 2)+Math.pow(groupY-destY, 2)) < 30) // reached destination
			{
				hasDestination = false;
			}
		}
		if(hasLostMembers)
		{
			hasLostMembers = false;
			formUp();
		}
	}
	protected void formUp()
	{
		formUp(groupRotation, groupX, groupY);
	}
	/**
	 * forms this group around given x, y, rotation
	 * @param rotation
	 * @param newX
	 * @param newY
	 */
	protected void formUp(double rotation, double newX, double newY)
	{
		Log.e("myid", "test13");
		organizing = true;
		groupRotationAfterOrganize = (int) rotation;
		if(layoutType == 0)
		{
			setGroupLayoutNormal(rotation);
		} else if(layoutType == 1)
		{
			setGroupLayoutAttack(rotation);
		} else if(layoutType == 2)
		{
			setGroupLayoutDefend(rotation);
		}
	}
	private void setGroupLayoutNormal(double rotation)
	{
		Log.e("myid", "test14");
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
		archersPerRow = (int) Math.ceil(((double)archers.size())/archerRows);
		magesPerRow = (int) Math.ceil(((double)mages.size())/mageRows);
		sheildsPerRow = (int) Math.ceil(((double)sheilds.size())/sheildRows);
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
				sheildPositions.add(pointAtAngle(pX, pY, cosT, sinT));
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
				archerPositions.add(pointAtAngle(pX, pY, cosT, sinT));
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
				magePositions.add(pointAtAngle(pX, pY, cosT, sinT));
				pY += spacing;
			}
			pX -= spacing;
		}
		Point average = getAveragePoint(sheildPositions, archerPositions, magePositions);
		startOrganizing(sheildPositions, archerPositions, magePositions, groupX-average.X, groupY-average.Y);
	}
	private void setGroupLayoutDefend(double rotation)
	{
	}
	private void setGroupLayoutAttack(double rotation)
	{
		Log.e("myid", "test14");
		double cosT = Math.cos(rotation/r2d);
		double sinT = Math.sin(rotation/r2d);
		ArrayList<Point> sheildPositions = new ArrayList<Point>();
		ArrayList<Point> archerPositions = new ArrayList<Point>();
		ArrayList<Point> magePositions = new ArrayList<Point>();
		int unitsPlaced = 0; // how many of current unit have a location
		int layer = 0;
		while(unitsPlaced < mages.size())
		{
			fillLayer(magePositions, layer, cosT, sinT);
			unitsPlaced += (layer*4 + 1); // enemies in each layer
			layer ++;
		}
		unitsPlaced = 0;
		while(unitsPlaced < archers.size())
		{
			fillLayer(archerPositions, layer, cosT, sinT);
			unitsPlaced += (layer*4 + 1); // enemies in each layer
			layer ++;
		}
		unitsPlaced = 0;
		while(unitsPlaced < sheilds.size())
		{
			fillLayer(sheildPositions, layer, cosT, sinT);
			unitsPlaced += (layer*4 + 1); // enemies in each layer
			layer ++;
		}
		Point average = getAveragePoint(sheildPositions, archerPositions, magePositions);
		startOrganizing(sheildPositions, archerPositions, magePositions, groupX-average.X, groupY-average.Y);
	}
	protected void fillLayer(ArrayList<Point> locations, int layer, double cosT, double sinT)
	{
		double pX = spacingSlanted * layer * 2; // how far out to start
		double pY = 0;
		locations.add(pointAtAngle(pX, pY, cosT, sinT));
		for(int i = 0; i < layer*2; i++)
		{
			pX -= spacingSlanted;
			pY += spacingSlanted;
			locations.add(pointAtAngle(pX, pY, cosT, sinT));
			locations.add(pointAtAngle(pX, -pY, cosT, sinT));
		}
	}
	protected Point pointAtAngle(double pX, double pY, double cosT, double sinT)
	{
		return new Point(cosT*pX - sinT*pY, sinT*pX + cosT*pY);
	}
	protected Point getAveragePoint(ArrayList<Point> sheildPositions, ArrayList<Point> archerPositions, ArrayList<Point> magePositions)
	{
		double averageX = 0;
		double averageY = 0;
		for(int i = 0; i < magePositions.size(); i++)
		{
			averageX += magePositions.get(i).X;
			averageY += magePositions.get(i).Y;
		}
		for(int i = 0; i < archerPositions.size(); i++)
		{
			averageX += archerPositions.get(i).X;
			averageY += archerPositions.get(i).Y;
		}
		for(int i = 0; i < sheildPositions.size(); i++)
		{
			averageX += sheildPositions.get(i).X;
			averageY += sheildPositions.get(i).Y;
		}
		averageX /= (magePositions.size()+archerPositions.size()+sheildPositions.size());
		averageY /= (magePositions.size()+archerPositions.size()+sheildPositions.size());
		return new Point(averageX, averageY);
	}
	protected void startOrganizing(List<Point> sheildPositions,List<Point> archerPositions, List<Point> magePositions, double addX, double addY)
	{
		Log.e("myid", "testqwet");
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).hasDestination = true;
			humans.get(i).speedCur = 5;			//faster to get in line
		}
		for(int i = 0; i < sheilds.size(); i++)
		{
			sheilds.get(i).destinationX = (int)(sheildPositions.get(i).X+addX);
			sheilds.get(i).destinationY = (int)(sheildPositions.get(i).Y+addY);
		}
		for(int i = 0; i < archers.size(); i++)
		{
			archers.get(i).destinationX = (int)(archerPositions.get(i).X+addX);
			archers.get(i).destinationY = (int)(archerPositions.get(i).Y+addY);
		}
		for(int i = 0; i < mages.size(); i++)
		{
			mages.get(i).destinationX = (int)(magePositions.get(i).X+addX);
			mages.get(i).destinationY = (int)(magePositions.get(i).Y+addY);
		}
	}
	protected void turnAfterOrganize()
	{
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).rotation = groupRotationAfterOrganize;
			humans.get(i).speedCur = 3.5;
		}
	}
	protected void startMovement()
	{
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).hasDestination = true;
			humans.get(i).speedCur = 3.5;
			humans.get(i).destinationX += destX-groupX;
			humans.get(i).destinationY += destY-groupY;
		}
	}
	protected void setLayoutType(int type) // 0 in norm, 1 is v, 2 is defend
	{
		layoutType = type;
		formUp();
		Log.e("myid", "type:".concat(Integer.toString(type)));
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
	protected void setDestination(double destXSet, double destYSet)
	{
		hasDestination = true;
		destX = destXSet;
		destY = destYSet;
		formUp(r2d*Math.atan2(destY-groupY, destX-groupX), destX, destY);
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
		hasLostMembers = true;
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
}