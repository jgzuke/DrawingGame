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
		Points = Utils.ScaleToSquare(Points, Recognizer.SquareSize);
		Points = Utils.TranslateToOrigin(Points);
		Points1 = Utils.RotateBy(Points, 0.1);
		Points2 = Utils.RotateBy(Points, -0.1);
		simplicity = Utils.getSimplicity(Points);
		
		Vector<Point> bigger = Utils.Resample(Points, Recognizer.NumPoints+2);
		PointsEarly = ResampleEarly(bigger);
		PointsLate = ResampleLate(bigger);
		bigger = Utils.Resample(Points1, Recognizer.NumPoints+2);
		PointsEarly1 = ResampleEarly(bigger);
		PointsLate1 = ResampleLate(bigger);
		bigger = Utils.Resample(Points2, Recognizer.NumPoints+2);
		PointsEarly2 = ResampleEarly(bigger);
		PointsLate2 = ResampleLate(bigger);
	}
	public void replacePoints(Vector<Point> points)
	{
		Points = points;
		Points1 = Utils.RotateBy(Points, 0.1);
		Points2 = Utils.RotateBy(Points, -0.1);
		Vector<Point> bigger = Utils.Resample(Points, Recognizer.NumPoints+2);
		PointsEarly = ResampleEarly(bigger);
		PointsLate = ResampleLate(bigger);
		bigger = Utils.Resample(Points1, Recognizer.NumPoints+2);
		PointsEarly1 = ResampleEarly(bigger);
		PointsLate1 = ResampleLate(bigger);
		bigger = Utils.Resample(Points2, Recognizer.NumPoints+2);
		PointsEarly2 = ResampleEarly(bigger);
		PointsLate2 = ResampleLate(bigger);
		simplicity = Utils.getSimplicity(Points);
	}
	public Vector<Point> ResampleLate(Vector<Point> pointsNew)
	{
		pointsNew.remove(0);
		pointsNew.remove(0);
		return pointsNew;
	}
	public Vector<Point> ResampleEarly(Vector<Point> points)
	{
		Vector<Point> pointsNew = (Vector<Point>) points.clone();
		pointsNew.remove(pointsNew.size()-1);
		pointsNew.remove(pointsNew.size()-1);
		return pointsNew;
	}
}
