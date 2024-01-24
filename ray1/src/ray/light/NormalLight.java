package ray.light;

import ray.HitRecord;
import ray.Ray;
import ray.Scene;
import ray.math.Color;
import ray.math.Vector3;

/**
 * This class represents a basic point light which is infinitely small and emits
 * a constant power in all directions. This is a useful idealization of a small
 * light emitter.
 *
 * @author ags
 */
public class NormalLight extends Light {
	
	/**
	 * Default constructor.  Produces a unit intensity light at the origin.
	 */
	public NormalLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "normal light end";
	}

	// TODO: Handle normal light "illumination"
	/* Solution */
	@Override
	public Color illuminate(Ray ray, HitRecord hitRecord, Scene scene) {
		Vector3 n = hitRecord.getNormal();
		return new Color((n.x+1.)/2., (n.y+1.)/2., (n.z+1.)/2.);
	}
	/* End Solution */

}