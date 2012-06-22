package org.jpropeller.ui.impl;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

/**
 * Wraps a {@link TableCellRenderer} to make it render text vertically
 */
public class RotatedTableCellRenderer implements TableCellRenderer {

	private final TableCellRenderer delegate;
	private RotatedTextIcon icon = null;
	
	/**
	 * Create a {@link TableCellRenderer} that renders exactly like the delegate,
	 * but with icons removed, and with text displayed vertically using an icon.
	 * @param delegate	The delegate
	 */
	public RotatedTableCellRenderer(TableCellRenderer delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			String text = label.getText();
			label.setText("");
			
			if (icon == null) {
				icon = new RotatedTextIcon(c);
			}
			
			icon.setText(text);
			
			label.setIcon(icon);
		}
		return c;
	}

	private static class RotatedTextIcon implements Icon{
		private final Component comp;
		private String s;
		
		private final static int border = 6;
		
		public RotatedTextIcon(Component comp) {
			super();
			this.comp = comp;
		}

		public void setText(String s) {
			this.s = s;
		}

		@Override
		public void paintIcon(Component c, Graphics gOld, int xIgnore, int yIgnore) {
			Graphics2D g = (Graphics2D)gOld;
			
			// We don't insist that it be on the same Component
			g.setColor(c.getForeground());
			g.setFont(c.getFont());
			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
		    Rectangle2D d = g.getFontMetrics().getStringBounds(s, g);
		    double w = d.getWidth();
		    double h = d.getHeight();
		    h = g.getFontMetrics().getAscent();

		    int x = c.getWidth()/2;
		    int y = c.getHeight() - border;
		    		
		    AffineTransform oldxForm = g.getTransform();
		    
		    AffineTransform t = g.getTransform();
		    t.concatenate(AffineTransform.getQuadrantRotateInstance(-1, x, y));
    	    g.setTransform(t);

    	    double alignX = 0;
    	    double alignY = 0.4;
    	    int ox = (int)(x - w * alignX);
    	    int oy = (int)(y + h * alignY);
    	    
    	    g.drawString(s, ox, oy);

    	    g.setTransform(oldxForm);
			
		}
		
		@Override
		public int getIconWidth() {
			FontMetrics fm = comp.getFontMetrics(comp.getFont());
			return fm.getHeight() + border * 2;
		}
		
		@Override
		public int getIconHeight() {
			FontMetrics fm = comp.getFontMetrics(comp.getFont());
			return SwingUtilities.computeStringWidth(fm, s) + border * 2;
		}
		
	}
	
}
