package org.jpropeller.ui.impl;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.Views;

/**
 * A simple {@link TableCellRenderer} for {@link Boolean}s, displays the value
 * as a label with a tick icon for {@link Boolean} true, and no icon for anything
 * else.
 */
public class BooleanCellRenderer extends DefaultTableCellRenderer {

	private final static Icon tick = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "tick");
	private final static Icon untick = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "untick");
	
	private final static BooleanCellRenderer instance = new BooleanCellRenderer();
	
	/**
	 * Get an instance of {@link BooleanCellRenderer}
	 * @return		The {@link BooleanCellRenderer}
	 */
	public final static BooleanCellRenderer get() {
		return instance;
	}
	
	/**
	 * Create a renderer
	 */
	BooleanCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Boolean) {
			setIcon(((Boolean)value) ? tick : untick);
		} else {
			setIcon(null);	
		}
	}
}