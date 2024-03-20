package modeler.scene;

import javax.swing.Icon;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import modeler.GLJPanel;
import modeler.MainFrame;

/**
 * Base class of all cameras
 * @author ags
 */
public abstract class Camera extends SceneNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1479549961968914444L;

	//All the camera parameters
	public float aspect = 1;
	public float near;
	public float far;
	public final Point3f eye = new Point3f();
	public final Point3f target = new Point3f();
	public final Vector3f up = new Vector3f();
	public final Vector3f right = new Vector3f();

	//Icon to use in the tree display
	protected transient Icon icon = null;

	/**
	 * Required by IO
	 *
	 */
	public Camera() {} 

	/**
	 * Creates a camera from input parameters
	 * @param newEye
	 * @param newTarget
	 * @param newUp
	 * @param newNear
	 * @param newFar
	 * @param newName
	 */
	public Camera(Point3f newEye, Point3f newTarget, Vector3f newUp, float newNear, float newFar, String newName) {

		up.set(newUp);
		eye.set(newEye);
		target.set(newTarget);
		near = newNear;
		far = newFar;

		name = newName;
	}

	/**
	 * Creates the camera from the current camera parameters
	 */
	public void updateFrame() {

		Vector3f negGaze = new Vector3f(eye);
		negGaze.sub(target);
		negGaze.normalize();

		up.normalize();
		right.cross(up, negGaze);
		right.normalize();
		up.cross(negGaze, right);

	}

	/**
	 * Return the eye point
	 * @return
	 */
	public Point3f getEye() {
		return eye;
	}

	/**
	 * Return the target point
	 * @return
	 */
	public Point3f getTarget() {
		return target;
	}

	/**
	 * Return the up vector
	 * @return
	 */
	public Vector3f getUp() {
		return up;
	}

	/**
	 * Return the right vector
	 * @return
	 */
	public Vector3f getRight() {

		return right;
	}

	/**
	 * Returns the height of this camera's image
	 * @return
	 */
	public abstract float getHeight();

	/**
	 * Set the aspect ratio
	 * @param d
	 */
	public void setAspect(float d) {
		aspect = d;
	}

	/**
	 * Move the camera eye and target by the given vector
	 * @param delta
	 */
	public void translate(Vector3f delta) {
		eye.add(delta);
		target.add(delta);
	}

	/**
	 * Convert a 2D mouse delta into a 3D translation of the camera
	 * @param delta
	 * @param output
	 */
	public abstract void convertMotion(Vector2f delta, Vector3f output);

	/**
	 * Zoom the camera a distance d
	 * @param d
	 */
	public abstract void zoom(float d);

	/**
	 * Flips the camera
	 */
	public void flip() {
		Vector3f offset = new Vector3f();
		offset.sub(eye, target);
		offset.negate();
		eye.add(target, offset);
	}

	/**
	 * Translate the camera in response to the given mouseDelta
	 * @param mouseDelta
	 */
	public void track(Vector2f mouseDelta) {

		Vector3f temp = new Vector3f();
		convertMotion(mouseDelta, temp);

		eye.add(temp);
		target.add(temp);

	}

	/**
	 * Set the OpenGL project matrix for this camera
	 * @param d
	 */
	public abstract void doProjection(GLJPanel canvas);

	/**
	 * Set the model view matrix for this camera
	 * @param d
	 */
	public void doModelview(GLJPanel canvas) {
		canvas.getGLU().gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
	}

	/**
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {

		return false;

	}

	/**
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	public boolean isLeaf() {

		return true;

	}

	public void setupAttributePanels(MainFrame mf) {
		// don't do it for cameras
	}

}