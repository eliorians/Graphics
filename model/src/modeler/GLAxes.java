/*
 * Author andru
 */

package modeler;

import jgl.GL;
import modeler.manip.Manip;
import modeler.scene.Camera;

/**
 * Class that models the coordinate axes
 * @author arbree
 * Oct 21, 2005
 * GLAxes.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class GLAxes {

	// This is a nice example of how to use display lists to speed up drawing
	// objects once you've computed static geometry for an object.
	private int cache = -1;
	private double constantSize = 0.3;

	/**
	 * Render the axes
	 * @param d
	 */
	public void glRender(GLJPanel canvas) {
		GL gl = canvas.getGL();

		gl.glPushAttrib(GL.GL_LIGHTING_BIT);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glPushAttrib(GL.GL_ENABLE_BIT);
		gl.glDisable(GL.GL_CULL_FACE);
		
		if (cache == -1) {
			cache = gl.glGenLists(1);
			gl.glNewList(cache, GL.GL_COMPILE_AND_EXECUTE);
			gl.glPushAttrib(GL.GL_CURRENT_BIT);
			gl.glColor4f(.8f, .2f, .2f, 1f);
			gl.glPushAttrib(GL.GL_POLYGON_BIT);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			Manip.glRenderArrow(Manip.X_AXIS, gl);

			gl.glColor4f(.2f, .8f, .2f, 1f);
			Manip.glRenderArrow(Manip.Y_AXIS, gl);

			gl.glColor4f(.2f, .2f, .8f, 1f);
			Manip.glRenderArrow(Manip.Z_AXIS, gl);

			gl.glPopAttrib();
			gl.glPopAttrib();
			gl.glEndList();
		}
		else {
			gl.glCallList(cache);
		}
		gl.glPopAttrib();
		gl.glPopAttrib();
	}

	/**
	 * Render the axes at a constant size in the given camera
	 * @param d
	 * @param camera
	 */
	public void renderConstantSize(GLJPanel canvas, Camera camera) {
		GL gl = canvas.getGL();
		double scale = camera.getHeight() * constantSize;
		gl.glPushMatrix();
		gl.glScaled(scale, scale, scale);
		glRender(canvas);
		gl.glPopMatrix();
	}

}
