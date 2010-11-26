package org.jpropeller.ui.impl;

import java.text.DecimalFormat;
import java.text.Format;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A simple {@link TableCellRenderer} for {@link Double}s, displays the value
 * as text, using a {@link Format}
 */
public class FormattedNumberCellRenderer extends DefaultTableCellRenderer {

	private final static FormattedNumberCellRenderer opaque = new FormattedNumberCellRenderer(true);
	private final static FormattedNumberCellRenderer transparent = new FormattedNumberCellRenderer(false);
	
	private final Format format;
	
	/**
	 * Get an opaque instance of {@link FormattedNumberCellRenderer}
	 * @return		The {@link FormattedNumberCellRenderer}
	 */
	public final static FormattedNumberCellRenderer opaque() {
		return opaque;
	}
	
	/**
	 * Get a transparent instance of {@link FormattedNumberCellRenderer}
	 * @return		The {@link FormattedNumberCellRenderer}
	 */
	public final static FormattedNumberCellRenderer transparent() {
		return transparent;
	}
	
	/**
	 * Create a renderer, default format of "#.##"
	 * @param opaque Opaque or not.
	 */
	public FormattedNumberCellRenderer(boolean opaque) {
		this(opaque, new DecimalFormat("#.##"));
	}
	
	/**
	 * Create a renderer
	 * @param opaque Opaque or not.
	 * @param format {@link Format} for numbers
	 */
	public FormattedNumberCellRenderer(boolean opaque, Format format) {
		super();		
		setOpaque(opaque);
		this.format = format;
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		if (value instanceof Number) {
			Number number = (Number)value;
			setText(format.format(number.doubleValue()));
		}
	}
}