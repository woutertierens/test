package org.jpropeller.ui;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A simple {@link TableCellRenderer} for {@link Icon}s, displays the icon in a
 * label. Should have the same behaviour as the JTable default, but it available
 * to be set for different data types, etc.
 * 
 * @author shingoki
 */
public class IconCellRenderer extends DefaultTableCellRenderer {

	/**
	 * Create a renderer
	 */
	public IconCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	@Override
	public void setValue(Object value) {
		setIcon((value instanceof Icon) ? (Icon) value : null);
	}
}