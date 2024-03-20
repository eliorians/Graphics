package modeler.ui;

import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import modeler.MainFrame;
import modeler.scene.Transformation;

/**
 * The attribute panel for this node
 * @author arbree
 * Oct 21, 2005
 * Transformation.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public class TransformationAttributePanel extends ItemAttributePanel implements DocumentListener {

	private static final long serialVersionUID = 3256437006337718072L;

	//Text fields for each of the components
	protected JFormattedTextField Tx;
	protected JFormattedTextField Ty;
	protected JFormattedTextField Tz;
	protected JFormattedTextField Rx;
	protected JFormattedTextField Ry;
	protected JFormattedTextField Rz;
	protected JFormattedTextField Sx;
	protected JFormattedTextField Sy;
	protected JFormattedTextField Sz;
	boolean refreshing = false;

	protected Transformation host;

	/**
	 * Inherited constructor
	 * @param mf
	 */
	public TransformationAttributePanel(MainFrame mf, Transformation trans) {
		super(mf);
		host = trans;
	}

	/**
	 * Builds the attribute panel
	 * @param mf
	 */
	public void initialize() {

		Tx = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Ty = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Tz = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Rx = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Ry = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Rz = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sx = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sy = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sz = new JFormattedTextField(new DecimalFormat("#0.0#"));

		JPanel outPanel = new JPanel();
		outPanel.setLayout(new GridLayout(4, 4));

		outPanel.add(new JLabel(""));
		JLabel lbl = new JLabel("X");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		outPanel.add(lbl);
		lbl = new JLabel("Y");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		outPanel.add(lbl);
		lbl = new JLabel("Z");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		outPanel.add(lbl);

		lbl = new JLabel("T");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		outPanel.add(lbl);
		outPanel.add(Tx);
		outPanel.add(Ty);
		outPanel.add(Tz);
		Tx.getDocument().addDocumentListener(this);
		Ty.getDocument().addDocumentListener(this);
		Tz.getDocument().addDocumentListener(this);

		lbl = new JLabel("R");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		outPanel.add(lbl);
		outPanel.add(Rx);
		outPanel.add(Ry);
		outPanel.add(Rz);
		Rx.getDocument().addDocumentListener(this);
		Ry.getDocument().addDocumentListener(this);
		Rz.getDocument().addDocumentListener(this);

		lbl = new JLabel("S");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		outPanel.add(lbl);
		outPanel.add(Sx);
		outPanel.add(Sy);
		outPanel.add(Sz);
		Sx.getDocument().addDocumentListener(this);
		Sy.getDocument().addDocumentListener(this);
		Sz.getDocument().addDocumentListener(this);

		this.setLayout(new GridLayout(2,1));
		this.add(outPanel);
	}

	/**
	 * Sets the display fields to the current values of the transformations
	 */
	public void refresh() {

		refreshing = true;
		Tx.setValue(new Float(host.getT().x));
		Ty.setValue(new Float(host.getT().y));
		Tz.setValue(new Float(host.getT().z));
		Rx.setValue(new Float(host.getR().x));
		Ry.setValue(new Float(host.getR().y));
		Rz.setValue(new Float(host.getR().z));
		Sx.setValue(new Float(host.getS().x));
		Sy.setValue(new Float(host.getS().y));
		Sz.setValue(new Float(host.getS().z));
		refreshing = false;
	}

	/**
	 * Updates the stored data
	 */
	private void update() {

		if(refreshing)
			return;
		try {
			host.getT().x = Float.parseFloat(Tx.getText());
			host.getT().y = Float.parseFloat(Ty.getText());
			host.getT().z = Float.parseFloat(Tz.getText());
			host.getR().x = Float.parseFloat(Rx.getText());
			host.getR().y = Float.parseFloat(Ry.getText());
			host.getR().z = Float.parseFloat(Rz.getText());
			host.getS().x = Float.parseFloat(Sx.getText());
			host.getS().y = Float.parseFloat(Sy.getText());
			host.getS().z = Float.parseFloat(Sz.getText());
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
