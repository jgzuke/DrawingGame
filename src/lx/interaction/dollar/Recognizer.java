package lx.interaction.dollar;

import java.util.Vector;


import android.util.Log;

import com.drawinggame.Controller;
import com.drawinggame.GestureDetector;

public class Recognizer
{
	//
	// Recognizer class constants
	//
	public static int NumPoints = 64;
	public static double SquareSize = 250.0;
	double AngleRange = 0.0;
	double AnglePrecision = 1.0;
	public static double Phi = 0.5 * (-1.0 + Math.sqrt(5.0)); // Golden Ratio
	
	public Point centroid = new Point(0, 0);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	int bounds[] = { 0, 0, 0, 0 };
	
	public Vector<Template> noSelectTemplates = new Vector<Template>();
	public Vector<Template> singleSelectTemplates = new Vector<Template>();
	public Vector<Template> groupSelectTemplates = new Vector<Template>();
	private GestureDetector gestureDetector;
	public Recognizer(GestureDetector gestureDetectorSet)
	{
		loadtemplates();
		gestureDetector = gestureDetectorSet;
	}
	
	void loadtemplates()
	{
		noSelectTemplates.addElement(loadTemplate("lineH", TemplateData.lineHorizontal));
		noSelectTemplates.addElement(loadTemplate("lineH", TemplateData.lineHorizontalR));
		noSelectTemplates.addElement(loadTemplate("lineH", TemplateData.lineHorizontalL));
		noSelectTemplates.addElement(loadTemplate("lineV", TemplateData.lineVertical));
		noSelectTemplates.addElement(loadTemplate("lineV", TemplateData.lineVerticalU));
		noSelectTemplates.addElement(loadTemplate("lineV", TemplateData.lineVerticalD));
		noSelectTemplates.addElement(loadTemplate("arrow", TemplateData.arrow));
		noSelectTemplates.addElement(loadTemplate("arrow", TemplateData.arrowLongLeft));
		noSelectTemplates.addElement(loadTemplate("arrow", TemplateData.arrowLongRight));
		groupSelectTemplates.addElement(loadTemplate("lineH", TemplateData.lineHorizontal));
		groupSelectTemplates.addElement(loadTemplate("lineH", TemplateData.lineHorizontalR));
		groupSelectTemplates.addElement(loadTemplate("lineH", TemplateData.lineHorizontalL));
		groupSelectTemplates.addElement(loadTemplate("lineV", TemplateData.lineVertical));
		groupSelectTemplates.addElement(loadTemplate("lineV", TemplateData.lineVerticalU));
		groupSelectTemplates.addElement(loadTemplate("lineV", TemplateData.lineVerticalD));
		groupSelectTemplates.addElement(loadTemplate("arrow", TemplateData.arrow));
		groupSelectTemplates.addElement(loadTemplate("arrow", TemplateData.arrowLongLeft));
		groupSelectTemplates.addElement(loadTemplate("arrow", TemplateData.arrowLongRight));
		
		
	}
	Template loadTemplate(String name, int[] array)
	{
		return new Template(name, loadArray(array));
	}
	
