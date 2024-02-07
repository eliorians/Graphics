package ray.material;

import ray.math.Color;
import ray.math.Vector3;

/**
 * This interface specifies what is necessary for an object to be a material.
 * You will probably want to add a "shade" method to it.
 * @author ags
 */
public abstract class Material {
  protected final Color ambientColor = new Color(1, 1, 1);
  public Color getAmbientColor() { return new Color(ambientColor); };
  public void setAmbientColor(Color color) { this.ambientColor.set(color); }

  // public void setColor(Color color) { setAmbientColor(color); }

  /**
   * The material given to all surfaces unless another is specified.
   */
  public static final Material DEFAULT_MATERIAL = new Lambertian();

  /**
	 * @see Object#toString()
	 */
	public String toString() {
		
		return "material: " + ambientColor + " end";
	}
  
  public abstract Color shade(Vector3 l, Vector3 v, Vector3 n);

}
