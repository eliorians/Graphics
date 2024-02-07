package ray.surface;

import ray.material.Material;
import ray.HitRecord;
import ray.Ray;

/**
 * Abstract base class for all surfaces.  Provides access for shader and
 * intersection uniformly for all surfaces.
 *
 * @author ags
 */
public abstract class Surface {
	
	/** Shader to be used to shade this surface. */
	protected Material material = Material.DEFAULT_MATERIAL;
	public void setMaterial(Material material) { this.material = material; }
	public Material getMaterial() { return material; }

	/* Solution */
	public abstract HitRecord hit(Ray ray, double t0, double t1);
	/* End Solution */
	
	public double[] solveQuadratic(double a, double b, double c) {
		// assume that A  is a positive number
		double t1 = Double.POSITIVE_INFINITY;
		double t2 = Double.POSITIVE_INFINITY;
		double discriminant = b * b - 4 * a * c;

		if (discriminant == 0.0) {
			t1 = t2 = -b / 2.0 / a;
		} else if (discriminant > 0.0) {
			t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
			t2 = (-b + Math.sqrt(discriminant)) / (2 * a);
			if (t1 > t2) {
				double temp = t1;
				t1 = t2;
				t2 = temp;
			}
		}
		return new double[] {t1, t2};
	}
}
