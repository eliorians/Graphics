package ray;
import ray.surface.Surface;
import ray.math.Vector3;

public class HitRecord {
	protected Surface surface;
	protected double t;
	protected Vector3 normal;
	
	public HitRecord() {
		this.surface = null;
		this.t = Double.POSITIVE_INFINITY;
		this.normal = new Vector3();
	}
	public HitRecord(double t) {
		this.surface = null;
		this.t = t;
		this.normal = new Vector3();
	}

	public void set(Surface surface, double t, Vector3 normal) {
		this.surface = surface;
		this.t = t;
		if (normal != null) {
			this.normal.set(normal);
		} else {
			this.normal = normal;
		}
	}
	
	public Surface getSurface() {
		return surface;
	}	
	public Vector3 getNormal() {
		return new Vector3(normal);
	}
	public double getT() {
		return t;
	}
	
}
