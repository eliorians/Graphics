package modeler.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import modeler.MainFrame;
import modeler.scene.Light;

/**
 * The attribute panel for this node
 * @author arbree
 * Oct 21, 2005
 * Transformation.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class LightAttributePanel extends ItemAttributePanel implements DocumentListener {

	private static final long serialVersionUID = 3256437006337718072L;

	//Text fields for each of the components
	protected JFormattedTextField Px;
	protected JFormattedTextField Py;
	protected JFormattedTextField Pz;
	protected JFormattedTextField Sr;
	protected JFormattedTextField Sg;
	protected JFormattedTextField Sb;

	protected JFileChooser fileChooser;

	protected Light host;

	/**
	 * Inherited constructor
	 * @param mf
	 */
	public LightAttributePanel(MainFrame mf, Light light) {
		super(mf);
		host = light;
	}

	/**
	 * Builds the attribute panel
	 * @param mf
	 */
	public void initialize() {

		Px = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Py = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Pz = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sr = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sg = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sb = new JFormattedTextField(new DecimalFormat("#0.0#"));

		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(2,4));

		JLabel lbl;
		lbl = new JLabel("Pos: ");
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		colorPanel.add(lbl);
		colorPanel.add(Px);
		colorPanel.add(Py);
		colorPanel.add(Pz);
		Px.getDocument().addDocumentListener(this);
		Py.getDocument().addDocumentListener(this);
		Pz.getDocument().addDocumentListener(this);

		lbl = new JLabel("Pow: ");
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		colorPanel.add(lbl);
		colorPanel.add(Sr);
		colorPanel.add(Sg);
		colorPanel.add(Sb);
		Sr.getDocument().addDocumentListener(this);
		Sg.getDocument().addDocumentListener(this);
		Sb.getDocument().addDocumentListener(this);

		BorderLayout fld = new BorderLayout();
		this.setLayout(fld);
		this.add(colorPanel, BorderLayout.NORTH);

	}

	/**
	 * Sets the display fields to the current values of the transformations
	 */
	public void refresh() {

		Px.setValue(new Float(host.getPosition()[0]));
		Py.setValue(new Float(host.getPosition()[1]));
		Pz.setValue(new Float(host.getPosition()[2]));
		Sr.setValue(new Float(host.getSpecular()[0]));
		Sg.setValue(new Float(host.getSpecular()[1]));
		Sb.setValue(new Float(host.getSpecular()[2]));

	}

	/**
	 * Updates the stored data
	 */
	private void update() {

		try {
			host.getPosition()[0] = Float.parseFloat(Px.getText());
			host.getPosition()[1] = Float.parseFloat(Py.getText());
			host.getPosition()[2] = Float.parseFloat(Pz.getText());
			host.getSpecular()[0] = Float.parseFloat(Sr.getText());
			host.getSpecular()[1] = Float.parseFloat(Sg.getText());
			host.getSpecular()[2] = Float.parseFloat(Sb.getText());
			host.getDiffuse()[0] = Light.DIFFUSE_CONSTANT * host.getSpecular()[0];
			host.getDiffuse()[1] = Light.DIFFUSE_CONSTANT * host.getSpecular()[1];
			host.getDiffuse()[2] = Light.DIFFUSE_CONSTANT * host.getSpecular()[2];
			host.getAmbient()[0] = Light.AMBIENT_CONSTANT * host.getSpecular()[0];
			host.getAmbient()[1] = Light.AMBIENT_CONSTANT * host.getSpecular()[1];
			host.getAmbient()[2] = Light.AMBIENT_CONSTANT * host.getSpecular()[2];
			mf.refresh();
		}
		catch (NumberFormatException e) {
			// Do nothing just skip those updates for invalid data
		}

	}

	/**
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {

		update();
	}

	/**
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) {

		update();
	}

	/**
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {

		update();
	}

}
