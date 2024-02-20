package modeler;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import jgl.GL;
import jgl.GLU;

/**
 * GLCanvas is the canvas class of jGL 2.4.
 *
 * @version 0.1, 18 Apr 2001
 * @author Robin Bing-Yu Chen
 */

public class GLJPanel extends JPanel {
	private static final long serialVersionUID = 6622637113292111062L;

	protected GL myGL = new GL();
	protected GLU myGLU = new GLU(myGL);

	protected final int SIZE = 600;


	public GLJPanel() {
		super();
//		System.out.println("GLJPanel.GLJPanel");
		setSize(SIZE, SIZE);
		myGL.glXMakeCurrent(this, 0, 0);
	}

	@Override
	protected void paintComponent(Graphics g) {
//		System.out.println("GLJPanel.paintComponent");
		super.paintComponent(g);
		paint(g);
		myGL.glFlush();
	}

	public void processEvent(AWTEvent e) {
//		System.out.println("GLJPanel.processEvent");
		setSize(SIZE, SIZE);
		super.processEvent(e);
	}

	public void glut_enable_events(long cap, boolean state) {
		System.out.println("GLJPanel.glut_enable_events");
		if (state)
			enableEvents(cap);
		else
			disableEvents(cap);
	}

	public void update(Graphics g) {
//		System.out.println("GLJPanel.update");
		paint(g);
	}

	public void paint(Graphics g) {
//		System.out.println("GLJPanel.paint");
		myGL.glXSwapBuffers(g, this);
		kPaint++;
	}

	int kPaint = 0;

	void postRenderString(Graphics g, String message, int x, int y){
//		System.out.println("GLJPanel.postRenderString");
		g.drawString(message, x, y);		
	}

	// ************ RETRIEVE RENDERING CONTEXT ************ //

	public GL getGL() {
		return myGL;
	}

	public GLU getGLU() {
		return myGLU;
	}

	// ************ MANUAL REPAINT ************ //

	/**
	 * Can be used to update image if camera has changed position.
	 * 
	 * Warning: if this is invoked by a thread external to AWT, maybe this
	 * will require to redraw GL while GL is already used by AWT.
	 */
	public void forceRepaint() {
		System.out.println("GLJPanel.forceRepaint");
		// This makes GLUT invoke the myReshape function
		processEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));

		// This triggers copy of newly generated picture to the GLCanvas
		repaint();
	}

}
