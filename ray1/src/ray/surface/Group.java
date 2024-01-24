package ray.surface;

import java.util.ArrayList;

public class Group extends Surface {
	protected ArrayList<Surface> surfaces = new ArrayList<Surface>();
	
	public void add(Surface toAdd) {
		this.surfaces.add(toAdd);
	}
	
	public String toString() {
		return "group of " + surfaces.size() + " surfaces";
	}
}
