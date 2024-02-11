package ray.material;

import ray.math.Color;
import ray.math.Vector3;

/**
 * A Phong material. Uses the Blinn-Phong model from the textbook.
 *
 * @author ags
 */
public class Phong extends Lambertian {
	
	protected final Color specularColor = new Color(0, 1, 0);
	public Color getSpecularColor() { return new Color(specularColor); };
	public void setSpecularColor(Color color) { this.specularColor.set(color); }
  
	protected double exponent = 1.0;
    public void setExponent(double exponent) { this.exponent = exponent; }
    public double getExponent() { return this.exponent; }

	// public void setColor(Color color) {setAmbientColor(color); setDiffuseColor(color); setSpecularColor(color); }

    public Phong() { }
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "phong " + ambientColor + " " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}
	
	//TODO: this (SPECULAR)
	@Override
	public Color shade(Vector3 l, Vector3 v, Vector3 n) {
		//compute halfway vector
		Vector3 h = new Vector3();
		h.add(l, v);
		h.normalize();

		//compute diffuse
		Color diffusePart = this.diffuseColor;

		//compute specular
		Color specularPart = new Color(specularColor); 
		specularPart.scale(Math.pow(Math.max(0, n.dot(h)), exponent));

		//final
		Color reflectanceCoefficient = new Color(diffusePart);
		reflectanceCoefficient.add(specularPart);

		return reflectanceCoefficient;

	}
}

