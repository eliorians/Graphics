/**
 * 
 */
package ray.surface;

import ray.HitRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a plane as a point on the plane and the normal to the plane.
 * 
 * @author parryrm
 *
 */
public class Plane extends Surface {

	/* A point on the plane. */
	protected final Point3 point = new Point3();
	public void setPoint(Point3 point) { this.point.set(point); }

	/* The normal vector. */
	protected final Vector3 normal = new Vector3();
	public void setNormal(Vector3 normal) { this.normal.set(normal); }
	
	public Plane() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "plane " + point + " " + normal + " " + material + " end";
	}


	@Override
	public HitRecord hit(Ray ray, double t0, double t1) {
		HitRecord record = new HitRecord();

		/* Solution */
		Vector3 op = new Vector3();
		op.sub(this.point, ray.getOrigin());
		double t = op.dot(this.normal) / ray.getDirection().dot(this.normal); 
		if (t >= t0 && t < t1) {
			record.set(this, t, this.normal);
		}		
		/* End Solution */

		return record;
	}
}
