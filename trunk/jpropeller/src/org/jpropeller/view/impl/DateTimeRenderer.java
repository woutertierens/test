package org.jpropeller.view.impl;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jpropeller.ui.IconAndHTMLRenderer;

/**
 * Renders {@link DateTime} instances
 */
public class DateTimeRenderer implements IconAndHTMLRenderer {

	private final static DateTimeRenderer INSTANCE = new DateTimeRenderer();

	/**
	 * {@link UIManager} key used to store/retrieve default {@link DateTimeFormatter}
	 */
	public static final String DEFAULT_DATETIME_FORMAT_KEY = "itis.datetime.format";
	
	private final static DateTimeFormatter dateTimeFormat;
	static {
		Object o = UIManager.get(DEFAULT_DATETIME_FORMAT_KEY);
		if (o != null && o instanceof DateTimeFormatter) {
			dateTimeFormat = (DateTimeFormatter)o;
		//Default to local short format
		} else {
			dateTimeFormat = DateTimeFormat.shortDateTime();
		}
	}
	
	/**
	 * Get a default {@link DateTimeFormatter} for general use
	 * @return	Default {@link DateTimeFormatter}
	 */
	public final static DateTimeFormatter defaultDateTimeFormatter() { 
		return dateTimeFormat;
	}
	
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
