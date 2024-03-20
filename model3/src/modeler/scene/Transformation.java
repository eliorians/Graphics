package modeler.scene;

import java.io.PrintWriter;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import jgl.GL;
import jgl.GLU;
import modeler.MainFrame;
import modeler.ui.TransformationAttributePanel;

/**
 * @author andru Transformation provides an interface to a 3D transformation
 *         matrix. It allows independent access to the translational,
 *         rotational, and scaling properties of the matrix. By convention,
 *         rotations are performed around the axes in X,Y,Z order.
 */

public class Transformation extends SceneNode {

	private static final long serialVersionUID = 3256437006337718072L;

	// Individual Translation, Rotation, and Scaling components of this transformation
	public final Vector3f R = new Vector3f(0, 0, 0); // Stored in degrees
	public final Vector3f S = new Vector3f(1, 1, 1);
	public final Vector3f T = new Vector3f(0, 0, 0);

	//Constants defining the coordinate axes
	public static final Vector3f xAxis = new Vector3f(1, 0, 0);
	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	public static final Vector3f zAxis = new Vector3f(0, 0, 1);

	/**
	 * Required for IO
	 */
	public Transformation() {}

	/**
	 * Constructor
	 * @param name
	 */
	public Transformation(String name) {

		this.name = name;
	}

	/**
	 * Copy constructor
	 * @param copy
	 */
	public Transformation(Transformation copy) {

		this.R.set(copy.R);
		this.S.set(copy.S);
		this.T.set(copy.T);
	}

	/**
	 * Converts the given point or vector from local space to world space 
	 */
	public void toWorld(Vector3f v, Vector3f outv, boolean isPoint) {
		Vector3f result = new Vector3f();
		transform(v, result, isPoint);
		if (parent == null) {
			outv.set(result);
		} else {
			((Transformation)parent).toWorld(result, outv, isPoint);
		}
	}

	/**
	 * Converts the given point v from local space to world space 
	 */
	public void toWorld(Vector3f v, Vector3f outv) {
		toWorld(v, outv, true);
	}

	/**
	 * Transforms the given point or vector by this transformation
	 */
	Matrix3f tempMatrix = new Matrix3f();
	public void transform(Vector3f v, Vector3f outv, boolean isPoint) {
		outv.set(v);

		// Scale
		outv.set(outv.x * S.x, outv.y * S.y, outv.z * S.z);

		// Rotate
		tempMatrix.rotX((float)Math.toRadians(R.x));
		tempMatrix.transform(outv);
		tempMatrix.rotY((float)Math.toRadians(R.y));
		tempMatrix.transform(outv);
		tempMatrix.rotZ((float)Math.toRadians(R.z));
		tempMatrix.transform(outv);

		if (isPoint) {
			// Translate
			outv.add(T);
		}
	}

	/**
	 * Transforms the given point by this transformation
	 */
	public void transform(Vector3f v, Vector3f outv) {
		transform(v, outv, true);
	}

	/**
	 * Sets the translation vector
	 * @param translation
	 */
	public void setTranslate(Vector3f translation) {

		T.set(translation);
	}

	/**
	 * Sets the scale vector
	 * @param scaling
	 */
	public void setScale(Vector3f scaling) {

		S.set(scaling);
	}

	/**
	 * Sets the rotation angle in degrees
	 * @param rotation
	 */
	public void setRotate(Vector3f rotation) {

		R.set(rotation);
	}

	/**
	 * Gets the translation vector
	 * @return
	 */
	public Vector3f getTranslate() {

		return new Vector3f(T);
	}

	/**
	 * Gets the scale vector
	 * @return
	 */
	public Vector3f getScale() {

		return new Vector3f(S);
	}

	/**
	 * Gets the rotation vector in degrees
	 * @return
	 */
	public Vector3f getRotate() {

		return new Vector3f(R);
	}

	/**
	 * @see modeler.scene.SceneNode#render(javax.media.opengl.GL)
	 */
	@Override
	public int render(GL gl, GLU glu) {
		int outCount = 0;

		//Push the current untranslated matrix
		gl.glPushMatrix();

		//Translate
		gl.glTranslatef(T.x, T.y, T.z); 

		//Rotation around axis (x,y,z in reverse order)
		gl.glRotatef(R.z, 0, 0, 1);
		gl.glRotatef(R.y, 0, 1, 0);
		gl.glRotatef(R.x, 1, 0, 0);

		// Scale
		gl.glScalef(S.x, S.y, S.z);

		// TODO: Part 1: fill in code before and after super.render to render children correctly
		outCount = super.render(gl, glu);

		//return to the original matrix for future operations
		gl.glPopMatrix();

		return outCount;
	}

	/**
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {

		return true;

	}

	/**
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	public boolean isLeaf() {

		return false;

	}

	public Vector3f getR() {
		return R;
	}

	public Vector3f getS() {
		return S;
	}

	public Vector3f getT() {
		return T;
	}

	protected void exportMainData(PrintWriter pw, String indent) {
		//Write our Transformation out
		String attrs = "name=\"" + name + "\"" +
				" T=\"" + T.x + " " + T.y + " " + T.z + "\"" + 
				" R=\"" + R.x + " " + R.y + " " + R.z + "\"" + 
				" S=\"" + S.x + " " + S.y + " " + S.z + "\"";
		String c = getClass().getCanonicalName();

		if (this.getChildCount()==0) {
			pw.println(indent + "<" + c + " " + attrs + "/>");
		} else {
			pw.println(indent + "<" + c + " " + attrs + ">");
			super.exportMainData(pw, indent + "  ");
			pw.println(indent + "</" + c + ">");
		}
	}

	@Override
	protected void readXMLData(Element node) {
		String[] t = node.getAttribute("T").split(" ");
		T.set(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
		t = node.getAttribute("R").split(" ");
		R.set(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
		t = node.getAttribute("S").split(" ");
		S.set(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));

	}

	public void setupAttributePanels(MainFrame mf) {
		this.setUserObject(new TransformationAttributePanel(mf, this));
		// do for children
		super.setupAttributePanels(mf);
	}
}
