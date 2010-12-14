package org.jpropeller.view.impl;

import javax.swing.Icon;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.view.info.Notable;

/**
 * Renders {@link Notable} instances, or the
 * {@link String} values from the {@link Notable#note()} properties.
 * Icon is blank if there is no note (null or whitespace only),
 * or a question mark if there is a note.
 */
public class DateTimeRenderer implements IconAndHTMLRenderer {

	private final static DateTimeRenderer INSTANCE = new DateTimeRenderer();
	private final static DateTimeFormatter dateTimeFormat = DateTimeFormat.shortDateTime();

	/**
	 * Shared instances only
	 */
	private DateTimeRenderer() {
	}

	/**
	 * Get a shared instance of the {@link DateTimeRenderer}
	 * @return		A {@link DateTimeRenderer}
	 */
	public final static DateTimeRenderer get() {
		return INSTANCE;
	}

	@Override
	public boolean canRender(Object o) {
		return (o instanceof DateTime);
	}

	@Override
	public String getHTML(Object o) {
		if (o == null) {
			return "";
		} else if (o instanceof DateTime) {
			return dateTimeFormat.print((DateTime)o);
		} else {
			return o.toString();
		}
	}

	@Override
	public Icon getIcon(Object o) {
		return null;
	}
}
