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

		//localize ray direction
		double rayDx = d.dot(aAxis);
		double rayDy = d.dot(bAxis);
		double rayDz = d.dot(cAxis);
		Vector3 new_d = new Vector3(rayDx, rayDy, rayDz);
		d = new_d;

		//localize ray origin
		Vector3 centerToRay = new Vector3();
		centerToRay.sub(o, center);
		double rayOx = centerToRay.dot(aAxis);
		double rayOy = centerToRay.dot(bAxis);
		double rayOz = centerToRay.dot(cAxis);
		rayOx = rayOx + center.x;
		rayOy = rayOy + center.y;
		rayOz = rayOz + center.z;
		Point3 new_o = new Point3(rayOx, rayOy, rayOz);
		o = new_o;

		// make new localized ray
		Ray localRay = new Ray(o, d);

		//calc quadratic equation coefficents
		double A = d.x * d.x / (a * a) + d.y * d.y / (b * b) + d.z * d.z / (c * c);
		double B = 2 * (d.x * (o.x - center.x) / (a * a) + d.y * (o.y - center.y) / (b * b) + d.z * (o.z - center.z) / (c * c));
		double C = (o.x - center.x) * (o.x - center.x) / (a * a) + (o.y - center.y) * (o.y - center.y) / (b * b) + (o.z - center.z) * (o.z - center.z) / (c * c) - 1;
		
		//solve quadratic for t
		double[] roots = solveQuadratic(A, B, C);

		//see if it hits
		for (double root : roots) {
			if (root >= t0 && root <= t1) {

				// calc intersection point
				//Point3 intersectionPoint = ray.evaluate(root);
				Point3 intersectionPoint = localRay.evaluate(root);
				
				//calc localized normal
				Vector3 normal = new Vector3(
                2 * (intersectionPoint.x - center.x) / (a * a),
                2 * (intersectionPoint.y - center.y) / (b * b),
                2 * (intersectionPoint.z - center.z) / (c * c)
            	);
				normal.normalize();

				//calc globalized normal
				Vector3 globalNormal = new Vector3();
				globalNormal.x = normal.x * aAxis.x + normal.y * bAxis.x + normal.z * cAxis.x;
				globalNormal.y = normal.x * aAxis.y + normal.y * bAxis.y + normal.z * cAxis.y;
				globalNormal.z = normal.x * aAxis.z + normal.y * bAxis.z + normal.z * cAxis.z;
				globalNormal.normalize();
				normal = globalNormal;

				// return the hit
				hit.set(this, root, normal);
				return hit;
			}
		}

        // No intersection found
        return null;		
    }

}
