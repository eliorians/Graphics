package ray.surface;

import ray.HitRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Ellipsoid extends Surface {
    /** The center of the ellipsoid. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }

    /** Length of axis a. */
	protected double a = 6;
	public void setA(double a) { this.a = a; }

    /** Length of axis b. */
	protected double b = 3;
	public void setB(double b) { this.b = b; }

    /** Length of axis c. */
	protected double c = 4.5;
	public void setC(double c) { this.c = c; }

	/** Axis a. */
	protected final Vector3 aAxis = new Vector3(1, 0, 0);
	public void setAAxis(Vector3 aAxis) { this.aAxis.set(aAxis); this.aAxis.normalize(); }

	/** Axis b. */
	protected final Vector3 bAxis = new Vector3(0, 1, 0);
	public void setBAxis(Vector3 bAxis) { this.bAxis.set(bAxis); this.bAxis.normalize(); }

	/** Axis c. */
	protected final Vector3 cAxis = new Vector3(0, 0, 1);
	public void setCAxis(Vector3 cAxis) { this.cAxis.set(cAxis); this.cAxis.normalize(); }
    
	//TODO: make this work for rotated ellipsoids
	//? or does that occur in computeRayDirection
    @Override
    public HitRecord hit(Ray ray, double t0, double t1) 
	{
        //throw new UnsupportedOperationException();
		HitRecord hit = new HitRecord();

		//set up ray equation
		Point3 o = ray.getOrigin();
		Vector3 d = ray.getDirection();

		//set up ellipsoid equation
		//(x^2/a^2) + (y^2/b^2) + (z^2/c^2) - 1 = 0
		//x,y,z = coords on ellipse (ray)
		//a,b,c = x,y,z axis length respectfully
		
		//where min and max are the bounding coords of a box around the ellipsoid
		double xMin = center.x - a;
		double xMax = center.x + a;
		double yMin = center.y - b;
		double yMax = center.y + b;
		double zMin = center.z - c;
		double zMax = center.z + c;
		setA((xMax - xMin) / 2.0);
		setB((yMax - yMin) / 2.0);
		setC((zMax - zMin) / 2.0);
		
		//calc quadratic equation coefficents
		// double A = d.x * d.x / a * a + d.y * d.y / b * b + d.z * d.z / c * c;
		// double B = 2 * (o.x * d.x / a * a + o.y * d.y / b * b + o.z * d.z / c * c);
		// double C = o.x * o.x / a * a + o.y * o.y / b * b + o.z * o.z / c * c - 1;
		double A = d.x * d.x / (a * a) + d.y * d.y / (b * b) + d.z * d.z / (c * c);
		double B = 2 * (d.x * (o.x - center.x) / (a * a) + d.y * (o.y - center.y) / (b * b) + d.z * (o.z - center.z) / (c * c));
		double C = (o.x - center.x) * (o.x - center.x) / (a * a) + (o.y - center.y) * (o.y - center.y) / (b * b) + (o.z - center.z) * (o.z - center.z) / (c * c) - 1;
		
		//solve quadratic for t
		double[] roots = solveQuadratic(A, B, C);

		//see if it hits
		for (double root : roots) {
			if (root >= t0 && root <= t1) {

				// calc intersection point
				Point3 intersectionPoint = ray.evaluate(root);
				
				//calc normal
				// Vector3 normal = new Vector3(
                //     2 * intersectionPoint.x / (a * a),
                //     2 * intersectionPoint.y / (b * b),
                //     2 * intersectionPoint.z / (c * c)
            	// );

				Vector3 normal = new Vector3(
                2 * (intersectionPoint.x - center.x) / (a * a),
                2 * (intersectionPoint.y - center.y) / (b * b),
                2 * (intersectionPoint.z - center.z) / (c * c)
            	);

				normal.normalize();

				// return the hit
				hit.set(this, root, normal);
				return hit;
			}
		}

        // No intersection found
        return null;		
    }
}
