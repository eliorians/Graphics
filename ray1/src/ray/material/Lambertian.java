package ray.material;

import ray.math.Color;
import ray.math.Vector3;

/**
 * A Lambertian material scatters light equally in all directions. Reflectance coefficient is
 * a constant
 *
 * @author ags
 */
public class Lambertian extends Material {
	
	public Lambertian() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "lambertian: " + color + " end";
	}
	
	// TODO: Handle lambertian material reflectance
	/* Solution */
	public Color evaluate(Vector3 l, Vector3 v, Vector3 n) {
		return new Color(color);
	}
	/* End Solution */
}
