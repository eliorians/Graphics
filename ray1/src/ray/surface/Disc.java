/**
 * 
 */
package ray.surface;

import ray.HitRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Representation of a 2D disk by a center, radius and normal.
 * 
 * @author parryrm
 *
 */
public class Disc extends Plane {

	/* The radius. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	public Disc() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "disk " + point + " " + normal + " " + radius + " " + material + " end";
	}
	
	// TODO: Handle disc intersection and normal computation
	/* Solution */
	@Override
	public HitRecord hit(Ray ray, double t0, double t1) {
		HitRecord record = new HitRecord();
		record = super.hit(ray, t0, t1);
		if (record.getT() < Double.POSITIVE_INFINITY) {
			// check if we're inside disk
			Point3 p = ray.evaluate(record.getT());
			Vector3 d = new Vector3();
			d.sub(p, this.point);
			if (d.length() > this.radius) {
				record.set(null, Double.POSITIVE_INFINITY, null);
			}
		}
		/* End Solution */
		return record;
	}
	/* End Solution */

}
