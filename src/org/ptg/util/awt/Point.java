
package org.ptg.util.awt;


public class Point  implements java.io.Serializable {
	String id;
    public int x;

    public int y;

    private static final long serialVersionUID = -5276940640259749850L;
    public Point() {
        this(0, 0);
    }
    public Point(Point p) {
        this(p.x, p.y);
    }
    public Point(int x, int y,String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    public void setLocation(int x, int y) {
        move(x, y);
    }

    public void setLocation(double x, double y) {
        this.x = (int) Math.floor(x+0.5);
        this.y = (int) Math.floor(y+0.5);
    }
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

    public String toString() {
        return   x + ":" + y ;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setX(int x) {
		this.x = x; 
	}

	public void setY(int y) {
		this.y = y;
	}
    
}
