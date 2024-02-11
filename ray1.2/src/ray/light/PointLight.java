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

	@Override
	public Color illuminate(Ray ray, HitRecord hitRecord, Scene scene) {
		Color c = new Color();

		//compute direction towards the viewer
		Vector3 v = new Vector3(ray.getDirection());
		v.scale(-1);
		v.normalize();

		//compute ray intersection
		Point3 x = ray.evaluate(hitRecord.getT());
		
		//compute vector from the surface point to the light position
		Vector3 l = new Vector3();
		l.sub(position, x);
		double r = l.length();	//distance from the surface point to the light
		l.normalize();
		
		// cast shadow ray toward view point
		Ray shadowRay = new Ray(x, l);
		HitRecord shadowHit = scene.getGroup().hit(shadowRay, 1e-4, Double.POSITIVE_INFINITY);

		//compute illumination if nothing in the way
		if (shadowHit == null || shadowHit.getT() >= r)
		{	
			//calculate the illumination due to the point light source
			Vector3 n = hitRecord.getNormal();
			Color irradiance = new Color(intensity);
			irradiance.scale(Math.max(0, n.dot(l)) / r / r);
			Color reflectanceCoefficient = hitRecord.getSurface().getMaterial().shade(l, v, n);

			//multiply by materials reflectance coefficeint
			Color reflectedLight = new Color(irradiance);
			reflectedLight.scale(reflectanceCoefficient);
			c = reflectedLight;
		}

		// otherwise return black
		return c;
	}
}