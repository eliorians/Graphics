package ray.surface;

import ray.HitRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
	
	/** The center of the sphere. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }
	
	/** The radius of the sphere. */
	protected double radius = 1.0;
	public void setRadius(double radius) { this.radius = radius; }
	
	public Sphere() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "sphere " + center + " " + radius + " " + material + " end";
	}

	// TODO: handle sphere intersection

	/* Solution */
	public Vector3 computeNormal(Point3 p) {
		Vector3 n = new Vector3();
		n.sub(p, this.center);
		n.normalize();
		return n;
	}

	@Override
	public HitRecord hit(Ray r, double t0, double t1) {
		HitRecord result = new HitRecord(Double.POSITIVE_INFINITY);
		double a = r.getDirection().lengthsquared();
		Vector3 co = new Vector3();
		co.sub(r.getOrigin(), this.center);
		double b = 2.0 * co.dot(r.getDirection());
		double c = co.lengthsquared() - this.radius*this.radius;
		double discriminant = b * b - 4 * a * c;

		if (discriminant == 0.0) {
			double t = -b / 2.0 / a;
			if (t >= t0 && t < t1) {
				result.set(this, t, this.computeNormal(r.evaluate(t)));
			}
		} else if (discriminant > 0.0) {
			double ta = (-b - Math.sqrt(discriminant)) / (2 * a);
			if (ta >= t0 && ta < t1) {
				result.set(this, ta, this.computeNormal(r.evaluate(ta)));
			} else {
				double tb = (-b + Math.sqrt(discriminant)) / (2 * a);
				if (tb >= t0 && tb < t1) {
					result.set(this, tb, this.computeNormal(r.evaluate(tb)));
				}
			}
		}
		return result;
	}
	/* End Solution */
}
