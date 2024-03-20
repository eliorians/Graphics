package modeler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.vecmath.Vector3f;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import modeler.manip.Manip;
import modeler.manip.RotateManip;
import modeler.manip.ScaleManip;
import modeler.manip.TranslateManip;
import modeler.scene.Light;
import modeler.scene.PerspectiveCamera;
import modeler.scene.SceneNode;
import modeler.scene.Transformation;
import modeler.shape.Cube;
import modeler.shape.Cylinder;
import modeler.shape.Shape;
import modeler.shape.Sphere;
import modeler.shape.Torus;
import modeler.ui.ItemAttributePanel;
import modeler.ui.LightAttributePanel;
import modeler.ui.NodeIconCache;
import modeler.ui.ShapeAttributePanel;
import modeler.ui.TransformationAttributePanel;

/**
 * @author ags
 */
public class MainFrame extends JFrame implements ActionListener, TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	// The initial size of the application
	protected static final int DEFAULT_WIDTH = 800;
	protected static final int DEFAULT_HEIGHT = 700;

	//Size of the screen
	protected static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	//Menu commands
	private static final String SAVE_AS_MENU_TEXT = "Save As";
	private static final String OPEN_MENU_TEXT = "Open";
	private static final String EXIT_MENU_TEXT = "Exit";
	private static final String CLEAR_SELECTED_TEXT = "Clear selection";
	private static final String GROUP_MENU_TEXT = "Group selected";
	private static final String REPARENT_MENU_TEXT = "Reparent selected";
	private static final String DELETE_MENU_TEXT = "Delete selected";
	private static final String PICK_MENU_TEXT = "Select";
	private static final String ROTATE_MENU_TEXT = "Rotate selected";
	private static final String TRANSLATE_MENU_TEXT = "Translate selected";
	private static final String SCALE_MENU_TEXT = "Scale selected";
	private static final String ADD_LIGHT_MENU_TEXT = "Add Light";
	private static final String ADD_SPHERE_MENU_TEXT = "Add Sphere";
	private static final String ADD_CUBE_MENU_TEXT = "Add Cube";
	private static final String ADD_CYLINDER_MENU_TEXT = "Add Cylinder";
	private static final String ADD_TORUS_MENU_TEXT = "Add Torus";

	//GUI elements
	protected JFileChooser fileChooser;
	protected DefaultTreeModel transformationTree;
	protected JTree transformationTreeView;
	protected JComponent leftWindow;
	protected JPanel rightPanel;
	protected JPanel currentAttributesPanel;
	protected JPanel attributeHolder;
	protected JPanel allGLViews;
	protected JPanel currentView;
	protected GLView pView;
	private boolean isReparenting = false;
	private SceneNode[] nodesToReparent;
	protected SliderPanel sliderPanel;
	private NodeIconCache nodeIcons = new NodeIconCache();
	private boolean interactive = true;

	//Cameras
	public PerspectiveCamera pViewCam;

	//Manipulator data
	public Manip currentManip;
	protected final int pickBufferSize = 1024;
	protected int[] pickBuffer = new int[pickBufferSize];

	public MainFrame() {
		this(true);
	}
	
	/**
	 * Creates the initial application
	 */
	public MainFrame(boolean interactive) {

		//Define the basic settings
		super("CS 5465 Modeler Solution");
		
		this.interactive = interactive;

		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		if (interactive) {
			fileChooser = new JFileChooser();
		}

		//Build the GUI
		setResizable(false);

		cp.add(createGUI(), BorderLayout.CENTER);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocation((int) (SCREEN_SIZE.getWidth() / 2 - getWidth() / 2), (int) (SCREEN_SIZE.getHeight() / 2 - getHeight() / 2));
		transformationTreeView.expandPath(new TreePath(transformationTree.getRoot()));


	}

	/**
	 * Creates the GUI
	 * @return
	 */
	protected JComponent createGUI() {

		JSplitPane toReturn = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		leftWindow = createLeftComponent();
		toReturn.setLeftComponent(leftWindow);
		rightPanel = new JPanel(new BorderLayout());

		sliderPanel = new SliderPanel();
		rightPanel.add(sliderPanel, BorderLayout.EAST);
		rightPanel.add(createRightComponent(), BorderLayout.CENTER);
		toReturn.setRightComponent(rightPanel);

		return toReturn;
	}

	/**
	 * Maps all GUI actions to listeners in this object and builds the menu
	 */
	protected void createActionsAndBuildMenu() {

		//Create all the actions
		BasicAction group = new BasicAction(GROUP_MENU_TEXT, this);
		BasicAction reparent = new BasicAction(REPARENT_MENU_TEXT, this);
		BasicAction delete = new BasicAction(DELETE_MENU_TEXT, this);
		BasicAction clear = new BasicAction(CLEAR_SELECTED_TEXT, this);

		BasicAction addLight = new BasicAction(ADD_LIGHT_MENU_TEXT, this);
		BasicAction addSphere = new BasicAction(ADD_SPHERE_MENU_TEXT, this);
		BasicAction addCube = new BasicAction(ADD_CUBE_MENU_TEXT, this);
		BasicAction addCylinder = new BasicAction(ADD_CYLINDER_MENU_TEXT, this);
		BasicAction addTorus = new BasicAction(ADD_TORUS_MENU_TEXT, this);

		BasicAction saveAs = new BasicAction(SAVE_AS_MENU_TEXT, this);
		BasicAction open = new BasicAction(OPEN_MENU_TEXT, this);
		BasicAction exit = new BasicAction(EXIT_MENU_TEXT, this);

		BasicAction pickTool = new BasicAction(PICK_MENU_TEXT, this);
		BasicAction rotateTool = new BasicAction(ROTATE_MENU_TEXT, this);
		BasicAction translateTool = new BasicAction(TRANSLATE_MENU_TEXT, this);
		BasicAction scaleTool = new BasicAction(SCALE_MENU_TEXT, this);

		//Set shortcut keys
		group.setAcceleratorKey(KeyEvent.VK_G, KeyEvent.CTRL_MASK);
		reparent.setAcceleratorKey(KeyEvent.VK_R, KeyEvent.CTRL_MASK);
		delete.setAcceleratorKey(KeyEvent.VK_DELETE, 0);

		pickTool.setAcceleratorKey(KeyEvent.VK_Q, 0);
		translateTool.setAcceleratorKey(KeyEvent.VK_W, 0);
		rotateTool.setAcceleratorKey(KeyEvent.VK_E, 0);
		scaleTool.setAcceleratorKey(KeyEvent.VK_R, 0);

		saveAs.setAcceleratorKey(KeyEvent.VK_A, KeyEvent.CTRL_MASK);
		open.setAcceleratorKey(KeyEvent.VK_O, KeyEvent.CTRL_MASK);
		exit.setAcceleratorKey(KeyEvent.VK_Q, KeyEvent.CTRL_MASK);

		//Create the menu
		JMenuBar bar = new JMenuBar();
		JMenu menu;

		menu = new JMenu("File");
		menu.setMnemonic('F');
		menu.add(new JMenuItem(open));
		menu.add(new JMenuItem(saveAs));
		menu.addSeparator();
		menu.add(new JMenuItem(exit));
		bar.add(menu);

		menu = new JMenu("Edit");
		menu.setMnemonic('E');
		menu.add(new JMenuItem(group));
		menu.add(new JMenuItem(reparent));
		menu.add(new JMenuItem(delete));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(pickTool));
		menu.add(new JMenuItem(translateTool));
		menu.add(new JMenuItem(rotateTool));
		menu.add(new JMenuItem(scaleTool));
		bar.add(menu);

		menu = new JMenu("Scene");
		menu.setMnemonic('S');
		menu.add(new JMenuItem(addLight));
		menu.add(new JMenuItem(addSphere));
		menu.add(new JMenuItem(addCube));
		menu.add(new JMenuItem(addCylinder));
		menu.add(new JMenuItem(addTorus));
		bar.add(menu);

		setJMenuBar(bar);

		JPopupMenu p = new JPopupMenu();
		p.add(new JMenuItem(group));
		p.add(new JMenuItem(reparent));
		p.add(new JMenuItem(delete));
		p.add(new JMenuItem(clear));
		p.addSeparator();
		p.add(new JMenuItem(addLight));
		p.add(new JMenuItem(addSphere));
		p.add(new JMenuItem(addCube));
		p.add(new JMenuItem(addCylinder));
		p.add(new JMenuItem(addTorus));

		transformationTreeView.addMouseListener(new PopupListener(p));
		transformationTreeView.addTreeSelectionListener(this);
	}

	/**
	 * Create the tree and properties veiws
	 * @return
	 */
	protected JComponent createLeftComponent() {

		JSplitPane toReturn = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		JPanel temp;

		// Create the tree models
		transformationTree = new DefaultTreeModel(new Transformation("Root"));

		// Create the tree views
		transformationTreeView = new JTree(transformationTree);
		DefaultTreeCellRenderer renderer = new TreeRenderer();
		transformationTreeView.setCellRenderer(renderer);
		transformationTreeView.setEditable(true);
		transformationTreeView.setCellEditor(new DefaultTreeCellEditor(transformationTreeView, renderer));
		transformationTreeView.setShowsRootHandles(true);
		transformationTreeView.setRootVisible(true);

		KeyListener[] kls = transformationTreeView.getKeyListeners();
		for (int i=0; i<kls.length; i++) transformationTreeView.removeKeyListener(kls[i]);

		// Associate the action handlers (and make the JMenuBar too)
		createActionsAndBuildMenu();

		// Add the components to another split pane
		temp = new JPanel(new BorderLayout());
		temp.add(new JLabel(" Scene"), BorderLayout.NORTH);
		temp.add(new JScrollPane(transformationTreeView));
		toReturn.setLeftComponent(temp);
		attributeHolder = new JPanel(new BorderLayout());
		currentAttributesPanel = ItemAttributePanel.EMPTY_PANEL;
		attributeHolder.add(currentAttributesPanel, BorderLayout.NORTH);
		toReturn.setRightComponent(attributeHolder);

		// Set some nice options on the split pane
		toReturn.setOneTouchExpandable(true);
		toReturn.setResizeWeight(0.95);
		toReturn.resetToPreferredSizes();

		// Return
		return toReturn;

	}

	/**
	 * Create the viewing windows
	 * @return
	 */
	protected JComponent createRightComponent() {

		currentView = new JPanel(new GridLayout(1, 1));
		allGLViews = new JPanel(new GridLayout(1, 1, 10, 10));
		SceneNode root = (SceneNode) transformationTree.getRoot();

		// CAMERA VIEW
		pViewCam = new PerspectiveCamera(2f, 1000f, 45.0f, "Perspective");
		pView = new GLView(this, transformationTreeView, pViewCam);
		pViewCam.setName("Camera");
		transformationTree.insertNodeInto(pView.getCamera(), root, root.getChildCount());
		allGLViews.add(pView);

		currentView.add(allGLViews);

		// LIGHT
		Light.resetLights();
		Light light = Light.makeNewLight(true);
		// add the panel
		light.setUserObject(new LightAttributePanel(this, light));
		transformationTree.insertNodeInto(light, root, root.getChildCount());

		return currentView;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == null) {
			return;
		}
		else if (cmd.equals(GROUP_MENU_TEXT)) {
			groupSelected(getSelectedReparentables(), "Group");
		}
		else if (cmd.equals(CLEAR_SELECTED_TEXT)) {
			transformationTreeView.clearSelection();
		}
		else if (cmd.equals(REPARENT_MENU_TEXT)) {
			nodesToReparent = getSelectedReparentables();
			isReparenting = true;
		}
		else if (cmd.equals(DELETE_MENU_TEXT)) {
			deleteSelected(getDeleteableSelections());
			currentManip = null;
			refresh();
		}
		else if (cmd.equals(PICK_MENU_TEXT)) {
			currentManip = null;
			refresh();
		}
		else if (cmd.equals(TRANSLATE_MENU_TEXT)) {
			SceneNode ts[] = getLeafmostSelectedTransformations();
			if (ts.length > 0) {
				currentManip = new TranslateManip((Transformation)ts[0]);
				refresh();
			}
		}
		else if (cmd.equals(ROTATE_MENU_TEXT)) {
			SceneNode ts[] = getLeafmostSelectedTransformations();
			if (ts.length > 0) {
				currentManip = new RotateManip((Transformation)ts[0]);
				refresh();
			}
		}
		else if (cmd.equals(SCALE_MENU_TEXT)) {
			SceneNode ts[] = getLeafmostSelectedTransformations();
			if (ts.length > 0) {
				currentManip = new ScaleManip((Transformation)ts[0]);
				refresh();
			}
		}
		else if (cmd.equals(ADD_LIGHT_MENU_TEXT)) {
			addNewLight();
		}
		else if (cmd.equals(ADD_SPHERE_MENU_TEXT)) {
			addNewShape(Sphere.class);
		}
		else if (cmd.equals(ADD_CUBE_MENU_TEXT)) {
			addNewShape(Cube.class);
		}
		else if (cmd.equals(ADD_CYLINDER_MENU_TEXT)) {
			addNewShape(Cylinder.class);
		}
		else if (cmd.equals(ADD_TORUS_MENU_TEXT)) {
			addNewShape(Torus.class);
		}
		else if (cmd.equals(OPEN_MENU_TEXT)) {
			openTree();
		}
		else if (cmd.equals(SAVE_AS_MENU_TEXT)) {
			saveTreeAs();
		}
		else if (cmd.equals(EXIT_MENU_TEXT)) {
			System.exit(0);
		}
	}

	/**
	 * Returns the tree model
	 * @return
	 */
	public DefaultTreeModel getTree() {

		return transformationTree;

	}

	/**
	 * Fix all the icon bindings
	 * @param inRoot
	 */
	protected void fixIcons(SceneNode inRoot) {

		for (int i = 0; i < inRoot.getChildCount(); i++)
			fixIcons((SceneNode) inRoot.getChildAt(i));
	}

	protected void saveTreeAs() {
		//Pick a file
		int choice = fileChooser.showSaveDialog(this);
		if (choice != JFileChooser.APPROVE_OPTION) {
			refresh();
			return;
		}
		String filename = fileChooser.getSelectedFile().getAbsolutePath();
		saveTree(filename);

	}
	/**
	 * exportTree the tree in a format readable by the ray tracer
	 *
	 */
	protected void saveTree(String filename) {

		//Write the tree out
		try {
			PrintWriter pw = new PrintWriter(new BufferedOutputStream(new FileOutputStream(filename)));
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			pw.println("<scene tolerance=\"" + Shape.TOLERANCE + "\" wireframe=\"" + pView.wireframe + "\" lighting=\"" + pView.lighting + "\">");

			synchronized (transformationTree) {
				SceneNode.exportXML(pw, (SceneNode) transformationTree.getRoot());
			}
			pw.println("</scene>");
			pw.close();
		}
		catch (IOException ioe) {
			if (interactive) {
				showExceptionDialog(ioe);
			} else {
				ioe.printStackTrace();
			}
		}

		refresh();
	}

	protected void openTree() {
		//Pick a file
		int choice = fileChooser.showOpenDialog(this);
		if (choice != JFileChooser.APPROVE_OPTION) {
			refresh();
			return;
		}
		String filename = fileChooser.getSelectedFile().getAbsolutePath();
		openTree(filename);
	}

	public void openTree(String filename) {
		//Load the tree
		try {
			SceneNode newRoot = null;
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(filename);
			Element e = doc.getDocumentElement();

			// make sure outer node is "scene"
			assert e.getNodeName().equals("scene");
			float tolerance = Shape.TOLERANCE;
			boolean lighting = this.pView.lighting;
			boolean wireframe = this.pView.wireframe;

			String s = e.getAttribute("tolerance");
			if (s.length() > 0)
				tolerance = Float.parseFloat(s);
			s = e.getAttribute("lighting");
			if (s.length() > 0)
				lighting = Boolean.parseBoolean(s);
			s = e.getAttribute("wireframe");
			if (s.length() > 0)
				wireframe = Boolean.parseBoolean(s);

			Light.resetLights();

			// search for root node
			NodeList children = e.getChildNodes();
			int numChildren = children.getLength();
			for(int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element root = (Element) child;
					// make sure it's the root node.
					if (root.getAttribute("name").equals("Root") && root.getNodeName().endsWith("Transformation")) {
						newRoot = SceneNode.readXML(root);
						break;
					}
				}
			}


			if (newRoot != null) {
				Enumeration<?> en = newRoot.breadthFirstEnumeration();
				while (en.hasMoreElements()) {
					SceneNode node = (SceneNode) en.nextElement();
					System.out.println(node.name);
					if (node.name.equals("Camera")) {
						pViewCam = (PerspectiveCamera) node;
						pView.setCamera(pViewCam);
					}
				}

				// setup attribute panels
				newRoot.setupAttributePanels(this);
				synchronized (transformationTree) {
					transformationTree.setRoot(newRoot);
				}
				//Update the window

				refresh();

				Shape.TOLERANCE = tolerance;
				this.sliderPanel.setValue(Shape.TOLERANCE);
				this.pView.lighting = lighting;
				this.pView.wireframe = wireframe;
				this.pView.lightBox.setSelected(lighting);
				this.pView.wireBox.setSelected(wireframe);
				refresh();
			}
		}
		catch (Exception e) {
			if (interactive) {
				showExceptionDialog(e);
			} else {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * Filters out extraneous child nodes from the 
	 * supplied array of Treeables.  
	 */
	protected Vector<SceneNode> filterChildren(SceneNode[] selected) {
		// Trim extraneous child nodes
		Vector<SceneNode> filtered = new Vector<SceneNode>();
		for (int i = 0; i < selected.length; i++)
			filtered.add(selected[i]);

		//Find the redundant children
		for (Iterator<SceneNode> j = filtered.iterator(); j.hasNext();) {
			SceneNode tj = j.next();
			for (int i = 0; i < selected.length; i++) {
				SceneNode ti = selected[i];
				if (ti == tj) continue;
				if (tj.isNodeAncestor(ti)) j.remove();
			}
		}
		return filtered;
	}

	/**
	 * Reparents the items in nodesToReparent underneath
	 * the currently selected node.
	 */
	protected void reparentUnderSelected() {
		SceneNode[] ts = getSelectedReparentables();

		// Invalid number of new parents selected?
		if (ts.length != 1) return;
		SceneNode parent = ts[0];

		// Invalid parent type selected?
		if (parent instanceof Shape ) return;

		// Invalid children selected?
		for (int i=0; i<nodesToReparent.length; i++) {
			if (parent.isNodeAncestor(nodesToReparent[i])) return;
		}

		Vector<SceneNode> filtered = filterChildren(nodesToReparent);

		//reparent the filtered children each seperately
		for (int i=0; i<filtered.size(); i++) {
			SceneNode t = filtered.get(i);
			t.removeFromParent();
			parent.insert(t,0);
		}
	}

	/**
	 * Groups a set of selected nodes into a new parent
	 * @param selected
	 * @param name
	 */
	protected void groupSelected(SceneNode[] selected, String name) {

		Vector<SceneNode> filtered = filterChildren(selected);

		if (filtered.size() == 0) return;

		//Form the new group and add it to the tree
		SceneNode groupNode = new Transformation(name);
		groupNode.setupAttributePanels(this);
		SceneNode firstSelected = filtered.get(0);
		SceneNode groupParent = (SceneNode) firstSelected.getParent();
		if (groupParent == null) return;
		int groupIdx = groupParent.getIndex(firstSelected);
		synchronized (transformationTree) {
			transformationTree.insertNodeInto(groupNode, groupParent, groupIdx);
			for (int i = 0; i < filtered.size(); i++) {
				SceneNode node = filtered.get(i);
				transformationTree.removeNodeFromParent(node);
				transformationTree.insertNodeInto(node, groupNode, groupNode.getChildCount());
			}
			transformationTree.reload();
		}
		transformationTreeView.expandPath(new TreePath(groupNode.getPath()));
	}

	/**
	 * Deletes all the selected nodes.  Checks to make sure deletes are 
	 * valid first
	 * @param selected
	 */
	protected void deleteSelected(SceneNode[] selected) {
		Vector<SceneNode> filtered = filterChildren(selected);
		for (int i=0; i<filtered.size(); i++) {
			SceneNode t = filtered.get(i);
			if(t == transformationTree.getRoot())
				continue;
			if(t instanceof Light)
				Light.returnLight((Light) t);
			transformationTree.removeNodeFromParent(t);
		}
	}

	/**
	 * Add a new light to the tree
	 */
	protected void addNewLight() {

		//Create a light
		Light light = Light.makeNewLight(true);
		// create attr panel
		light.setUserObject(new LightAttributePanel(this, light));

		SceneNode root = (SceneNode) transformationTree.getRoot();

		//Add it to the tree
		synchronized (transformationTree) {
			transformationTree.insertNodeInto(light, root, root.getChildCount());
		}

		refresh();
	}

	/**
	 * Add a new shape to the tree
	 * @param c
	 */
	public Transformation addNewShape(Class<? extends Shape> c) {

		TreePath path = transformationTreeView.getSelectionPath();

		//Get the node to insert into
		SceneNode selected = (SceneNode) transformationTree.getRoot();
		if (path != null && path.getLastPathComponent() instanceof Transformation)
			selected = (SceneNode) path.getLastPathComponent();

		Transformation newTransform = null;
		
		//Add the node
		try {
			Shape newObject = c.newInstance();
			// create attr panel
			newObject.setUserObject(new ShapeAttributePanel(this, newObject));

			newTransform = new Transformation(newObject.toString());
			// create attr panel
			newTransform.setUserObject(new TransformationAttributePanel(this, newTransform));

			newTransform.insert(newObject, 0);
			synchronized (transformationTree) {
				transformationTree.insertNodeInto(newTransform, selected, selected.getChildCount());
			}
		}
		catch (Exception e) {
			if (interactive) {
				showExceptionDialog(e);
			} else {
				e.printStackTrace();
			}
		}

		refresh();
		return newTransform;
	}

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {

		if (isReparenting) {
			reparentUnderSelected();
			isReparenting = false;
			transformationTree.reload();
		} else {
			SceneNode[] transformations = getLeafmostSelectedTransformations();
			if (transformations.length != 0) {
				if (currentManip != null) {
					currentManip.drawEnabled = true;
					currentManip.setTransformation((Transformation)transformations[0]);
				}
			}
			attributeHolder.remove(currentAttributesPanel);
			if (getSelection().length == 1) {
				SceneNode node = getSelection()[0];

				// The attr panel is stored as the node's user object
				currentAttributesPanel = (ItemAttributePanel)node.getUserObject();
				if(currentAttributesPanel == null)
					currentAttributesPanel = ItemAttributePanel.EMPTY_PANEL;
			}
			else {
				currentAttributesPanel = ItemAttributePanel.EMPTY_PANEL;
			}
			attributeHolder.add(currentAttributesPanel, BorderLayout.NORTH);

			currentAttributesPanel.invalidate();
			((ItemAttributePanel)currentAttributesPanel).refresh();
			leftWindow.revalidate();
			leftWindow.repaint();
		}
		refresh();

	}

	/**
	 * Returns an array containing the current selection
	 * @return
	 */
	public SceneNode[] getSelection() {

		TreePath[] paths = transformationTreeView.getSelectionPaths();
		if (paths == null) {
			return new SceneNode[] {};
		}
		SceneNode[] ts = new SceneNode[paths.length];
		for (int i = 0; i < paths.length; i++) {
			ts[i] = (SceneNode) paths[i].getLastPathComponent();
		}
		return ts;
	}

	/**
	 * Returns an array containing the current selection, limited to
	 * Transformations and Shapes (reparentable objects)
	 */
	public SceneNode[] getSelectedReparentables() {

		int ts_size = 0;
		TreePath[] paths = transformationTreeView.getSelectionPaths();
		if (paths == null) {
			return new SceneNode[] {};
		}

		for (int i = 0; i < paths.length; i++) {
			SceneNode t = (SceneNode) paths[i].getLastPathComponent();
			if (t instanceof Shape || t instanceof Transformation)
				ts_size++;
		}

		SceneNode ts[] = new SceneNode[ts_size];
		int i_ts = 0;
		for (int i = 0; i < paths.length; i++) {
			SceneNode t = (SceneNode) paths[i].getLastPathComponent();
			if (t instanceof Shape || t instanceof Transformation)
				ts[i_ts++] = t;
		}

		return ts;
	}

	/**
	 * Gets all reparentable selections and then adds all the lights
	 * @return
	 */
	public SceneNode[] getDeleteableSelections() {

		SceneNode[] output = getSelectedReparentables();
		int ts_size = 0;
		TreePath[] paths = transformationTreeView.getSelectionPaths();
		if (paths == null) {
			return output;
		}

		for (int i = 0; i < paths.length; i++) {
			SceneNode t = (SceneNode) paths[i].getLastPathComponent();
			if (t instanceof Light)
				ts_size++;
		}

		SceneNode ts[] = new SceneNode[output.length + ts_size];
		System.arraycopy(output, 0, ts, 0, output.length);
		int i_ts = output.length;
		for (int i = 0; i < paths.length; i++) {
			SceneNode t = (SceneNode) paths[i].getLastPathComponent();
			if (t instanceof Light)
				ts[i_ts++] = t;
		}

		return ts;


	}

	/**
	 * Returns an array containing the current selection, limited to
	 * Transformations and Shapes, filtering out extraneous parent Transformations
	 * and replacing Shapes with their immediate parent transformation. The result
	 * is a Transformation array where no Transformation appears above any of the
	 * others.
	 */
	public SceneNode[] getLeafmostSelectedTransformations() {

		Vector<SceneNode> transformations = new Vector<SceneNode>();

		SceneNode[] selection = getSelectedReparentables();
		for (int i = 0; i < selection.length; i++) {
			if (selection[i] instanceof Shape) {
				if (selection[i].getParent() != null && !transformations.contains(selection[i].getParent()))
					transformations.add((SceneNode)selection[i].getParent());
			}
			else if (selection[i] instanceof Transformation && !transformations.contains(selection[i])) {
				transformations.add(selection[i]);
			}
		}

		Vector<SceneNode> filteredTransformations = new Vector<SceneNode>(transformations);
		for (Iterator<SceneNode> i = transformations.iterator(); i.hasNext();) {
			SceneNode ti = i.next();
			SceneNode testParent = ((SceneNode)ti.getParent());
			while (testParent != null) {
				for (Iterator<SceneNode> j = filteredTransformations.iterator(); j.hasNext();) {
					SceneNode tj = j.next();
					if (ti == tj)
						continue;
					if (tj == testParent) {
						j.remove();
					}
				}
				testParent = (SceneNode) testParent.getParent();
			}
		}

		SceneNode[] ts = new SceneNode[filteredTransformations.size()];
		for (int i = 0; i < filteredTransformations.size(); i++)
			ts[i] = filteredTransformations.get(i);
		return ts;
	}

	/**
	 * Translates all cameras by a given vector
	 * @param motion
	 */
	public void trackAllCams(Vector3f motion) {
		pViewCam.translate(motion);
	}

	/**
	 * Causes all views to be repainted
	 *
	 */
	public void refresh() {
		pView.display();
		pView.refresh();
		sliderPanel.update();
	}

	/**
	 * Updates the polygon count
	 *
	 */
	public void updatePolyCount() {
		sliderPanel.update();
	}

	/**
	 * Set one view to cover the whole screen and temporarily repress the remaining views
	 * @param toFocus
	 */
	public void setView(GLView toFocus) {

		if (currentView.getComponent(0) == allGLViews) {
			currentView.remove(0);
			currentView.add(toFocus);
		}
		else {
			currentView.remove(0);
			allGLViews.removeAll();
			allGLViews.add(pView);
			currentView.add(allGLViews);
		}
		validate();
	}

	/**
	 * Returns true if all the supplied objects are the same class
	 * @param a
	 * @return
	 */
	public boolean allSameType(Object[] a) {

		if (a.length == 0)
			return false;
		Class<?> type = a[0].getClass();
		for (int i = 1; i < a.length; i++) {
			if (a[i].getClass() != type)
				return false;
		}
		return true;
	}

	/**
	 * Displays an exception in a window
	 * @param e
	 */
	protected void showExceptionDialog(Exception e) {

		String str = "The following exception was thrown: " + e.toString() + ".\n\n" + "Would you like to see the stack trace?";
		int choice = JOptionPane.showConfirmDialog(this, str, "Exception Thrown", JOptionPane.YES_NO_OPTION);

		if (choice == JOptionPane.YES_OPTION) {
			e.printStackTrace();
		}
	}

	/**
	 * Private class that handles the popup menu events in the display tree
	 * @author arbree
	 * Oct 21, 2005
	 * MainFrame.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class PopupListener extends MouseAdapter {

		protected JPopupMenu menu;

		public PopupListener(JPopupMenu newMenu) {

			menu = newMenu;
		}

		public void mousePressed(MouseEvent e) {

			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {

			maybeShowPopup(e);
		}

		protected void maybeShowPopup(MouseEvent e) {

			if (e.isPopupTrigger()) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/**
	 * Private class that handles the GUI events
	 * @author arbree
	 * Oct 21, 2005
	 * MainFrame.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class BasicAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		protected ActionListener listener;

		public BasicAction(String newName, ActionListener newListener) {

			super(newName);
			listener = newListener;
		}

		public void setAcceleratorKey(int key, int masks) {

			putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(key, masks));
		}

		public void actionPerformed(ActionEvent e) {

			listener.actionPerformed(e);
		}

	}

	/**
	 * Class that describes how the display tree is rendered
	 * @author arbree
	 * Oct 21, 2005
	 * MainFrame.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class TreeRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		public TreeRenderer() {

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if ((value instanceof SceneNode) && interactive)
				setIcon( nodeIcons.getIconFor((SceneNode)value) );
			return this;
		}
	}

	/**
	 * Handles the global shape tolerance slider
	 * @author arbree
	 * Oct 27, 2005
	 * MainFrame.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private class SliderPanel extends JPanel implements ChangeListener {

		private static final long serialVersionUID = 3977294404597855026L;

		/** The GUI components */
		private JSlider slider;
		private JTextField valueFld;
		private JTextField polyFld;
		private DecimalFormat df1 = new DecimalFormat("0.000");
		private DecimalFormat df2 = new DecimalFormat("0.0E0#");

		// Configuration of the slider
		int numTicks = 1000;
		float initialValue = 0.5f;
		float minValue = -3;
		float maxValue = -0.7f;
		boolean log = true;

		public SliderPanel() {

			super();
			this.setLayout(new BorderLayout());
			slider = new JSlider(JSlider.VERTICAL);
			slider.setMinorTickSpacing(10);
			slider.setMaximum(numTicks);
			slider.setMinimum(0);
			slider.setPaintTicks(true);
			slider.setValue((int) (numTicks * initialValue));
			slider.addChangeListener(this);
			JPanel southPanel = new JPanel(new GridLayout(2,1));
			valueFld = new JTextField();
			valueFld.setEditable(false);
			valueFld.setText(df1.format(Shape.TOLERANCE));
			valueFld.setHorizontalAlignment(JTextField.RIGHT);
			slider.addChangeListener(this);
			polyFld = new JTextField();
			polyFld.setColumns(4);
			polyFld.setEditable(false);
			polyFld.setText("0");
			polyFld.setHorizontalAlignment(JTextField.RIGHT);
			this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			this.add(slider, BorderLayout.CENTER);
			southPanel.add(valueFld);
			southPanel.add(polyFld);
			this.add(southPanel, BorderLayout.SOUTH);
			stateChanged(null);

		}

		private void setValue(float value) {
			if (log) {
				value = (float) Math.log10(value);
			}
			value = (value - minValue) / (maxValue - minValue);
			value *= numTicks;
			slider.setValue((int) value);
		}

		/**
		 * Sets the values of the given slider
		 * @param source
		 */
		private float getValue(JSlider source) {

			float value;
			value = source.getValue() / (float) numTicks;
			value = minValue + value * (maxValue - minValue);
			if (log)
				value = (float) Math.pow(10, value);
			return value;

		}

		/**
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {

			Shape.TOLERANCE = getValue(slider);
			valueFld.setText(df1.format(Shape.TOLERANCE));
			if(pView != null && transformationTree != null) {
				((SceneNode) (transformationTree.getRoot())).rebuild();
				refresh();
			}
		}

		/**
		 * Updates the polygon count
		 */
		public void update() {

			String text = null;
			if(GLView.polyCount < 100000)
				text = Integer.toString(GLView.polyCount);
			else text = df2.format(GLView.polyCount);
			polyFld.setText(text);
			repaint();

		}
	}

	public BufferedImage writeImage(String pngFile) {
		GLJPanel pCanvas = pView.canvas;
		BufferedImage image = new BufferedImage(pCanvas.getWidth(), pCanvas.getHeight(), BufferedImage.TYPE_INT_RGB);         
		Graphics2D g = image.createGraphics();

		pCanvas.print(g);
		image.flush();

		try {
			ImageIO.write(image, "png", new File(pngFile));
		} catch (IOException ex) {
			System.out.println("Failed to write " + pngFile);
		}

		return image;

	}
	
	public static void recursiveXML(File inputFile) {
		if (inputFile.isFile() && inputFile.getName().endsWith(".xml")) 
		{
			String fileName = inputFile.getPath();
			System.out.println("Loading " + fileName);
			MainFrame m = new MainFrame();
			m.setVisible(true);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			m.refresh();

			m.openTree(fileName);
			m.writeImage(fileName + ".png");
			m.setVisible(false);
			m.dispose();
		}
		else if (inputFile.isDirectory())
		{
			File[] listing = inputFile.listFiles();
			if (listing != null) 
			{
				for (File file : listing) 
				{
					recursiveXML(file);
				}
			}
		}
		else
		{
			System.out.println("Input argument \"" + inputFile.getPath() + "\" is neither an XML file nor a directory.");
		}
	}

	/**
	 * Main
	 * @param args
	 * @throws InterruptedException 
	 */
	public static final void main(String[] args) throws InterruptedException {

		if (args.length > 0) {
			for (String inputString : args) {
				File inputFile = new File(inputString);
				recursiveXML(inputFile);
			}
		}
		else {
			MainFrame m = new MainFrame();
			m.setVisible(true);

			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			m.refresh();
		}
	}
}
