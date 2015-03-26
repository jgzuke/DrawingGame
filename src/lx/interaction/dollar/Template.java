package lx.interaction.dollar;

import java.util.*;

public class Template
{
	String Name;
	public Vector<Point> Points;

	Template(String name, Vector<Point> points) 
	{
		this.Name = name;
		this.Points = Utils.Resample(points, Recognizer.NumPoints);
		this.Points = Utils.RotateToZero(this.Points);
		this.Points = Utils.ScaleToSquare(this.Points, Recognizer.SquareSize);
		this.Points = Utils.TranslateToOrigin(this.Points);		
	}
}
