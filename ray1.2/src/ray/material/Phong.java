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
	
	@Override
	public Color shade(Vector3 l, Vector3 v, Vector3 n, Color intensity, double r) {

		//       diffuse        +      specular
		// kd I max(0, n · li)  +  ks I max(0, n · hi)^p

		//setup vars
		Vector3 h = new Vector3();
		h.add(l, v);
		h.normalize();

		//diffuse component= kd I max(0, n · li)
		//kd
		Color diffuse = new Color(diffuseColor);
		//lI
		diffuse.scale(intensity);
		//max(0, n · li)
		diffuse.scale(Math.max(0, n.dot(l)) / r / r);

		//specular component= ks I max(0, n · hi)^p
		//ks
		Color specular = new Color(specularColor);
		//I
		specular.scale(intensity);
		// max(0, n · hi)^p
		specular.scale(Math.pow(Math.max(0, n.dot(h)), exponent) / r / r);

		//add them together
		Color c = new Color(diffuse);
		c.add(specular);

		return c;
	}
}