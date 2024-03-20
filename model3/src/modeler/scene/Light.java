package modeler.scene;

import java.io.PrintWriter;

import org.w3c.dom.Element;

import jgl.GL;
import jgl.GLU;
import jgl.glu.GLUquadricObj;
import modeler.MainFrame;
import modeler.ui.LightAttributePanel;

/**
 * Light object.  Also includes functions to map light objects to OpenGL lights
 * @author ags
 */
public class Light extends SceneNode { 

	private static final long serialVersionUID = 3688792483416912692L;

	public static final float AMBIENT_CONSTANT = 0.05f;
	public static final float DIFFUSE_CONSTANT = 0.4f;

	/** Constant light parameters */
	public final float[] ambient = new float[] { AMBIENT_CONSTANT, AMBIENT_CONSTANT, AMBIENT_CONSTANT, 1 };
	public final float[] diffuse = new float[] { DIFFUSE_CONSTANT, DIFFUSE_CONSTANT, DIFFUSE_CONSTANT, 1 };
	public final float[] specular = new float[] { 2.0f, 2.0f, 2.0f, 1.0f };
	public final float[] position = new float[] { 6.0f, 8.0f, 10.0f, 1.0f };

	/** Map of all 8 GL lights */
	protected final static boolean[] taken = { false, false, false, false, false, false, false, false };
	protected final static int[] ids = { GL.GL_LIGHT0, GL.GL_LIGHT1, GL.GL_LIGHT2, GL.GL_LIGHT3, GL.GL_LIGHT4, GL.GL_LIGHT5, GL.GL_LIGHT6, GL.GL_LIGHT7 };

	/** This lights ID and index in the GL map */
	public int id;
	protected int index = -1;

	/** quadric for drawing the light */
	protected transient GLUquadricObj quad;

	/**
	 * Required for IO
	 */
	protected Light() {
		this(-1);
	}

	/**
	 * Constructor
	 * @param newIndex
	 */
	private Light(int newIndex) {

		index = newIndex;
		if (index != -1) {
			id = ids[index];
		}
		diffuse[0] = DIFFUSE_CONSTANT * specular[0];
		diffuse[1] = DIFFUSE_CONSTANT * specular[1];
		diffuse[2] = DIFFUSE_CONSTANT * specular[2];
		ambient[0] = AMBIENT_CONSTANT * specular[0];
		ambient[1] = AMBIENT_CONSTANT * specular[1];
		ambient[2] = AMBIENT_CONSTANT * specular[2];
		setName("Light " + index);
	}

	public static synchronized void takeLight(Light light) {
		if (light.index < 0) {
			for (int ctr = 0; ctr < taken.length; ctr++) {
				if (taken[ctr] == false) {
					taken[ctr] = true;
					light.index = ctr;
					light.id = Light.ids[ctr];
					light.setName("Light " + ctr);
					return;
				}
			}			
		}
	}

	/**
	 * Create a new light which is on or off depending on enabled.
	 * @param enabled
	 * @return
	 */
	public static synchronized Light makeNewLight(boolean enabled) {

		if (!enabled) {
			return new Light(-1);
		}

		Light light = new Light();
		takeLight(light);
		if (light.index >= 0) {
			// if it was successfully assigned
			return light;
		}
		return null;
	}

	/**
	 * Enable the lights in the scene
	 */
	public static synchronized void turnOnLights(GL gl) {

		for (int ctr = 0; ctr < taken.length; ctr++) {
			if (taken[ctr]) {
				gl.glEnable(ids[ctr]);
			}
			else {
				gl.glDisable(ids[ctr]);
			}
		}
	}

	/**
	 * Unmap a light from the OpenGL map
	 * @param l
	 */
	public static synchronized void returnLight(Light l) {

		taken[l.index] = false;
	}

	/**
	 * Draws a light
	 */
	public void draw(GL gl, GLU glu, Camera cam) {

		if(quad == null) {
			quad = glu.gluNewQuadric();
		}  

		float scale = 0.05f * cam.getHeight();
		gl.glPushAttrib(GL.GL_LIGHTING_BIT);
		gl.glPushAttrib(GL.GL_POLYGON_BIT);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glPushMatrix();
		gl.glTranslatef(position[0], position[1], position[2]);
		gl.glScalef(scale, scale, scale);
		gl.glColor3f(0.8f,0.7f,0.1f);
		gl.glColor3f(0.8f,0.7f,0.1f);
		glu.gluSphere(quad, 1, 4, 2);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glLineWidth(1f);
		gl.glScalef(1.03f, 1.03f, 1.03f);
		gl.glColor3f(0,0,0);
		glu.gluSphere(quad, 1, 4, 2);
		gl.glLineWidth(1);
		gl.glPopMatrix();
		gl.glPopAttrib();
		gl.glPopAttrib();
	}

	/**
	 * @see modeler.scene.SceneNode#exportMainData(java.util.HashMap, java.io.PrintWriter)
	 */
	protected void exportMainData(PrintWriter pw, String indent) {
		String attrs = "name=\"" + name + "\"" +
				" position=\"" + position[0] + " " + position[1] + " " + position[2] + "\"" + 
				" ambient=\"" + ambient[0] + " " + ambient[1] + " " + ambient[2] + "\"" +
				" diffuse=\"" + diffuse[0] + " " + diffuse[1] + " " + diffuse[2] + "\"" +
				" specular=\"" + specular[0] + " " + specular[1] + " " + specular[2] + "\"";
		String c = getClass().getCanonicalName();

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

	public float[] getAmbient() {
		return ambient;
	}

	public float[] getDiffuse() {
		return diffuse;
	}

	public float[] getSpecular() {
		return specular;
	}

	public float[] getPosition() {
		return position;
	}

	public static void resetLights() {
		for (int ctr = 0; ctr < taken.length; ctr++) {
			taken[ctr] = false;
		}
	}
	@Override
	protected void readXMLData(Element node) {
		takeLight(this);
		if (index != -1) {
			id = ids[index];
		}
		String[] t = node.getAttribute("position").split(" ");
		for (int i = 0; i < t.length; i++) {
			position[i] = Float.parseFloat(t[i]);
		}
		t = node.getAttribute("ambient").split(" ");
		for (int i = 0; i < t.length; i++) {
			ambient[i] = Float.parseFloat(t[i]);
		}
		t = node.getAttribute("diffuse").split(" ");
		for (int i = 0; i < t.length; i++) {
			diffuse[i] = Float.parseFloat(t[i]);
		}
		t = node.getAttribute("specular").split(" ");
		for (int i = 0; i < t.length; i++) {
			specular[i] = Float.parseFloat(t[i]);
		}
	}
	public void setupAttributePanels(MainFrame mf) {
		this.setUserObject(new LightAttributePanel(mf, this));
	}

}