	Vector<Point> loadArray(int[] array)
	{
		Vector<Point> v = new Vector<Point>(array.length/2);
		for (int i = 0; i < array.length; i+= 2)
		{
			Point p = new Point(array[i], array[i+1]);
			v.addElement(p);
		}
		return v;
	}
	public void recognizeNoSelect(Vector<Point> points)
	{
		Point moveCoords = Utils.getCentre(points);					// use this to get the x, y of the gestures centre
		points = Utils.ScaleToSquare(points, SquareSize);
		points = Utils.TranslateToOrigin(points);
		gestureDetector.setLastShapeDone((Vector<Point>) points.clone());
	
		bounds[0] = (int)boundingBox.X;
		bounds[1] = (int)boundingBox.Y;
		bounds[2] = (int)boundingBox.X + (int)boundingBox.Width;
		bounds[3] = (int)boundingBox.Y + (int)boundingBox.Height;
		int t = 0;
		double b = Double.MAX_VALUE;
		for (int i = 0; i < noSelectTemplates.size(); i++)
		{
			double d = Utils.PathDistance(points, noSelectTemplates.elementAt(i).Points);
			if((noSelectTemplates.elementAt(i)).Name.startsWith("line")) d *= 3;
			if (d < b)
			{
				b = d;
				t = i;
			}
		}
		if(b > 40)
		{
			//Log.e("myid", Double.toString(b).concat("unknown"));
			gestureDetector.endShape(moveCoords);
		} else
		{
			//Log.e("myid", Double.toString(b).concat((templates.elementAt(t)).Name));
			gestureDetector.endShapeSingle((noSelectTemplates.elementAt(t)).Name, moveCoords);
		}
	}
	public void recognizeSingleSelect(Vector<Point> points)
	{
		points = Utils.RotateToZero(points, centroid, boundingBox);
		
	}
	public void recognizeGroupSelect(Vector<Point> points)
	{
		Point moveCoords = Utils.getCentre(points);					// use this to get the x, y of the gestures centre
		points = Utils.ScaleToSquare(points, SquareSize);
		points = Utils.TranslateToOrigin(points);
		gestureDetector.setLastShapeDone((Vector<Point>) points.clone());
	
		bounds[0] = (int)boundingBox.X;
		bounds[1] = (int)boundingBox.Y;
		bounds[2] = (int)boundingBox.X + (int)boundingBox.Width;
		bounds[3] = (int)boundingBox.Y + (int)boundingBox.Height;
		int t = 0;
		double b = Double.MAX_VALUE;
		for (int i = 0; i < groupSelectTemplates.size(); i++)
		{
			double d = Utils.PathDistance(points, groupSelectTemplates.elementAt(i).Points);
			if((groupSelectTemplates.elementAt(i)).Name.startsWith("line")) d *= 3;
			if (d < b)
			{
				b = d;
				t = i;
			}
		}
		if(b > 40)
		{
			//Log.e("myid", Double.toString(b).concat("unknown"));
			gestureDetector.endShape(moveCoords);
		} else
		{
			//Log.e("myid", Double.toString(b).concat((templates.elementAt(t)).Name));
			gestureDetector.endShapeGroup((groupSelectTemplates.elementAt(t)).Name, moveCoords);
		}
	}
	public void Recognize(Vector<Point> points, String selectType)
	{
		if(points.size() == 0) return;
		Rectangle myBounds = new Rectangle(0,0,0,0);
		Utils.BoundingBox(points, myBounds);
		if(isClick(myBounds)) return;
		points = Utils.Resample(points, NumPoints);
		gestureDetector.setLastShape((Vector<Point>) points.clone());
		if(isCircle(points)) return;
		
		
		
		if(selectType.equals("none"))
	    {
			recognizeNoSelect(points);
	    } else if(selectType.equals("single"))
	    {
	    	recognizeSingleSelect(points);
	    } else if(selectType.equals("group"))
	    {
	    	recognizeGroupSelect(points);
	    }
	}
	public boolean isClick(Rectangle myBounds)
	{
		double distMax = Math.pow(myBounds.Width, 2) + Math.pow(myBounds.Height, 2);
		if(distMax < 2000)			// a click
		{
			gestureDetector.click(new Point(myBounds.X+(myBounds.Width/2), myBounds.Y+(myBounds.Height/2)));
			return true;
		}
		return false; 
	}
	public boolean isCircle(Vector<Point> points)
	{
		double pointSeperation = 10*distanceBetweenPoints(points, 0, 1);
		for(int i = 0; i < points.size()-40; i++)
		{
			for(int j = i+40; j < points.size(); j++)
			{
				if(distanceBetweenPoints(points, i, j) < pointSeperation) // we made a circle, end points are close to start points
				{
					gestureDetector.endCircle(points);
					return true;
				}
			}
		}
		return false;
	}
	public double distanceBetweenPoints(Vector<Point> points, int index1, int index2)
	{
		double xDif = points.get(index1).X - points.get(index2).X;
		double yDif = points.get(index1).Y - points.get(index2).Y;
		return Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2));
	}
}
