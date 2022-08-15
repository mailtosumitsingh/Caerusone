package org.ptg.util;



/**
 * this class contains transformation functions for a 3D vector
 * @author kenny cason
 * http://www.kennycason.com
 * 2008 December
 */
public class Transform3DMine {
	
	public Transform3DMine() {
	}
	 
	/**
	 * [(cos(a),-sin(a),0),
	 *  (sin(a),cos(a) ,0),
	 *  (0     ,0      ,1)]
	 * @param point
	 * @param theta
	 * @return
	 */
	public Point3D rotateZ(Point3D point, double theta) {
		double[] p = new double[point.get().length];
		theta = Math.toRadians(theta);
	    p[0] = point.getX() * Math.cos(theta) + point.getY() * -Math.sin(theta);
	    p[1] = point.getX() * Math.sin(theta) + point.getY() * Math.cos(theta);
	    p[2] = point.getZ();
	    point.set(p);
		return point;
	}

	/**
	 * [(cos(a) ,0      ,sin(a)),
	 *  (0      ,1      ,0),
	 *  (-sin(a),0      ,cos(a))]
	 * @param point
	 * @param theta
	 * @return
	 */
	public Point3D rotateY(Point3D point, double theta) {
		double[] p = new double[point.get().length];
		theta = Math.toRadians(theta);
	    p[0] = point.getX() * Math.cos(theta) + point.getZ() * Math.sin(theta);
	    p[1] = point.getY();
	    p[2] = point.getX() * -Math.sin(theta) + point.getZ() * Math.cos(theta);
	    point.set(p);
		return point;
	}
	
	
	/**
	 * [(1      ,0      ,0),
	 *  (0      ,cos(a) ,-sin(a)),
	 *  (0      ,sin(a) ,cos(a))]
	 * @param point
	 * @param theta
	 * @return
	 */
	public Point3D rotateX(Point3D point, double theta) {
		double[] p = new double[point.get().length];
		theta = Math.toRadians(theta);
		p[0] = point.getX();
	    p[1] = point.getY() * Math.cos(theta) + point.getZ() * -Math.sin(theta);
	    p[2] = point.getY() * Math.sin(theta) + point.getZ() * Math.cos(theta);
	    point.set(p);
		return point;
	}
	
	
	/**
	 * using quaternions to rotate around an arbirtuary axis
	 * 
	 * Given angle theta in radians and unit vector u = ai + bj + ck or (a,b,c)
	 * 
	 * q0 = cos(r/2),  q1 = sin(r/2) a,  q2 = sin(r/2) b,  q3 = sin(r/2) c
	 * 
	 * Q = 
	 * [
	 *   (q0 + q1 - q2 - q3)        2(q1q2 - q0q3)     2(q1q3 + q0q2)
	 *   2(q2q1 + q0q3)           (q0 - q1 + q2 - q3)  2(q2q3 - q0q1)
	 *   2(q3q1 - q0q2)             2(q3q2 + q0q1)     (q0 - q1 - q2 + q3)
	 * ]
	 * 
	 * Q u = u
	 * 
	 * TODO - implement this
	 * @param point
	 * @param vect
	 * @param theta
	 * @return
	 */
	public Point3D rotateAroundVector(Point3D point, Point3D vect, double theta) {
		double[] p = new double[point.get().length];
		Point3D unit = getUnitVector(vect);
		theta = Math.toRadians(theta);
		double q0 = Math.cos(theta/2);
		double q1 = Math.sin(theta/2)*unit.getX();
		double q2 = Math.sin(theta/2)*unit.getY();
		double q3 = Math.sin(theta/2)*unit.getZ();
		
		// row		
//		p[0] = (q0*q0+q1*q1-q2*q2-q3*q3)*point.getX() + 2*(q1*q2-q0*q3)*point.getY() + 2*(q1*q3+q0*q2)*point.getZ();
//		p[1] = 2*(q2*q1+q0*q3)*point.getX() + (q0*q0-q1*q1+q2*q2-q3*q3)*point.getY() + 2*(q2*q3-q0*q1)*point.getZ();
//		p[2] = 2*(q3*q1-q0*q2)*point.getX() + 2*(q3*q2+q0*q1)*point.getY() + (q0*q0-q1*q1-q2*q2+q3*q3)*point.getZ();
		
		// column vect
		p[0] = (q0*q0+q1*q1-q2*q2-q3*q3)*point.getX() + 2*(q2*q1+q0*q3)*point.getY() + 2*(q3*q1-q0*q2)*point.getZ();
		p[1] = 2*(q1*q2-q0*q3)*point.getX() +(q0*q0-q1*q1+q2*q2-q3*q3)*point.getY() + 2*(q3*q2+q0*q1)*point.getZ();
		p[2] = 2*(q1*q3+q0*q2)*point.getX() +2*(q2*q3-q0*q1)*point.getY() + (q0*q0-q1*q1-q2*q2+q3*q3)*point.getZ();
	//	System.out.println("p[0]="+p[0]+" p[1]="+p[1]+" p[2]="+p[2]);
		point.set(p);
		return point;
	}
	
	
	public Point3D getUnitVector(Point3D point) {
		double[] p = new double[point.get().length];

		double d = Math.sqrt(point.getX()*point.getX() + point.getY()*point.getY() + point.getZ()*point.getZ());
		p[0] = point.getX() / d;
		p[1] = point.getY() / d;
		p[2] = point.getZ() / d;

		return new Point3D(p);
	}
	
	
	/**
	 * compute cross product of two vectors
	 * @param v1
	 * @param v2
	 * @return
	 */
	public Point3D cross(Point3D v1, Point3D v2) {
		Point3D newVec = new Point3D(0,0,0);
		newVec.setX(v1.getY() * v2.getZ() - v1.getZ() * v2.getY());
		newVec.setY(v1.getZ() * v2.getX() - v1.getX() * v2.getZ());
		newVec.setZ(v1.getX() * v2.getY() - v1.getY() * v2.getX());
		return newVec;
	}
	
	/**
	 * compute dot product of two vectors
	 * @param _v
	 * @return
	 */
	public double dot(Point3D v1, Point3D v2) {
		double dot = 0;
		dot += (v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ());
		return dot;
	}

	
	
	
	/**
	 * 
	 * @param point
	 * @param cameraLoc
	 * @return
	 */
	public Point3D perspective3D(Point3D point, double[] cameraLoc) {
		point.setX(point.getX() - cameraLoc[0]);
		point.setY(point.getY() - cameraLoc[1]);
		point.setZ(point.getZ() - cameraLoc[2]);
		return point;
	}
	

	
	
}