package lx.interaction.dollar;

import java.util.*;

public class Template
{
	public String Name;
	public Vector<Point> Points;
	public Vector<Point> PointsLate;
	public Vector<Point> PointsEarly;
	public Vector<Point> Points1;
	public Vector<Point> PointsLate1;
	public Vector<Point> PointsEarly1;
	public Vector<Point> Points2;
	public Vector<Point> PointsLate2;
	public Vector<Point> PointsEarly2;
	public double simplicity;

	Template(String name, Vector<Point> points) 
	{
		this.Name = name;
		Points = Utils.Resample(points);
		Points = Utils.ScaleToSquare(this.Points, Recognizer.SquareSize);
		Points = Utils.TranslateToOrigin(this.Points);
		PointsLate = Utils.ResampleEarly((Vector<Point>) points);
		PointsEarly	= Utils.ResampleLate((Vector<Point>) points);
		Points1 = Utils.RotateBy(Points, 0.1);
		Points2 = Utils.RotateBy(Points, -0.1);
		PointsLate1 = Utils.RotateBy(PointsLate, 0.1);
		PointsLate2 = Utils.RotateBy(PointsLate, -0.1);
		PointsEarly1 = Utils.RotateBy(PointsEarly, 0.1);
		PointsEarly2 = Utils.RotateBy(PointsEarly, -0.1);
		
		simplicity = Utils.getSimplicity(this.Points);
	}
	public void replacePoints(Vector<Point> points)
	{
		this.Points = points;
		simplicity = Utils.getSimplicity(this.Points);
	}
}
