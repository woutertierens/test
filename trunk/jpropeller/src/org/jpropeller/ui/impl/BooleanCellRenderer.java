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
	
	private final static BooleanCellRenderer opaque = new BooleanCellRenderer(true);
	private final static BooleanCellRenderer transparent = new BooleanCellRenderer(false);
	
	/**
	 * Get an opaque instance of {@link BooleanCellRenderer}
	 * @return		The {@link BooleanCellRenderer}
	 */
	public final static BooleanCellRenderer opaque() {
		return opaque;
	}
	
	/**
	 * Get a transparent instance of {@link BooleanCellRenderer}
	 * @return		The {@link BooleanCellRenderer}
	 */
	public final static BooleanCellRenderer transparent() {
		return transparent;
	}
	
	/**
	 * Create a renderer
	 * @param opaque Opaque or not.
	 */
	BooleanCellRenderer(boolean opaque) {
		super();		
		setOpaque(opaque);
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