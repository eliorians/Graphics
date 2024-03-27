package modeler.manip;

import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import jgl.GL;
import modeler.GLJPanel;
import modeler.scene.Transformation;

public class ScaleManip extends Manip {

	public ScaleManip() {} 
	
	public ScaleManip(Transformation t) {

		this.t = t;
	}

	public void dragged(Vector2f mousePosition, Vector2f mouseDelta) {
		// TODO: Part 3: Implement this method

		//get axis ray
		Point3f axisOrigin = new Point3f();
		Vector3f axisDirection = new Vector3f();
		computeAxisRay(axisOrigin, axisDirection);

		//get viewing ray for start position
		Point3f viewOriginStart = new Point3f();
		Vector3f viewDirectionStart = new Vector3f();
		computeViewingRay(mousePosition, viewOriginStart, viewDirectionStart);

		//get viewing ray for end position
		Point3f viewOriginEnd = new Point3f();
		Vector3f viewDirectionEnd = new Vector3f();
		Vector2f newMousePosition = new Vector2f(mousePosition.x + mouseDelta.x, mousePosition.y + mouseDelta.y);
		computeViewingRay(newMousePosition, viewOriginEnd, viewDirectionEnd);

		//get T values
		float tStart = computePseudointersection(viewOriginStart, viewDirectionStart, axisOrigin, axisDirection);
		float tEnd = computePseudointersection(viewOriginEnd, viewDirectionEnd, axisOrigin, axisDirection);
		float tDifference = tEnd - tStart;

		//scale selected axis by t value
		Vector3f translationAmount = new Vector3f();
		translationAmount.scale(tDifference, axisDirection);

		//update the translation component of the transformation
		Vector3f currentTranslation = t.getScale();
		currentTranslation.add(translationAmount);
		t.setScale(currentTranslation);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Render the manipulator
	////////////////////////////////////////////////////////////////////////////////////////////////////

	Vector3f skewX = new Vector3f();
	Vector3f skewY = new Vector3f();
	Vector3f skewZ = new Vector3f();
	public void glRender(GLJPanel canvas, double scale) {

		GL gl = canvas.getGL();
		//		GL2 gl = d.getGL().getGL2();
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glPushAttrib(GL.GL_LIGHTING_BIT);
		gl.glDisable(GL.GL_LIGHTING);

		gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
		gl.glDisable(GL.GL_DEPTH_TEST);

		gl.glPushMatrix();
		glDoLocation(gl, t);

		gl.glScaled(scale, scale, scale);
		glRenderRotation(gl, t);

		normalizedTransformedAxes(skewX, skewY, skewZ);

		gl.glPushMatrix();
		glRotateTo(gl,skewX);
		gl.glColor4d(0.8, 0, 0, 1);
		gl.glLoadName(PICK_X);
		glRenderBoxOnAStick(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		glRotateTo(gl,skewY);
		gl.glColor4d(0, 0.8, 0, 1);
		gl.glLoadName(PICK_Y);
		glRenderBoxOnAStick(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		glRotateTo(gl,skewZ);
		gl.glColor4d(0, 0, 0.8, 1);
		gl.glLoadName(PICK_Z);
		glRenderBoxOnAStick(gl);
		gl.glPopMatrix();    

		gl.glLoadName(PICK_OTHER);
		gl.glColor4d(0.8, 0.8, 0, 1);
		glRenderBox(gl);

		gl.glPopMatrix();

		gl.glPopAttrib();
		gl.glPopAttrib();
		gl.glPopAttrib();
	}
}