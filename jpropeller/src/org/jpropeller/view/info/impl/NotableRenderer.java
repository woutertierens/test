package org.jpropeller.view.info.impl;

import javax.swing.Icon;

import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.Views;
import org.jpropeller.view.info.Notable;

/**
 * Renders {@link Notable} instances, or the
 * {@link String} values from the {@link Notable#note()} properties.
 * Icon is blank if there is no note (null or whitespace only),
 * or a question mark if there is a note.
 */
public class NotableRenderer implements IconAndHTMLRenderer {

	private final static NotableRenderer DISPLAY_TEXT_INSTANCE = new NotableRenderer(true);
	private final static NotableRenderer INSTANCE = new NotableRenderer(false);
	
	private final static Icon noteIcon = Views.getIconFactory().getIcon(IconSize.SMALL, "status", "mail-attachment");

	private final boolean displayText;
	
	/**
	 * Shared instances only
	 */
	private NotableRenderer(boolean displayText) {
		this.displayText = displayText;
	}

	/**
	 * Get a shared instance of the {@link NotableRenderer},
	 * which does not display text
	 * @return		A {@link NotableRenderer}
	 */
	public final static NotableRenderer get() {
		return get(false);
	}

	/**
	 * Get a shared instance of the {@link NotableRenderer}
	 * @param displayText	True to display text, false to omit it
	 * @return		A {@link NotableRenderer}
	 */
	public final static NotableRenderer get(boolean displayText) {
		return displayText ? DISPLAY_TEXT_INSTANCE : INSTANCE;
	}

	@Override
	public boolean canRender(Object o) {
		return (o == null || o instanceof Notable || o instanceof String);
	}

	@Override
	public String getHTML(Object o) {
		if (o == null) {
			return "";
		} else if (o instanceof String) {
			if (displayText) {
				return (String)o;
			} else {
				return isNote(o) ? "..." : "";
			}
		} else if (o instanceof Notable) {
			if (displayText) {
				Notable notable = (Notable)o;
				return notable.note().get();
			} else {
				return isNote(o) ? "..." : "";
			}
		} else {
			return o.toString();
		}
	}

	@Override
	public Icon getIcon(Object o) {
		return isNote(o) ? noteIcon : null;
	}
	
	private static final boolean isNote(Object o) {
		String note = null;
		if (o instanceof Notable) {
			Notable notable = (Notable)o;
			note = notable.note().get();
		} else if (o instanceof String) {
			note = (String)o;
		}

		return (note != null && !note.trim().isEmpty());
	}

}
