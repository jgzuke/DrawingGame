package lx.interaction.dollar;

import java.util.*;

import com.drawinggame.MyView;

public class Recognizer
{
	//
	// Recognizer class constants
	//
	int Numtemplates = 16;
	public static int NumPoints = 64;
	public static double SquareSize = 250.0;
	double AngleRange = 0.0;
	double AnglePrecision = 1.0;
	public static double Phi = 0.5 * (-1.0 + Math.sqrt(5.0)); // Golden Ratio
	
	public Point centroid = new Point(0, 0);
	public Rectangle boundingBox = new Rectangle(0, 0, 0, 0);
	int bounds[] = { 0, 0, 0, 0 };
	
	public Vector<Template> templates = new Vector<Template>(Numtemplates);
	private MyView myView;
	public Recognizer(MyView myViewSet)
	{
		loadtemplates();
		myView = myViewSet;
	}
	
	void loadtemplates()
	{
		templates.addElement(loadTemplate("triangle", TemplateData.trianglePoints));
		templates.addElement(loadTemplate("x", TemplateData.xPoints));
		templates.addElement(loadTemplate("rectangle CCW", TemplateData.rectanglePointsCCW));
		templates.addElement(loadTemplate("circle CCW", TemplateData.circlePointsCCW));
		templates.addElement(loadTemplate("check", TemplateData.checkPoints));
		templates.addElement(loadTemplate("caret CW", TemplateData.caretPointsCW));
		templates.addElement(loadTemplate("question", TemplateData.questionPoints));
		templates.addElement(loadTemplate("arrow", TemplateData.arrowPoints));
		templates.addElement(loadTemplate("leftSquareBracket", TemplateData.leftSquareBracketPoints));
		templates.addElement(loadTemplate("rightSquareBracket", TemplateData.rightSquareBracketPoints));
		templates.addElement(loadTemplate("v", TemplateData.vPoints));
		templates.addElement(loadTemplate("delete", TemplateData.deletePoints));	
		templates.addElement(loadTemplate("leftCurlyBrace", TemplateData.leftCurlyBracePoints));
		templates.addElement(loadTemplate("rightCurlyBrace", TemplateData.rightCurlyBracePoints));
		templates.addElement(loadTemplate("star", TemplateData.starPoints));
		templates.addElement(loadTemplate("pigTail", TemplateData.pigTailPoints));
	}
	
	void loadtemplatesSimple()
	{
		templates.addElement(loadTemplate("circle CCW", TemplateData.circlePointsCCW));
		templates.addElement(loadTemplate("circle CW", TemplateData.circlePointsCW));
		templates.addElement(loadTemplate("rectangle CCW", TemplateData.rectanglePointsCCW));
		templates.addElement(loadTemplate("rectangle CW", TemplateData.rectanglePointsCW));
		templates.addElement(loadTemplate("caret CCW", TemplateData.caretPointsCCW));
		templates.addElement(loadTemplate("caret CW", TemplateData.caretPointsCW));
	}
	

	void loadtemplatesCircles()
	{
		templates.addElement(loadTemplate("circle CCW", TemplateData.circlePointsCCW));
		templates.addElement(loadTemplate("circle CW", TemplateData.circlePointsCW));
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
	
	public String Recognize(Vector<Point> points)
	{
		points = Utils.Resample(points, NumPoints);
		points = Utils.ScaleToSquare(points, SquareSize);
		points = Utils.TranslateToOrigin(points);
		myView.setLastShapeDone((Vector<Point>) points.clone());
	
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
		return (templates.elementAt(t)).Name;
	};
}
