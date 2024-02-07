package ray.light;

import ray.HitRecord;
import ray.Ray;
import ray.Scene;
import ray.math.Color;

/**
 * This class represents a basic point light which is infinitely small and emits
 * a constant power in all directions. This is a useful idealization of a small
 * light emitter.
 *
 * @author ags
 */
public abstract class Light {
	public abstract Color illuminate(Ray ray, HitRecord hitRecord, Scene scene);
}