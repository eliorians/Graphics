package ray.surface;

import java.util.ArrayList;

import ray.HitRecord;
import ray.Ray;

public class Group extends Surface {
	protected ArrayList<Surface> surfaces = new ArrayList<Surface>();
	
	public void add(Surface toAdd) {
		this.surfaces.add(toAdd);
	}
	
	public String toString() {
		return "group of " + surfaces.size() + " surfaces";
	}

	@Override
	public HitRecord hit(Ray ray, double t0, double t1) {

		HitRecord hit = null;
		double closestT = Double.POSITIVE_INFINITY;

		//call hit on every surface and return smallest T value
		for (Surface surface : surfaces) {

			HitRecord surfaceHit = surface.hit(ray, t0, t1);
			if (surfaceHit != null && surfaceHit.getT() < closestT && surfaceHit.getT() >= t0 && surfaceHit.getT() <= t1) {
				closestT = surfaceHit.getT();
				hit = surfaceHit;
			}

		}

		return hit;
	}

}
