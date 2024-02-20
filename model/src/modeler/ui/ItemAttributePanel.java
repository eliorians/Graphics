/*
 * Created on Oct 22, 2005
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package modeler.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import modeler.MainFrame;


/**
 * Base class of all the attribute panels
 * @author arbree
 * Oct 22, 2005
 * ItemAttributePanel.java
 * Copyright 2005 Program of Computer Graphics, Cornell University
 */
public abstract class ItemAttributePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8916728017199406129L;

	//The fixed size of attribute panels
	private static final Dimension SIZE = new Dimension(120, 250);

	//The frame holding this attribute panel
	protected MainFrame mf;

	//The empty attribute panel
	public static final ItemAttributePanel EMPTY_PANEL = new EmptyAttributePanel(null);

	/**
	 * Creates a fixed size attribute panel
	 */
	protected ItemAttributePanel(MainFrame mf) {

		super();
		this.mf = mf;
		initialize();
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setMaximumSize(SIZE);
		this.setMinimumSize(SIZE);
		this.setPreferredSize(SIZE);

	}

	/**
	 * Sets up this panel
	 */
	public abstract void initialize();

	/*
	 * Update the values displayed in this panel
	 */
	public abstract void refresh();

	/**
	 * Takes in a GUI component and some text and returns a labelled version of the GUI component.
	 * @param component The component to be labelled.
	 * @param text The text to go in the label.
	 * @return a new panel with the label and element
	 */
	public static JPanel addLabelLeft(JComponent component, String text) {

		BorderLayout bl = new BorderLayout();
		bl.setHgap(5);
		JPanel toReturn = new JPanel(bl);

		JLabel lbl = new JLabel(text);
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		toReturn.add(lbl, BorderLayout.WEST);
		toReturn.add(component, BorderLayout.CENTER);

		return toReturn;

	}

	/**
	 * Special empty panel
	 * @author arbree
	 * Oct 22, 2005
	 * ItemAttributePanel.java
	 * Copyright 2005 Program of Computer Graphics, Cornell University
	 */
	private static class EmptyAttributePanel extends ItemAttributePanel {

		private static final long serialVersionUID = 4121132545741109552L;

		/**
		 * @param mf
		 */
		protected EmptyAttributePanel(MainFrame mf) {

			super(mf);

		}

		/**
		 * @see modeler.ui.ItemAttributePanel#initialize()
		 */
		public void initialize() {

			this.setLayout(new BorderLayout());
			JLabel lbl = new JLabel("Empty.");
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			this.add(lbl, BorderLayout.CENTER);

		}

		public void refresh() {

		}
	}
}
