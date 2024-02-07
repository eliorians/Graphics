
package ray.light;

import ray.HitRecord;
import ray.Ray;
import ray.Scene;
import ray.math.Color;

/**
 * This class represents a basic point light which is infinitely small and emits
 * a constant power in all directions. This is a useful idealization of a small
 * light emitter.
 *
 * @author ags
 */
public class ConstantLight extends Light {
	
	/**
	 * Default constructor.  Produces a unit intensity light at the origin.
	 */
	public ConstantLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "constant light end";
	}

	@Override
	public Color illuminate(Ray ray, HitRecord hitRecord, Scene scene) {
		return new Color(hitRecord.getSurface().getMaterial().getAmbientColor());
	}
}