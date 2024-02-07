package ray.light;

import ray.HitRecord;
import ray.math.Color;
import ray.Ray;
import ray.Scene;

/**
 * This class represents a basic point light which is infinitely small and emits
 * a constant power in all directions. This is a useful idealization of a small
 * light emitter.
 *
 * @author ags
 */
public class AmbientLight extends Light {
	
	/** How bright the light is. */
	public final Color intensity = new Color(1, 1, 1);
	public void setIntensity(Color intensity) { this.intensity.set(intensity); }
	
	/**
	 * Default constructor.  Produces a unit intensity light at the origin.
	 */
	public AmbientLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "ambient light: " + intensity;
	}

	@Override
	public Color illuminate(Ray ray, HitRecord hitRecord, Scene scene) {
		Color ka = hitRecord.getSurface().getMaterial().getAmbientColor();
		Color ia = intensity;
		Color result = new Color(ka);
		result.scale(ia);
		return result;
	}

}