package ray.material;

import ray.math.Color;
import ray.math.Vector3;

/**
 * A Phong material. Uses the Blinn-Phong model from the textbook.
 *
 * @author ags
 */
public class Phong extends Material {
	
	/** The color of the surface (specular reflection). */
	protected final Color specularColor = new Color(1, 1, 1);
	public void setSpecularColor(Color specularColor) { this.specularColor.set(specularColor); }
	
	/** The exponent controlling the sharpness of the specular reflection. */
	protected double exponent = 1.0;
	public void setExponent(double exponent) { this.exponent = exponent; }
	
	public Phong() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "phong " + color + " " + specularColor + " " + exponent + " end";
	}
	
	// TODO: handle phong material reflectance
	/* Solution */
	@Override
	public Color evaluate(Vector3 l, Vector3 v, Vector3 n) {
		Vector3 h = new Vector3();
		h.add(l, v);
		h.normalize();
		Color diffusePart = this.color;
		Color specularPart = new Color(specularColor); 
		specularPart.scale(Math.pow(Math.max(0, n.dot(h)), exponent));
		Color reflectanceCoefficient = new Color(diffusePart);
		reflectanceCoefficient.add(specularPart);
		return reflectanceCoefficient;
	}
	/* End Solution */

}
