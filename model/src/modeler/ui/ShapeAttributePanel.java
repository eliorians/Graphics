package modeler.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import modeler.MainFrame;
import modeler.shape.Shape;

public class ShapeAttributePanel extends ItemAttributePanel implements ActionListener, DocumentListener {
	private static final long serialVersionUID = 3256437006337718072L;

	//Text fields for each of the components
	protected JFormattedTextField Dr;
	protected JFormattedTextField Dg;
	protected JFormattedTextField Db;
	protected JFormattedTextField Sr;
	protected JFormattedTextField Sg;
	protected JFormattedTextField Sb;
	protected JFormattedTextField exp;

	protected Shape host;

	/**
	 * Inherited constructor
	 * @param mf
	 */
	public ShapeAttributePanel(MainFrame mf, Shape shape) {
		super(mf);
		host = shape;
	}

	/**
	 * Builds the attribute panel
	 * @param mf
	 */
	public void initialize() {

		Dr = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Dg = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Db = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sr = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sg = new JFormattedTextField(new DecimalFormat("#0.0#"));
		Sb = new JFormattedTextField(new DecimalFormat("#0.0#"));
		exp = new JFormattedTextField(new DecimalFormat("#0.0#"));

		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(4,4));

		colorPanel.add(new JLabel(""));
		JLabel lbl = new JLabel("R");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		colorPanel.add(lbl);
		lbl = new JLabel("G");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		colorPanel.add(lbl);
		lbl = new JLabel("B");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		colorPanel.add(lbl);

		lbl = new JLabel("Diff: ");
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		colorPanel.add(lbl);
		colorPanel.add(Dr);
		colorPanel.add(Dg);
		colorPanel.add(Db);
		Dr.getDocument().addDocumentListener(this);
		Dg.getDocument().addDocumentListener(this);
		Db.getDocument().addDocumentListener(this);

		lbl = new JLabel("Spec: ");
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		colorPanel.add(lbl);
		colorPanel.add(Sr);
		colorPanel.add(Sg);
		colorPanel.add(Sb);
		Sr.getDocument().addDocumentListener(this);
		Sg.getDocument().addDocumentListener(this);
		Sb.getDocument().addDocumentListener(this);

		lbl = new JLabel("Exp: ");
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		colorPanel.add(lbl);
		colorPanel.add(exp);
		exp.getDocument().addDocumentListener(this);
		colorPanel.add(new JLabel());
		colorPanel.add(new JLabel());

		BorderLayout fld = new BorderLayout();
		this.setLayout(fld);
		this.add(colorPanel, BorderLayout.NORTH);
	}

	/**
	 * Sets the display fields to the current values of the transformations
	 */
	public void refresh() {

		Dr.setValue(new Float(host.getDiffuseColor()[0]));
		Dg.setValue(new Float(host.getDiffuseColor()[1]));
		Db.setValue(new Float(host.getDiffuseColor()[2]));
		Sr.setValue(new Float(host.getSpecularColor()[0]));
		Sg.setValue(new Float(host.getSpecularColor()[1]));
		Sb.setValue(new Float(host.getSpecularColor()[2]));
		exp.setValue(new Float(host.getExpSpecular()));

	}

	/**
	 * Updates the stored data
	 */
	private void update() {

		try {
			host.getDiffuseColor()[0] = Float.parseFloat(Dr.getText());
			host.getDiffuseColor()[1] = Float.parseFloat(Dg.getText());
			host.getDiffuseColor()[2] = Float.parseFloat(Db.getText());
			host.getSpecularColor()[0] = Float.parseFloat(Sr.getText());
			host.getSpecularColor()[1] = Float.parseFloat(Sg.getText());
			host.getSpecularColor()[2] = Float.parseFloat(Sb.getText());
			host.setExpSpecular( Float.parseFloat(exp.getText()) );
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

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

	}
}
