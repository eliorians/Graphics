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

		for (Surface surface : surfaces) {
			//calculate the hit for each surface in the group
			HitRecord surfaceHit = surface.hit(ray, t0, t1);
			if (surfaceHit != null && surfaceHit.getT() < t1 && surfaceHit.getT() > t0) {
				//if surface hit, and the T value is at the front, then update closest hit and t1
				t1 = surfaceHit.getT();
				hit = surfaceHit;
			}

		}

		return hit;
	}

}
