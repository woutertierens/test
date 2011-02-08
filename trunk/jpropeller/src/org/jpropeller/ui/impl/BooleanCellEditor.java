package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

/**
 * Simple {@link TableCellEditor} for {@link Boolean}, toggling
 * on a single click
 */
public class BooleanCellEditor extends AbstractCellEditor implements TableCellEditor {

	private boolean completeImmediately = false;
	private Boolean toggled = false;
	private Boolean originalValue = false;
	private Boolean inComponent = false;

	private final BooleanCellRenderer renderer = new BooleanCellRenderer(true);
	private Component rendererComponent = null;

	private final static Color highlightColor = new Color(255, 255, 255, 50);
	
	private final JLabel label = new JLabel() {
		protected void paintComponent(java.awt.Graphics g) {
			if (rendererComponent != null) {
				rendererComponent.setBounds(getBounds());
				rendererComponent.paint(g);
			}
			if (inComponent) {
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(highlightColor);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
	};
	
	/**
	 * Create a {@link BooleanCellEditor}
	 */
	public BooleanCellEditor() {
		label.setOpaque(true);
		//label.setHorizontalAlignment(JLabel.CENTER);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if (label.contains(e.getPoint())) {
					toggled = true;
				}
				fireEditingStopped();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				inComponent = true;
				label.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				inComponent = false;
				label.repaint();
			}
		});
	}
	
	//Implement the one CellEditor method that AbstractCellEditor doesn't.
	public Object getCellEditorValue() {
		boolean newValue = toggled ? !originalValue : originalValue;
		return newValue;
	}

	//Implement the one method defined by TableCellEditor.
	@Override
	public Component getTableCellEditorComponent(JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		if (value instanceof Boolean) {
			originalValue = (Boolean)value;
			
			//Normally we start out doing nothing
			toggled = false;
			
			//If we will complete immediately, then invoke
			//an event so the editor will essentially never appear,
			//and also set toggled so we will edit.
			if (completeImmediately) {
				toggled = true;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						fireEditingStopped();			
					}
				});
			}
			/*
			if (originalValue){
				label.setIcon(BooleanCellRenderer.tick);
			} else {
				label.setIcon(null);
			}
			System.out.println(isSelected);
			label.setBackground(rendererComponent.getBackground());
			label.setForeground(rendererComponent.getForeground());
			*/
			rendererComponent = renderer.getTableCellRendererComponent(table, value, true, true, row, column);

			return label;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		//Only single clicks start editing using mouse, and
		//in this case we want to wait for the mouse to be released
		if (anEvent instanceof MouseEvent) {
			inComponent = true;
			completeImmediately = false;			
			return ((MouseEvent) anEvent).getClickCount() >= 1;
		//Other events start editing, but complete immediately - 
		//this will just toggle the contents
		} else {
			completeImmediately = true;
			return true;
		}
	}
}
