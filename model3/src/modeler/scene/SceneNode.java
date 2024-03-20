package modeler.scene;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import jgl.GL;
import jgl.GLU;
import modeler.MainFrame;
import modeler.shape.Shape;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public abstract class SceneNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5512505287856413079L;

	/** The name of this node */
	public String name;

	/**
	 * Empty constructor required for I/O
	 */
	protected SceneNode() {

	}

	/**
	 * Sets the name of this node
	 * 
	 * @param name
	 */
	public void setName(String name) {

		this.name = name;

	}

	/**
	 * Render this node.  The current model view matrix is supplied.  Default just called 
	 * render on the children of this node
	 * @param gl
	 * @param currMat
	 */
	public int render(GL gl, GLU glu) {
		// call render for each child
		// and count the number of tris drawn
		int outCount = 0;
		for (int ctr = 0; ctr < getChildCount(); ctr++)
			outCount += ((SceneNode) getChildAt(ctr)).render(gl, glu);
		return outCount;
	}

	/**
	 * Export this file to data that can be read by the ray tracer
	 * @param fileName
	 * @throws FileNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public static void exportXML(PrintWriter pw, SceneNode root) throws FileNotFoundException {
		root.exportMainData(pw, "  ");
	}

	public static SceneNode readXML(Element node) {
		//Constructs an instance of the class to be read
		String className = (String) node.getNodeName();
		SceneNode currNode = null;
		Class<?> currClass;
		try {
			currClass  = Class.forName(className);
			currNode = (SceneNode) currClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		//Reads the data
		currNode.name = node.getAttribute("name");
		currNode.readXMLData(node);

		//Adds its children
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for(int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				currNode.add(readXML((Element) child));
			}
		}
		return currNode;
	}

	/**
	 * Exports this nodes data for the main xml output file
	 */
	protected void exportMainData(PrintWriter pw, String indent) {
		if(children != null ) {
			for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
				SceneNode currNode = (SceneNode) iter.next();
				currNode.exportMainData(pw, indent);
			}
		}
	}

	protected abstract void readXMLData(Element node);

	/**
	 * Rebuilds all meshes
	 */
	public void rebuild() {
		if(this instanceof Shape) {
			((Shape) this).mesh = null;
			return;
		}
		if(children != null ) {
			for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
				SceneNode currChild = (SceneNode) iter.next();
				currChild.rebuild();
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		return name;

	}

	public void setupAttributePanels(MainFrame mf) {
		if(children != null ) {
			for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
				SceneNode currChild = (SceneNode) iter.next();
				currChild.setupAttributePanels(mf);
			}
		}
	}
}
