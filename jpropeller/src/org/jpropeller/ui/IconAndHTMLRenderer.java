package org.jpropeller.ui;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * A renderer that can display an object as
 * an icon and/or HTML
 */
public interface IconAndHTMLRenderer {

	/**
	 * Checks whether an object can be rendered
	 * 
	 * @param o		The object to render
	 * @return		True if the object can be rendered,
	 * 				false otherwise.
	 */
	public boolean canRender(Object o);
	
	/**
	 * Get the icon with which to render an object
	 * 
	 * @param o		The object to render
	 * @return		The icon
	 */
	public Icon getIcon(Object o);
	
	/**
	 * Get the HTML snippet with which to render an object.
	 * This does NOT need the &lt;html&gt; and &lt;/html&gt;
	 * tags, and should be suitable for use as a paragraph of
	 * HTML to be displayed by a {@link JLabel} etc.
	 * 
	 * @param o		The object to render
	 * @return		The HTML
	 */
	public String getHTML(Object o);
	
}
