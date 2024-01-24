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

	// TODO: add abstract intersection method
	/* Solution */
	public abstract HitRecord hit(Ray ray, double t0, double t1);
	/* End Solution */
	
}
