package org.jpropeller.ui.impl;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.ui.IconAndHTMLRenderer;

/**
 * A simple {@link TableCellRenderer} for any class, which will delegate
 * to an {@link IconAndHTMLRenderer} to give the contents of the {@link JLabel}
 * for each displayed object
 */
public class IconAndHTMLCellRenderer extends DefaultTableCellRenderer {

	private final IconAndHTMLRenderer delegate;
	private final int verticalAlignment;
	private final boolean showIcon; 
	private final boolean showHTML; 
	
	/**
	 * Create a renderer showing icon and html, with label aligned centrally
	 * 
	 * @param delegate		The delegate used to render to icon and/or html 
	 */
	public IconAndHTMLCellRenderer(IconAndHTMLRenderer delegate) {
		this(delegate, SwingConstants.CENTER);
	}

	/**
	 * Create a renderer showing icon and html
	 * 
	 * @param delegate			The delegate used to render to icon and/or html
	 * @param verticalAlignment The alignment of the label used to render - one 
	 * 							of {@link SwingConstants#TOP}, {@link SwingConstants#BOTTOM}
	 * 							or {@link SwingConstants#CENTER}
	 */
	public IconAndHTMLCellRenderer(IconAndHTMLRenderer delegate, int verticalAlignment) {
		this(delegate, verticalAlignment, true, true, true);
	}
	
	/**
	 * Create a renderer
	 * 
	 * @param delegate			The delegate used to render to icon and/or html
	 * @param verticalAlignment The alignment of the label used to render - one 
	 * 							of {@link SwingConstants#TOP}, {@link SwingConstants#BOTTOM}
	 * 							or {@link SwingConstants#CENTER}
	 * @param showIcon			True to show icon 
	 * @param showHTML 			True to show html
	 * @param opaque 			True to render opaque
	 */
	public IconAndHTMLCellRenderer(IconAndHTMLRenderer delegate, int verticalAlignment, boolean showIcon, boolean showHTML, boolean opaque) {
		super();
		setOpaque(opaque);
		this.delegate = delegate;
		this.verticalAlignment = verticalAlignment;
		this.showIcon = showIcon;
		this.showHTML = showHTML;
	}

	@Override
	public void setValue(Object value) {
		
		//If we can render, do so. Otherwise retain default behaviour.
		if (delegate.canRender(value)) {
			setIcon(showIcon ? delegate.getIcon(value) : null);
			setText(showHTML ? "<html>" + delegate.getHTML(value) + "</html>" : "");
		} else {
			setIcon(null);
			super.setValue(value);
		}
		setVerticalAlignment(verticalAlignment);
	}
}