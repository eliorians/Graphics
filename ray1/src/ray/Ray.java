package ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Ray {
	protected Point3 origin;
	protected Vector3 direction;
	
	public Ray(Point3 o, Vector3 d) {
		this.origin = new Point3(o);
		this.direction = new Vector3(d);
		// make sure direction is unit length.
		this.direction.normalize();
	}
	
	public Point3 getOrigin() {
		return new Point3(this.origin);
	}
	
	public Vector3 getDirection() {
		return new Vector3(this.direction);
	}
	
	public Point3 evaluate(double t) {
		Point3 p = new Point3(this.origin);
		p.scaleAdd(t, this.direction);
		return p;
	}
}
