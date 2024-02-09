package ray.light;

import ray.HitRecord;
import ray.Ray;
import ray.Scene;
import ray.math.Color;
import ray.math.Point3;
import ray.math.Vector3;

/**
 * This class represents a basic point light which is infinitely small and emits
 * a constant power in all directions. This is a useful idealization of a small
 * light emitter.
 *
 * @author ags
 */
public class PointLight extends Light {
	
	/** Where the light is located in space. */
	public final Point3 position = new Point3();
	public void setPosition(Point3 position) { this.position.set(position); }
	
	/** How bright the light is. */
	public final Color intensity = new Color(1, 1, 1);
	public void setIntensity(Color intensity) { this.intensity.set(intensity); }
	
	/**
	 * Default constructor.  Produces a unit intensity light at the origin.
	 */
	public PointLight() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "point light: " + position + " " + intensity;
	}

	//TODO: this
	public Color illuminate(Ray ray, HitRecord hitRecord, Scene scene) 
	{
		throw new UnsupportedOperationException();
		
	}
}