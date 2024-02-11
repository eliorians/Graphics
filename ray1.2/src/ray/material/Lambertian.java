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
	protected final Color diffuseColor = new Color(1, 0, 0);
	public Color getDiffuseColor() { return new Color(diffuseColor); };
	public void setDiffuseColor(Color color) { this.diffuseColor.set(color); }

	// public void setColor(Color color) { setAmbientColor(color); setDiffuseColor(color); }

  
	public Lambertian() { }	

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "lambertian: " + ambientColor + " " + diffuseColor + " end";
	}
	
	//(DIFFUSE - reflects in all directions)
	public Color shade(Vector3 l, Vector3 v, Vector3 n) 
	{
		Color diffuse = new Color(ambientColor);
		diffuse.scale(diffuseColor);
	
		return diffuse;
	}
}
