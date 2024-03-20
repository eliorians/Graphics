package modeler.shape;

import org.w3c.dom.Element;

public class Torus extends Shape {
    // aspect ratio = R / r
    protected float aspectRatio = 2.333f;

	/**
	 * Required for IO
	 */
	public Torus() {}

	@Override
	public void readXMLData(Element node) {
		super.readXMLData(node);
		aspectRatio = Float.parseFloat(node.getAttribute("aspectRatio"));
	}

	/**
	 * @see modeler.shape.Shape#buildMesh()
	 */
	public void buildMesh() {
		// TODO: Part 2: Implement this method
		// throw new UnsupportedOperationException();
	}



}
