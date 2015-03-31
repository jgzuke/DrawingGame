package lx.interaction.dollar;

import java.util.Vector;


import com.drawinggame.Controller;
import com.drawinggame.GestureDetector;

public class Recognizer
{
	//
	// Recognizer class constants
	//
	int Numtemplates = 8;
	public static int NumPoints = 64;
	public static double SquareSize = 250.0;
	double AngleRange = 0.0;
	double AnglePrecision = 1.0;
	public static double Phi = 0.5 * (-1.0 + Math.sqrt(5.0)); // Golden Ratio
	
	public Point centroid = new Point(0, 0);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	int bounds[] = { 0, 0, 0, 0 };
	
	public Vector<Template> templates = new Vector<Template>(Numtemplates);
	private GestureDetector gestureDetector;
	public Recognizer(GestureDetector gestureDetectorSet)
	{
		loadtemplates();
		gestureDetector = gestureDetectorSet;
	}
	
	void loadtemplates()
	{
		templates.addElement(loadTemplate("lineRight", TemplateData.lineRight));
		templates.addElement(loadTemplate("lineLeft", TemplateData.lineLeft));
		templates.addElement(loadTemplate("lineDown", TemplateData.lineDown));
		templates.addElement(loadTemplate("lineUp", TemplateData.lineUp));
		templates.addElement(loadTemplate("arrowRight", TemplateData.arrowRight));
		templates.addElement(loadTemplate("arrowLeft", TemplateData.arrowLeft));
		templates.addElement(loadTemplate("leftCurlyBracePoints", TemplateData.leftCurlyBracePoints));
		templates.addElement(loadTemplate("rightCurlyBracePoints", TemplateData.rightCurlyBracePoints));
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
	
	public void Recognize(Vector<Point> points)
	{
		if(points.size() == 0) return;
		Rectangle myBounds = new Rectangle(0,0,0,0);
		Utils.BoundingBox(points, myBounds);
		double distMax = Math.pow(myBounds.Width, 2) + Math.pow(myBounds.Height, 2);
		if(distMax < 2000)			// a click
		{
			gestureDetector.click(new Point(myBounds.X+(myBounds.Width/2), myBounds.Y+(myBounds.Height/2)));
		} else
		{
			points = Utils.Resample(points, NumPoints);
			gestureDetector.setLastShape((Vector<Point>) points.clone());
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
			for (int i = 0; i < templates.size(); i++)
			{
				double d = Utils.DistanceAtAngle(points, (Template)templates.elementAt(i), 0);
				if (d < b)
				{
					b = d;
					t = i;
				}
			}
			gestureDetector.endShape((templates.elementAt(t)).Name, moveCoords);
		}
	};
}
