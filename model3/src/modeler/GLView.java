package modeler;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import jgl.GL;
import jgl.GLU;
import modeler.manip.Manip;
import modeler.manip.RotateManip;
import modeler.scene.Camera;
import modeler.scene.Light;
import modeler.scene.PerspectiveCamera;
import modeler.scene.SceneNode;
import modeler.shape.Shape;

/**
 * This class is a swing widget that will display the OpenGL implementation of
 * what is going on in the modeler.
 * 
 * @author ags
 */
public class GLView extends JPanel implements ComponentListener {

	//GUI element names
	private static final String WIREFRAME_CHECKBOX_TEXT = "Wireframe";
	private static final String LIGHTING_CHECKBOX_TEXT = "Lighting";
	private static final String FLIP_CHECKBOX_TEXT = "Camera Flip";

	static final long serialVersionUID = 1L;

	//GUI parameters
	private final int[] viewport = new int[4];
	protected boolean drawInPickMode = false;
	protected boolean multiPickMode = false;
	protected boolean wireframe = false;
	protected boolean lighting = true;

	//GUI elements
	protected GLJPanel canvas;
	protected Camera camera;
	protected GLAxes axes;
	protected EventHandler handler;
	protected JTree transformationTreeView;
	protected MainFrame mainFrame;
	protected JCheckBox flipBox;
	protected JCheckBox wireBox;
	protected JCheckBox lightBox;

	//Current number of polygons in the scene
	public static int polyCount = 0;

	/**
	 * Create a OpenGL drawing panel
	 * @param newMainFrame
	 * @param newTransformationTreeView
	 * @param newCamera
	 */
	public GLView(MainFrame newMainFrame, JTree newTransformationTreeView, Camera newCamera) {
		// System.out.println("GLView.GLView");
		handler = new EventHandler();
		camera = newCamera;
		axes = new GLAxes();
		mainFrame = newMainFrame;
		transformationTreeView = newTransformationTreeView;

		canvas = new GLJPanel();
		canvas.addComponentListener(this);

		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		add(createTopPanel(), BorderLayout.NORTH);

		canvas.addMouseListener(handler);
		canvas.addMouseMotionListener(handler);
	}

	/**
	 * Initialize the wireframe and lighting boxes in the panel
	 * @return
	 */
	protected JComponent createTopPanel() {
		JPanel toReturn = new JPanel(new BorderLayout());
		toReturn.add(new JLabel(" " + camera), BorderLayout.WEST);

		JPanel temp = new JPanel(new GridLayout(1, 3));

		flipBox = new JCheckBox(FLIP_CHECKBOX_TEXT, false);
		flipBox.addActionListener(handler);
		temp.add(flipBox);
		wireBox = new JCheckBox(WIREFRAME_CHECKBOX_TEXT, wireframe);
		wireBox.addActionListener(handler);
		temp.add(wireBox);
		lightBox = new JCheckBox(LIGHTING_CHECKBOX_TEXT, lighting);
		lightBox.addActionListener(handler);
		temp.add(lightBox);

		toReturn.add(temp, BorderLayout.EAST);
		toReturn.add(new JPanel(), BorderLayout.CENTER);
		return toReturn;
	}

	/**
	 * Getter for camera
	 * @return
	 */
	public Camera getCamera() {
		// System.out.println("GLView.getCamera");

		return camera;

	}

	/**
	 * Setter for camera
	 * @param newCamera
	 */
	public void setCamera(Camera newCamera) {
		// System.out.println("GLView.setCamera");

		camera = newCamera;
	}

	/**
	 * Repaints the canvas
	 *
	 */
	public void refresh() {
		// System.out.println("GLView.refresh");
		canvas.repaint();

	}

	public void processEvent(AWTEvent e) {
		// System.out.println("GLView.processEvent");
		init();
	}

	public void init() {
		// System.out.println("GLView.init");
		GL gl = canvas.getGL();

		gl.glDepthFunc(GL.GL_LESS);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_BLEND);

		gl.glEnable(GL.GL_NORMALIZE);

