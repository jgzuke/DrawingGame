package lx.interaction.dollar;

public class Point
{
	public double X, Y;
	
	public Point(double x, double y)
	{	this.X = x; this.Y = y;	}
	
	public void copy(Point src)
	{
		X = src.X;
		Y = src.Y;
	}
	public Point clone()
	{
		return new Point(X, Y);
	}
	public double distanceTo(Point p)
	{
		return Math.sqrt(Math.pow(p.X-X, 2)+Math.pow(p.Y-Y, 2));
	}	
}
