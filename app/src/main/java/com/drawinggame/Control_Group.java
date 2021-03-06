package com.drawinggame;

import java.util.ArrayList;
import java.util.List;

import lx.interaction.dollar.Point;

public final class Control_Group extends Control_Main
{
	protected ArrayList<Enemy> humans;
	protected ArrayList<Enemy_Archer> archers = new ArrayList<Enemy_Archer>();
	protected ArrayList<Enemy_Mage> mages = new ArrayList<Enemy_Mage>();
	protected ArrayList<Enemy_Sheild> sheilds = new ArrayList<Enemy_Sheild>();
	protected int destinationRotation;
	protected int groupRotation;
	private static double r2d = 180/Math.PI;
	protected boolean hasDestination = false;
	private boolean hasChangedMembers = false;
	protected int layoutType = 1; //1 is norm, 2 is standGround, 0 is V or attack
	protected Point destLocation;
	protected boolean organizing = false;
	private static double spacing = 40;
	private static double spacingSlanted = Math.sqrt(2)*spacing/2;
	private boolean groupEngaged = false;
	
	public Control_Group(Controller controlSet, ArrayList<Enemy> humansSet, boolean onPlayersTeam)
	{
		super(controlSet, onPlayersTeam);
		humans = humansSet;
		double layoutMode = 0;
		double inGroups = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			if(humans.get(i).myController != null && humans.get(i).myController.isGroup)
			{
				layoutMode += ((Control_Group)humans.get(i).myController).layoutType;
				inGroups ++;
			}
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
		humans = new ArrayList<Enemy>();
		humans.addAll(mages);
		humans.addAll(archers);
		humans.addAll(sheilds);
		groupLocation = averagePoint();
		groupRotation = averageRotation();
		isGroup = true;
		if(inGroups == 0)			// if noone in a group
		{
			layoutType = 1;
		} else
		{
			layoutType = (int) Math.round(layoutMode/inGroups);
		}
		formUp();
		groupRadius = Math.sqrt(humans.size()) * spacing;
	}
	protected void frameCall()
	{
		if(humans.size()==0) return;
		boolean lastGoupEngaged = groupEngaged;
		groupEngaged = false;
		doingNothing = false;
		if(organizing)
		{
			boolean doneOrganizing = true;
			for(int i = 0; i < humans.size(); i ++)
			{
				if(humans.get(i).hasDestination)
				{
					doneOrganizing = false;
				}
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
		} else if(retreating)
		{
			if(Math.sqrt(Math.pow(groupLocation.X-destLocation.X, 2)+Math.pow(groupLocation.Y-destLocation.Y, 2)) < 30) // reached destination
			{
				hasDestination = false;
				retreating = false;
			}
		} else if(enemiesAround())
		{
			groupEngaged = true;
			for(int i = 0; i < humans.size(); i++)
			{
				humans.get(i).hasDestination = false;
			}
		} else if(hasDestination)
		{
			if(Math.sqrt(Math.pow(groupLocation.X-destLocation.X, 2)+Math.pow(groupLocation.Y-destLocation.Y, 2)) < 30) // reached destination
			{
				hasDestination = false;
			}
		} else
		{
			if(hasChangedMembers || lastGoupEngaged)
			{
				groupRadius = Math.sqrt(humans.size()) * spacing;
				hasChangedMembers = false;
				formUp();
			} else
			{
				doingNothing = true;
			}
		}
		groupLocation = averagePoint();
		groupRotation = averageRotation();
		for(int i = 0; i < archers.size(); i ++) Control_AI.archerFrame(archers.get(i), retreating, groupEngaged);
		for(int i = 0; i < mages.size(); i ++) Control_AI.mageFrame(mages.get(i), retreating, groupEngaged);
		for(int i = 0; i < sheilds.size(); i ++) Control_AI.sheildFrame(sheilds.get(i), retreating, groupEngaged);
	}
	protected void formUp()
	{
		if(hasDestination)
		{
			formUp(destinationRotation, groupLocation.X, groupLocation.Y);
		} else
		{
			formUp(groupRotation, groupLocation.X, groupLocation.Y);
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
		organizing = true;
		destinationRotation = (int) rotation;
		if(layoutType == 0)
		{
			setGroupLayoutAttack(rotation);
		} else if(layoutType == 1)
		{
			setGroupLayoutNormal(rotation);
		} else if(layoutType == 2)
		{
			setGroupLayoutDefend(rotation);
		}
	}
	private void setGroupLayoutNormal(double rotation)
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
		archersPerRow = (int) Math.ceil(((double)archers.size())/archerRows);
		magesPerRow = (int) Math.ceil(((double)mages.size())/mageRows);
		sheildsPerRow = (int) Math.ceil(((double)sheilds.size())/sheildRows);
		ArrayList<Point> sheildPositions = new ArrayList<Point>();
		ArrayList<Point> archerPositions = new ArrayList<Point>();
		ArrayList<Point> magePositions = new ArrayList<Point>();
		double cosT = Math.cos(rotation/r2d);
		double sinT = Math.sin(rotation/r2d);
		double pX = (spacing/2) * (bestRows-1);
		double pY;
		for(int i = 0; i < sheildRows; i++)
		{
			pY = 0;
			int inRow = sheildsPerRow;
			if(i == sheildRows-1) inRow = sheilds.size() - sheildsPerRow*(sheildRows-1);
			if(inRow%2 == 0) pY = spacing/2;// even num
			for(int j = 0; j < (sheildsPerRow+1)/2; j++)
			{
				sheildPositions.add(pointAtAngle(pX, pY, cosT, sinT));
				if(pY != 0) sheildPositions.add(pointAtAngle(pX, -pY, cosT, sinT));
				pY += spacing;
			}
			pX -= spacing;
		}
		for(int i = 0; i < archerRows; i++)
		{
			pY = 0;
			int inRow = sheildsPerRow;
			if(i == archerRows-1) inRow = archers.size() - archersPerRow*(archerRows-1);
			if(inRow%2 == 0) pY = spacing/2;// even num
			for(int j = 0; j < (archersPerRow+1)/2; j++)
			{
				archerPositions.add(pointAtAngle(pX, pY, cosT, sinT));
				if(pY != 0) archerPositions.add(pointAtAngle(pX, -pY, cosT, sinT));
				pY += spacing;
			}
			pX -= spacing;
		}
		for(int i = 0; i < mageRows; i++)
		{
			pY = 0;
			int inRow = magesPerRow;
			if(i == mageRows-1) inRow = mages.size() - magesPerRow*(mageRows-1);
			if(inRow%2 == 0) pY = spacing/2;// even num
			for(int j = 0; j < (magesPerRow+1)/2; j++)
			{
				magePositions.add(pointAtAngle(pX, pY, cosT, sinT));
				if(pY != 0) magePositions.add(pointAtAngle(pX, -pY, cosT, sinT));
				pY += spacing;
			}
			pX -= spacing;
		}
		Point average = getAveragePoint(sheildPositions, archerPositions, magePositions);
		startOrganizing(sheildPositions, archerPositions, magePositions, groupLocation.X-average.X, groupLocation.Y-average.Y, (int)rotation);
	}
	private void setGroupLayoutDefend(double rotation)
	{
		double cosT = Math.cos(rotation/r2d);
		double sinT = Math.sin(rotation/r2d);
		ArrayList<Point> locations = new ArrayList<Point>();
		int layer = 0;
		boolean moreUnits = true;
		int humansLeft = humans.size();
		while(moreUnits)
		{
			double pX = 0; // how far out to start
			double pY = spacing * layer;
			int skip = (layer*4 + 1) - humansLeft; // layersize - humans left
			for(int i = 0; i < layer*2 + 1; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					if(locations.size() == humans.size())
					{
						Point average = getAveragePoint(locations);
						startOrganizing(locations, groupLocation.X-average.X, groupLocation.Y-average.Y, (int)rotation);
						return;
					}
					if(i!=layer*2 || j != 0)
					{
						skip --;
						if(skip < 0)
						{
							humansLeft--;
							if(j==0)
							{
								locations.add(pointAtAngle(pX, -pY, cosT, sinT));
							} else
							{
								locations.add(pointAtAngle(pX, pY, cosT, sinT));
							}
						}
					}
				}
				if(i < layer)
				{
					pX += spacing;
				} else
				{
					pY -= spacing;
				}
			}
			layer ++;
		}
	}
	private void setGroupLayoutAttack(double rotation)
	{
		double cosT = Math.cos(rotation/r2d);
		double sinT = Math.sin(rotation/r2d);
		ArrayList<Point> locations = new ArrayList<Point>();
		int layer = 0;
		boolean moreUnits = true;
		int humansLeft = humans.size();
		while(moreUnits)
		{
			double pX = 0; // how far out to start
			double pY = spacingSlanted * layer * 2;
			int skip = (layer*4 + 1) - humansLeft; // layersize - humans left
			for(int i = 0; i < layer*2 + 1; i++)
			{
				for(int j = 0; j < 2; j++)
				{
					if(locations.size() == humans.size())
					{
						Point average = getAveragePoint(locations);
						startOrganizing(locations, groupLocation.X-average.X, groupLocation.Y-average.Y, (int)rotation);
						return;
					}
					if(i!=layer*2 || j != 0)
					{
						skip --;
						if(skip < 0)
						{
							humansLeft--;
							if(j==0)
							{
								locations.add(pointAtAngle(pX, -pY, cosT, sinT));
							} else
							{
								locations.add(pointAtAngle(pX, pY, cosT, sinT));
							}
						}
					}
				}
				pX += spacingSlanted;
				pY -= spacingSlanted;
			}
			layer ++;
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
	protected Point getAveragePoint(ArrayList<Point> positions)
	{
		double averageX = 0;
		double averageY = 0;
		for(int i = 0; i < positions.size(); i++)
		{
			averageX += positions.get(i).X;
			averageY += positions.get(i).Y;
		}
		averageX /= positions.size();
		averageY /= positions.size();
		return new Point(averageX, averageY);
	}
	protected void startOrganizing(List<Point> positions, double addX, double addY, int setRotation)
	{
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).setDestination((int)(positions.get(i).X+addX), (int)(positions.get(i).Y+addY), setRotation);
		}
	}
	protected void startOrganizing(List<Point> sheildPositions,List<Point> archerPositions, List<Point> magePositions, double addX, double addY, int setRotation)
	{
		for(int i = 0; i < sheilds.size(); i++)
		{
			sheilds.get(i).setDestination((int)(sheildPositions.get(i).X+addX), (int)(sheildPositions.get(i).Y+addY), setRotation);
		}
		for(int i = 0; i < archers.size(); i++)
		{
			archers.get(i).setDestination((int)(archerPositions.get(i).X+addX), (int)(archerPositions.get(i).Y+addY), setRotation);
		}
		for(int i = 0; i < mages.size(); i++)
		{
			mages.get(i).setDestination((int)(magePositions.get(i).X+addX), (int)(magePositions.get(i).Y+addY), setRotation);
		}
	}
	protected void turnAfterOrganize()
	{
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).speedCur = 3.5;
		}
	}
	protected void startMovement()
	{
		for(int i = 0; i < humans.size(); i++)
		{
			int setX = (int) (humans.get(i).destinationX + destLocation.X-groupLocation.X);
			int setY = (int) (humans.get(i).destinationY + destLocation.Y-groupLocation.Y);
			humans.get(i).setDestination(setX, setY);
		}
	}
	protected void setLayoutType(int type) // 0 in norm, 1 is v, 2 is defend
	{
		layoutType = type;
		formUp();
	}
	protected Point averagePoint()
	{
		double sumX = 0;
		double sumY = 0;
		for(int i = 0; i < humans.size(); i++)
		{
			sumX += humans.get(i).x;
			sumY += humans.get(i).y;
		}
		return new Point(sumX/(double)humans.size(), sumY/(double)humans.size());
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
	protected void setSelected()
	{
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).selected = true;
		}
	}
	@Override
	protected void setDestination(Point p)
	{
		if(enemiesAround()) retreating = true;
		hasDestination = true;
		destLocation = p;
		formUp(r2d*Math.atan2(destLocation.Y-groupLocation.Y, destLocation.X-groupLocation.X), destLocation.X, destLocation.Y);
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
			if(onPlayersTeam)
			{
				control.spriteController.allyControllers.remove(this);
			} else
			{
				control.spriteController.enemyControllers.remove(this);
			}
		}
		hasChangedMembers = true;
	}
	@Override
	protected void cancelMove()
	{
		hasDestination = false;
		retreating = false;
		for(int i = 0; i < humans.size(); i++)
		{
			humans.get(i).hasDestination = false;
		}
	}
	
	@Override
	protected void archerDoneFiring(Enemy_Archer archer)
	{
		Control_AI.archerDoneFiring(archer, enemiesAround() && !archer.hasDestination && !organizing);
	}
	@Override
	protected int getSize()
	{
		return humans.size();
	}
}