		float lmodel_ambient[] = {0.1f, 0.1f, 0.1f, 1.0f};
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);
		gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
		gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);

		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glShadeModel(GL.GL_SMOOTH);

		gl.glSelectBuffer(mainFrame.pickBufferSize, mainFrame.pickBuffer);

	}

	/**
	 * Choosing pick or regular via flag is necessary
	 * because all drawable calls must be made from
	 * the AWT event thread (i.e., start at display())
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	public void display() {
		// System.out.println("GLView.display");

		GL gl = canvas.getGL();
		GLU glu = canvas.getGLU();

		if (drawInPickMode) {
			gl.glRenderMode(GL.GL_SELECT);

			gl.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);

			// Never wireframe
			gl.glCullFace(GL.GL_BACK);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);

			// Camera and pick projections
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPickMatrix(handler.lastMousePixel_X, (canvas.getHeight() - 1) - handler.lastMousePixel_Y, 6, 6, viewport);
			camera.doProjection(canvas);

			// Camera lookat
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			camera.doModelview(canvas);

			gl.glInitNames();
			gl.glPushName(1); // push a dummy name onto the stack so we can call
			// glLoadName from now on

			// Render only pickable items
			synchronized (transformationTreeView) {
				TreeModel transformationTree = transformationTreeView.getModel();
				SceneNode root = (SceneNode) (transformationTree.getRoot());
				polyCount = root.render(gl, glu);
				mainFrame.updatePolyCount();
			}

			if (mainFrame.currentManip != null)
				mainFrame.currentManip.renderConstantSize(canvas, camera);

			// Stop pick mode
			drawInPickMode = false;

			// Process hits
			int hits = gl.glRenderMode(GL.GL_RENDER);
			processHits(hits);
		}
		else {
			displaySetup();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());

			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			camera.doProjection(canvas);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			camera.doModelview(canvas);

			synchronized (transformationTreeView) {
				TreeModel transformationTree = transformationTreeView.getModel();
				SceneNode root = (SceneNode) (transformationTree.getRoot());

				drawLights(root, camera, gl, glu);

				polyCount = root.render(gl, glu);
				mainFrame.updatePolyCount();
			}

			drawGrid(gl);

			axes.renderConstantSize(canvas, camera);
			if (mainFrame.currentManip != null)
				mainFrame.currentManip.renderConstantSize(canvas, camera);
		}
		gl.glFlush();
	}

	/**
	 * Draws the lights
	 * @param root
	 * @param camera2
	 * @param gl
	 * @param glu
	 */
	private void drawLights(SceneNode root, Camera camera, GL gl, GLU glu) {
		for (int i=0; i<root.getChildCount(); i++) {
			SceneNode node = (SceneNode)root.getChildAt(i);
			if (node instanceof Light) {
				Light light = (Light)node;
				light.draw(gl, glu, camera);
			}
		}
	}

	/**
	 * Setup all the lights
	 * @param gl
	 */
	protected void lightSetup(GL gl) {
		TreeModel transformationTree = transformationTreeView.getModel();
		SceneNode root = (SceneNode) (transformationTree.getRoot());

		for (int i=0; i<root.getChildCount(); i++) {
			SceneNode node = (SceneNode)root.getChildAt(i);
			if (node instanceof Light) {
				Light light = (Light)node;
				gl.glLightfv(light.id, GL.GL_POSITION, light.position);
				gl.glLightfv(light.id, GL.GL_DIFFUSE, light.diffuse);
				gl.glLightfv(light.id, GL.GL_AMBIENT, light.ambient);
				gl.glLightfv(light.id, GL.GL_SPECULAR, light.specular);
			}
		}
	}

	/**
	 * Set the culling, wireframe, and lighting parameters based on current settings
	 */
	protected void displaySetup() {
		GL gl = canvas.getGL();
		if (wireframe) {
			gl.glCullFace(GL.GL_BACK);
			gl.glDisable(GL.GL_CULL_FACE);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		}
		else {
			gl.glCullFace(GL.GL_BACK);
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		}

		if (lighting) {
			gl.glEnable(GL.GL_LIGHTING);
			Light.turnOnLights(gl);
			lightSetup(gl);
		}
		else {
			gl.glDisable(GL.GL_LIGHTING);
		}
	}

	/**
	 * Handle the picking events
	 * @param numHits
	 */
	public void processHits(int numHits) {

		boolean picked = false;
		// Give hitting manipulators first priority
		for (int i_hit = 0; i_hit < numHits; i_hit++) {
			int idx = i_hit * 4;
			int pickId = mainFrame.pickBuffer[idx + 3];

			if (pickId < 100) {
				handler.mouseMode = handler.MOUSE_MANIP;
				if (mainFrame.currentManip != null && mainFrame.currentManip.axisMode != Manip.PICK_OTHER) // give
					// priority to
					// PICK_OTHER
					mainFrame.currentManip.setPickedInfo(pickId, camera, handler.lastMousePoint);
				picked = true;
			}
		}
		if (picked) {
			if(mainFrame.currentManip instanceof RotateManip) {
				((RotateManip) (mainFrame.currentManip)).toggleCircleDepth(); 
			}
			mainFrame.refresh();
			return;
		}

		// Then objects
		int closestZ = Integer.MAX_VALUE;
		int closestId = 0;
		for (int i_hit = 0; i_hit < numHits; i_hit++) {
			int idx = i_hit * 4;

			int pickId = mainFrame.pickBuffer[idx + 3];
			if (pickId < 1000) {
				break;
			}
			int pickZ = mainFrame.pickBuffer[idx+1];
			picked = true;
			if (pickZ < closestZ) {
				closestZ = pickZ;
				closestId = pickId;
			} else continue;
		}
		if (picked) {
			SceneNode t = Shape.get(closestId);
			if (t.getParent() != null) t=(SceneNode)t.getParent(); 
			if (multiPickMode) transformationTreeView.addSelectionPath(new TreePath(t.getPath()));
			else transformationTreeView.setSelectionPath(new TreePath(t.getPath()));
		} else {
			transformationTreeView.clearSelection();
			if (mainFrame.currentManip != null) mainFrame.currentManip.drawEnabled = false;
		}
		refresh();
	}

	/**
	 * Draws the object position grid
	 */
	protected void drawGrid(GL gl) {
		int bigness = 10;
		gl.glPushAttrib(GL.GL_LIGHTING_BIT);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glColor3f(0.4f, 0.4f, 0.4f);
		gl.glBegin(GL.GL_LINES);
		if (camera instanceof PerspectiveCamera) {
			for (int i = -bigness; i <= bigness; i++) {
				// RMP: fix z-buffer problem by rendering more lines instead of really long ones.
				for (int j = -bigness; j < bigness; j++) {
					int big = 10 * bigness;
					for (int k = 0; k < big; k++) {
						float j0 = j + ((float) k) / big;
						float j1 = j + ((float) k + 1) / big;
						gl.glVertex3f(i, 0, j0);
						gl.glVertex3f(i, 0, j1);
						gl.glVertex3f(j0, 0, i);
						gl.glVertex3f(j1, 0, i);
					}
				}
			}
		}
		gl.glEnd();
		gl.glPopAttrib();
	}

	/**
	 * Handles all mouse events in this window
	 * @author arbree
	 * Oct 21, 2005
	 * GLView.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class EventHandler extends MouseAdapter implements ActionListener {

		Vector3f motion = new Vector3f();
		protected int lastMousePixel_X, lastMousePixel_Y;
		protected Vector2f lastMousePoint = new Vector2f();
		protected Vector2f currentMousePoint = new Vector2f();
		protected Vector2f mouseDelta = new Vector2f();

		//Mode constants
		protected final int MOUSE_PICK = 0;
		protected final int MOUSE_MANIP = 1;
		protected final int MOUSE_ORBIT = 61;
		protected final int MOUSE_ZOOM = 62;
		protected final int MOUSE_TRACK = 63;

		public int mouseMode = MOUSE_PICK;


		/**
		 * Handle double-clicks in view.
		 */
		public void mouseClicked(MouseEvent e) {
			// System.out.println("GLView.EventHandler.mouseClicked");
			if (e.getClickCount() == 2 && e.getSource() == canvas) {
				mainFrame.setView(GLView.this);
				mainFrame.refresh();
			}
		}

		/**
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 * Handle mouse press events
		 */
		public void mousePressed(MouseEvent e) {
			// System.out.println("GLView.EventHandler.mousePressed");
			// store mouse location on start of drag event
			lastMousePixel_X = e.getX();
			lastMousePixel_Y = e.getY();
			lastMousePoint.set(lastMousePixel_X, lastMousePixel_Y);
			windowToViewport(lastMousePoint);
			updateState(e);
		}

		/**
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			// System.out.println("GLView.EventHandler.mouseReleased");
			// not a click event, so at end of drag reset mouseMode to MOUSE_PICK 
			// (in case it was MOUSE_MANIP or something else
			mouseMode = MOUSE_PICK;
			if (mainFrame.currentManip != null) {
				mainFrame.currentManip.released();
			}
		}

		/**
		 * Compute the current drawing state from the current mouse position 
		 * @param e
		 */
		protected void updateState(MouseEvent e) {
			// System.out.println("GLView.EventHandler.updateState");
			// when the mouse is clicked, check to see what other buttons are down that modify its affect.
			if (flagSet(e, MouseEvent.BUTTON1_DOWN_MASK) && !flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) && !flagSet(e, MouseEvent.CTRL_DOWN_MASK) && flagSet(e, MouseEvent.ALT_DOWN_MASK)) {
				// If alt left-click
				mouseMode = MOUSE_ORBIT;
			}
			else if (!flagSet(e, MouseEvent.BUTTON1_DOWN_MASK) && flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) && !flagSet(e, MouseEvent.CTRL_DOWN_MASK) && flagSet(e, MouseEvent.ALT_DOWN_MASK)) {
				// If alt right-click
				mouseMode = MOUSE_ZOOM;
			}
			else if (flagSet(e, MouseEvent.BUTTON1_DOWN_MASK) && !flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) && flagSet(e, MouseEvent.CTRL_DOWN_MASK) && flagSet(e, MouseEvent.ALT_DOWN_MASK)) {
				// If ctrl-alt left-click
				mouseMode = MOUSE_TRACK;
			}
			else if (flagSet(e, MouseEvent.BUTTON2_DOWN_MASK) && !flagSet(e, MouseEvent.BUTTON1_DOWN_MASK) && !flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) && flagSet(e, MouseEvent.ALT_DOWN_MASK)) {
				// If alt middle-click
				mouseMode = MOUSE_TRACK;
			}
			else {
				multiPickMode = flagSet(e, MouseEvent.SHIFT_DOWN_MASK);  
				drawInPickMode = true;
				mainFrame.refresh();
			}
		}

		/**
		 * Compute viewport mouse positions from window position
		 * @param p
		 */
		protected void windowToViewport(Tuple2f p) {
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			p.set((2 * p.x - w) / w, (2 * (h - p.y - 1) - h) / h);
		}

		/**
		 * Return the state of a mouse flag
		 * @param e
		 * @param flag
		 * @return
		 */
		protected boolean flagSet(MouseEvent e, int flag) {
			return (e.getModifiersEx() & flag) == flag;
		}

		/**
		 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
		 */
		public void mouseDragged(MouseEvent e) {
			// System.out.println("GLView.EventHandler.mouseDragged");
			currentMousePoint.set(e.getX(), e.getY());
			windowToViewport(currentMousePoint);

			// get the vector from the mouse point when pressed and current mouse point
			mouseDelta.set(currentMousePoint);
			mouseDelta.sub(lastMousePoint);

			switch (mouseMode) {
			case MOUSE_PICK:
				// don't support lasso-selection...
				break;
			case MOUSE_MANIP:
				mainFrame.currentManip.dragged(currentMousePoint, mouseDelta );
				break;
			case MOUSE_TRACK:
				camera.convertMotion(mouseDelta, motion);
				mainFrame.trackAllCams(motion);
				break;
			case MOUSE_ORBIT:
				if (camera == mainFrame.pViewCam)
					((PerspectiveCamera) camera).orbit(mouseDelta);
				else {
					camera.convertMotion(mouseDelta, motion);
					mainFrame.trackAllCams(motion);
				}
				break;
			case MOUSE_ZOOM:
				camera.zoom(mouseDelta.y);
				break;
			}

			lastMousePoint.set(e.getX(), e.getY());
			windowToViewport(lastMousePoint);
			mainFrame.refresh();
		}

		/**
		 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
		 */
		public void mouseMoved(MouseEvent e) {
			// make sure nothing needs to be here
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			// System.out.println("GLView.EventHandler.actionPerformed");
			String cmd = e.getActionCommand();

			if (cmd == null) {
				return;
			}
			else if (cmd.equals(WIREFRAME_CHECKBOX_TEXT)) {
				wireframe = !wireframe;
			}
			else if (cmd.equals(LIGHTING_CHECKBOX_TEXT)) {
				lighting = !lighting;
			}
			else if (cmd.equals(FLIP_CHECKBOX_TEXT)) {
				camera.flip();
				canvas.forceRepaint();
			}
			mainFrame.refresh();

		}
	}

	private void myReshape(int w, int h) {
		// System.out.println("GLView.myReshape");
		GL myGL = canvas.getGL();
		myGL.glViewport(0, 0, w, h);
		myGL.glMatrixMode(GL.GL_PROJECTION);
		myGL.glLoadIdentity();
		if (w <= h) {
			myGL.glOrtho(-50.0f, 50.0f, -50.0f * (float) h / (float) w, 50.0f * (float) h / (float) w, -1.0f, 1.0f);
		} else {
			myGL.glOrtho(-50.0f * (float) w / (float) h, 50.0f * (float) w / (float) h, -50.0f, 50.0f, -1.0f, 1.0f);
		}
		myGL.glMatrixMode(GL.GL_MODELVIEW);
		myGL.glLoadIdentity();
	}
	public void componentMoved(ComponentEvent e) {
		// System.out.println("GLView.componentMoved");
		init();
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		// System.out.println("GLView.componentResized");
		// get window width and height by myself
		myReshape(getSize().width, getSize().height);
		display();
		repaint();
	}
}
