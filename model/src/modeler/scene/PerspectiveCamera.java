package modeler.scene;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.w3c.dom.Element;

import modeler.GLJPanel;
import modeler.MainFrame;
import modeler.ui.ItemAttributePanel;

/**
 * @author ags
 */
public class PerspectiveCamera extends Camera {

	private static final long serialVersionUID = 3256437006337718072L;

	//Default position of the camera
	protected static final Point3f DEFAULT_EYE = new Point3f(5, 5, 3);
	protected static final Point3f DEFAULT_TARGET = new Point3f(0, 0, 0);
	protected static final Vector3f DEFAULT_UP = new Vector3f(0, 1, 0);
	private static final Vector3f VERTICAL = new Vector3f(0,1,0);

	/** The Y field of view of the camera in degrees */
	float fovy;

	//The attribute panel
	protected transient AttributePanel attribs;

	/**
	 * Required for IO
	 */
	public PerspectiveCamera() {}

	/**
	 * Constructor
	 * @param newNear
	 * @param newFar
	 * @param newFovy
	 * @param newName
	 */
	public PerspectiveCamera(float newNear, float newFar, float newFovy, String newName) {

		super(DEFAULT_EYE, DEFAULT_TARGET, DEFAULT_UP, newNear, newFar, newName);
		fovy = newFovy;
		updateFrame();

	}


	public PerspectiveCamera(Point3f newEye, Point3f newTarget, Vector3f newUp, float newNear, float newFar, float newFovy, String newName) {

		super(newEye, newTarget, newUp, newNear, newFar, newName);
		fovy = newFovy;
		updateFrame();

	}

	/**
	 * @see modeler.scene.Camera#updateFrame()
	 */
	public void updateFrame() {

		Vector3f negGaze = new Vector3f(eye);
		negGaze.sub(target);
		negGaze.normalize();

		up.normalize();
		right.cross(VERTICAL, negGaze);
		right.normalize();
		up.cross(negGaze, right);

	}

	/**
	 * Returns the height of the viewing frustum,
	 * evaluated at the target point.
	 */
	public float getHeight() {
		float dist = eye.distance(target);
		return (float)(Math.tan(Math.toRadians(fovy/2.0)) * dist);
	}
	
	/**
	 * Returns the field of view in the y-direction.
	 */
	public float getFovY() {
		return fovy;
	}

	/**
	 * Perform an orbit move of this camera
	 * @param mouseDelta
	 */
	public void orbit(Vector2f mouseDelta) {

		Vector3f negGaze = new Vector3f(eye);
		negGaze.sub(target);
		float dist = negGaze.length();
		negGaze.normalize();

		float azimuth = (float) Math.atan2(negGaze.x, negGaze.z);
		float elevation = (float) Math.atan2(negGaze.y, Math.sqrt(negGaze.x * negGaze.x + negGaze.z * negGaze.z));
		azimuth = (azimuth - mouseDelta.x) % (float) (2 * Math.PI);
		elevation = (float) Math.max(-Math.PI * 0.495, Math.min(Math.PI * 0.495, (elevation - mouseDelta.y)));

		negGaze.set((float) (Math.sin(azimuth) * Math.cos(elevation)), (float) Math.sin(elevation), (float) (Math.cos(azimuth) * Math.cos(elevation)));
		negGaze.normalize();

		eye.scaleAdd(dist, negGaze, target);

		updateFrame();
	}

	/**
	 * @see modeler.scene.Camera#zoom(float)
	 */
	public void zoom(float d) {

		dolly(-d);
	}

	/**
	 * Dolly this camera 
	 * @param d
	 */
	public void dolly(float d) {

		Vector3f gaze = new Vector3f(target);
		gaze.sub(eye);
		double dist = gaze.length();
		gaze.normalize();
		d *= 6;

		if (dist + d > 0.01)
			eye.scaleAdd(-d, gaze, eye);
	}

	/**
	 * @see modeler.scene.Camera#convertMotion(javax.vecmath.Vector2f, javax.vecmath.Vector3f)
	 */
	public void convertMotion(Vector2f delta, Vector3f output) {
		// note: this method is approximate.  Loses accuracy away from the center of the viewport
		output.scale(-delta.x * aspect * getHeight(), right);
		output.scaleAdd(-delta.y * getHeight(), up, output);
	}


	/**
	 * @see modeler.scene.Camera#doProjection(javax.media.opengl.GLAutoDrawable)
	 */
	public void doProjection(GLJPanel canvas) {

		canvas.getGLU().gluPerspective(fovy, aspect, near, far);
	}

	/**
	 * @see modeler.scene.SceneNode#getAttributePanel(modeler.MainFrame)
	 */
	public ItemAttributePanel getAttributePanel(MainFrame mainFrame) {

		if (attribs == null) {
			attribs = new AttributePanel(mainFrame, this);
		}
		return attribs;
	}

	/**
	 * The attribute panel for this camera
	 * @author arbree
	 * Oct 21, 2005
	 * PerspectiveCamera.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class AttributePanel extends ItemAttributePanel implements ActionListener {

		private static final long serialVersionUID = 3256437006337718072L;

		JFormattedTextField TFfov;

		//    protected Camera host;

		/**
		 * Inherited constuctor
		 * @param mf
		 */
		protected AttributePanel(MainFrame mf, PerspectiveCamera cam) {
			super(mf);
			//      host = cam;
		}

		public void initialize() {

			JPanel panel = new JPanel();
			TFfov = new JFormattedTextField(new DecimalFormat());
			panel.setLayout(new GridLayout(1, 2));
			panel.add(new JLabel("Vert. FOV: "));
			panel.add(TFfov);
			this.setLayout(new BorderLayout());
			this.add(panel, BorderLayout.NORTH);
			TFfov.addActionListener(this);
		}

		public void refresh() {

			TFfov.setText("" + fovy);
		}

		public void actionPerformed(ActionEvent e) {

			JFormattedTextField tf = (JFormattedTextField) e.getSource();
			fovy = Float.parseFloat(tf.getText());

			mf.refresh();
		}
	}

	@Override
	protected void exportMainData(PrintWriter pw, String indent) {
		String attrs = "name=\"" + name + "\"" +
				" eye=\"" + eye.x + " " + eye.y + " " + eye.z + "\"" + 
				" target=\"" + target.x + " " + target.y + " " + target.z + "\"" + 
				" up=\"" + up.x + " " + up.y + " " + up.z + "\"" +
				" fovy=\"" + fovy + "\"" +
				" near=\"" + near + "\"" +
				" far=\"" + far + "\"";
		String c = this.getClass().getCanonicalName();

		if (this.getChildCount()==0) {
			pw.println(indent + "<" + c + " " + attrs + "/>");
		} else {
			// this shouldn't happen
			assert false;
			pw.println(indent + "<" + c + " " + attrs + ">");
			super.exportMainData(pw, indent + "  ");
			pw.println(indent + "</" + c + ">");		
		}
	}

	@Override
	protected void readXMLData(Element node) {
		String[] t = node.getAttribute("eye").split(" ");
		eye.set(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
		t = node.getAttribute("target").split(" ");
		target.set(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
		t = node.getAttribute("up").split(" ");
		up.set(Float.parseFloat(t[0]), Float.parseFloat(t[1]), Float.parseFloat(t[2]));
		fovy = Float.parseFloat(node.getAttribute("fovy"));
		near = Float.parseFloat(node.getAttribute("near"));
		far = Float.parseFloat(node.getAttribute("far"));
		updateFrame();
	}


}
