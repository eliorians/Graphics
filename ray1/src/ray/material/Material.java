package ray.material;

import ray.math.Color;
import ray.math.Vector3;

/**
 * This interface specifies what is necessary for an object to be a material.
 * You will probably want to add a "shade" method to it.
 * @author ags
 */
public abstract class Material {
  /** The base color of the surface (diffuse and ambient color). */
  protected final Color color = new Color(1, 1, 1);
  public Color getColor() { return new Color(color); };
  public void setColor(Color color) { this.color.set(color); }

  /**
   * The material given to all surfaces unless another is specified.
   */
  public static final Material DEFAULT_MATERIAL = new Lambertian();
  
  // TODO: Maybe add an abstract material reflectance?
  
  /* Solution */
  public abstract Color evaluate(Vector3 l, Vector3 v, Vector3 n);
  /* End Solution */

}
