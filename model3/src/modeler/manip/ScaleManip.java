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

		//grabbing the center point
		if (axisMode == PICK_OTHER)
		{
			float scale = mouseDelta.y * 2 + 1;
			Vector3f currentScale = t.getScale();
			currentScale.scale(scale);
			t.setScale(currentScale);
		}
		//grabbing individual axis
		else
		{
			//get axis ray
			Point3f axisOrigin = new Point3f();
			Vector3f axisDirection = new Vector3f();
			computeAxisRay(axisOrigin, axisDirection);

			//get viewing ray for start position
			Point3f viewOriginStart = new Point3f();
			Vector3f viewDirectionStart = new Vector3f();
			Vector2f startMouse = new Vector2f(mousePosition.x - mouseDelta.x, mousePosition.y - mouseDelta.y);
			computeViewingRay(startMouse, viewOriginStart, viewDirectionStart);

			//get viewing ray for end position
			Point3f viewOriginEnd = new Point3f();
			Vector3f viewDirectionEnd = new Vector3f();
			computeViewingRay(mousePosition, viewOriginEnd, viewDirectionEnd);

			//get T values
			float tStart = computePseudointersection(viewOriginStart, viewDirectionStart, axisOrigin, axisDirection);
			float tEnd = computePseudointersection(viewOriginEnd, viewDirectionEnd, axisOrigin, axisDirection);

			//find ratio
			float tDifference = (tEnd / tStart);

			//set scale
			Vector3f currentScale = t.getScale();

			if (axisMode == PICK_X)
			{
				currentScale.x = tDifference;
			}
			if (axisMode == PICK_Y)
			{
				currentScale.y = tDifference;
			}
			if (axisMode == PICK_Z)
			{
				currentScale.z = tDifference;
			}

			t.setScale(currentScale);
		}
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