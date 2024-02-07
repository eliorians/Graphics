package ray.surface;

import ray.HitRecord;
import ray.Ray;
import ray.math.Point3;
import ray.math.Vector3;

public class Ellipsoid extends Surface {
    /** The center of the ellipsoid. */
	protected final Point3 center = new Point3();
	public void setCenter(Point3 center) { this.center.set(center); }

    /** Length of axis a. */
	protected double a = 6;
	public void setA(double a) { this.a = a; }

    /** Length of axis b. */
	protected double b = 3;
	public void setB(double b) { this.b = b; }

    /** Length of axis c. */
	protected double c = 4.5;
	public void setC(double c) { this.c = c; }

	/** Axis a. */
	protected final Vector3 aAxis = new Vector3(1, 0, 0);
	public void setAAxis(Vector3 aAxis) { this.aAxis.set(aAxis); this.aAxis.normalize(); }

	/** Axis b. */
	protected final Vector3 bAxis = new Vector3(0, 1, 0);
	public void setBAxis(Vector3 bAxis) { this.bAxis.set(bAxis); this.bAxis.normalize(); }

	/** Axis c. */
	protected final Vector3 cAxis = new Vector3(0, 0, 1);
	public void setCAxis(Vector3 cAxis) { this.cAxis.set(cAxis); this.cAxis.normalize(); }
    
	//TODO: this
    @Override
    public HitRecord hit(Ray ray, double t0, double t1) 
	{
        throw new UnsupportedOperationException();
    }
}
