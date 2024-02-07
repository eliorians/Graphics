package ray.surface;

import ray.HitRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Box extends Surface {
	
	/* The corner of the box with the smallest x, y, and z components. */
	protected final Point3 minPt = new Point3();
	public void setMinPt(Point3 minPt) { this.minPt.set(minPt); }

	/* The corner of the box with the largest x, y, and z components. */
	protected final Point3 maxPt = new Point3();
	public void setMaxPt(Point3 maxPt) { this.maxPt.set(maxPt); }

	public Box() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "box " + minPt + " " + maxPt + " " + material + " end";
	}
	
	/* Solution */
	public Vector3 computeNormal(Point3 p) {
		Vector3 n = new Vector3();
		Vector3 v = new Vector3();
		v.sub(p, this.maxPt);
		double above = Double.NEGATIVE_INFINITY;
		if (v.x > above) {
			above = v.x;
			n.set(1.0, 0.0, 0.0);
		}
		if (v.y > above) {
			above = v.y;
			n.set(0.0, 1.0, 0.0);
		}
		if (v.z > above) {
			above = v.z;
			n.set(0.0, 0.0, 1.0);
		}
		v.sub(p, this.minPt);
		if (-v.x > above) {
			above = -v.x;
			n.set(-1.0, 0.0, 0.0);
		}
		if (-v.y > above) {
			above = -v.y;
			n.set(0.0, -1.0, 0.0);
		}
		if (-v.z > above) {
			above = -v.z;
			n.set(0.0, 0.0, -1.0);
		}
		return n;
	}

	@Override
	public HitRecord hit(Ray r, double t0, double t1) {
		HitRecord result = new HitRecord(Double.POSITIVE_INFINITY);
		double txmin = (this.minPt.x - r.getOrigin().x) / r.getDirection().x;
		double tymin = (this.minPt.y - r.getOrigin().y) / r.getDirection().y;
		double tzmin = (this.minPt.z - r.getOrigin().z) / r.getDirection().z;
		double txmax = (this.maxPt.x - r.getOrigin().x) / r.getDirection().x;
		double tymax = (this.maxPt.y - r.getOrigin().y) / r.getDirection().y;
		double tzmax = (this.maxPt.z - r.getOrigin().z) / r.getDirection().z;
		
		double txenter = Math.min(txmin, txmax);
		double txexit = Math.max(txmin,  txmax);
		double tyenter = Math.min(tymin, tymax);
		double tyexit = Math.max(tymin,  tymax);
		double tzenter = Math.min(tzmin, tzmax);
		double tzexit = Math.max(tzmin,  tzmax);
		
		double tenter = Math.max(txenter, Math.max(tyenter, tzenter));
		double texit = Math.min(txexit, Math.min(tyexit, tzexit));
		
		if (tenter < texit) {
			if (tenter >= t0 && tenter < t1) {
				Vector3 n = this.computeNormal(r.evaluate(tenter));
				result.set(this, tenter, n);
			} else if (texit >= t0 && texit < t1) {
				Vector3 n = this.computeNormal(r.evaluate(texit));
				result.set(this, texit, n);
			}
		}
		return result;
	}
	/* End Solution */

}
