package crashmap

class Point {
	double x, y;
	int count;
	
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
		count = 1;
	}
	
	public void add()
	{
		count++;
	}
	
	public boolean equals(Point point)
	{
		if(((Point) point).x == x && ((Point) point).y == y)
			return true;
		else
			return false;
	}
	
	public String toString()
	{
		//return x + ", " + y + " " + count +"\n";
		return "{lat: " + y + ", lng: " + x + ", count: " + count + "}";
	}
	
	static mapWith = "none"
    static constraints = {
    }
}
