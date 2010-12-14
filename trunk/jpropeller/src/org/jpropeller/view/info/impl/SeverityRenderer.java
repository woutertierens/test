package org.jpropeller.view.info.impl;

import java.util.EnumMap;

import javax.swing.Icon;

import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.Views;
import org.jpropeller.view.info.Severity;
import org.jpropeller.view.info.WithSeverity;

/**
 * Renders {@link Severity} values, or {@link WithSeverity} instances, 
 * as an icon chosen to indicate level.
 */
public class SeverityRenderer implements IconAndHTMLRenderer {

	private final static SeverityRenderer INSTANCE = new SeverityRenderer(IconSize.MEDIUM);
	
	private boolean showText = false;
	
	/**
	 * Get an instance of {@link SeverityRenderer}
	 * 
	 * @return		A {@link SeverityRenderer}
	 */
	public final static SeverityRenderer get() {
		return INSTANCE;
	}

	EnumMap<Severity, String> severityStrings = 
		new EnumMap<Severity, String>(Severity.class);

	EnumMap<Severity, Icon> severityIcons = 
		new EnumMap<Severity, Icon>(Severity.class);

	/**
	 * Create a {@link SeverityRenderer}
	 */
	private SeverityRenderer(IconSize iconSize) {
		severityStrings.put(Severity.FAILURE, 	"<font color=\"#f76000\"><b><i>" + 	Severity.FAILURE + 	"</i></b></font>");
		severityStrings.put(Severity.ERROR, 	"<font color=\"#dd0000\"><b>" + 	Severity.ERROR + 	"</b></font>");
		severityStrings.put(Severity.WARNING, 	"<font color=\"#cc8800\"><b>" + 	Severity.WARNING + 	"</b></font>");
		severityStrings.put(Severity.INFO, 		"<font color=\"blue\">" + 			Severity.INFO + 	"</font>");
		severityStrings.put(Severity.UPDATE, 	"<font color=\"#000000\">" + 		Severity.UPDATE + 	"</font>");
		severityStrings.put(Severity.FINE, 		"<font color=\"#747474\">" + 		Severity.FINE + 	"</font>");
		severityStrings.put(Severity.FINER, 	"<font color=\"#747474\">" + 		Severity.FINER + 	"</font>");
		severityStrings.put(Severity.FINEST, 	"<font color=\"#747474\">" + 		Severity.FINEST + 	"</font>");
		
		severityIcons.put(Severity.FAILURE, 	Views.getIconFactory().getIcon(iconSize, "status", "software-update-urgent"));
		severityIcons.put(Severity.ERROR, 		Views.getIconFactory().getIcon(iconSize, "status", "dialog-error"));
		severityIcons.put(Severity.WARNING, 	Views.getIconFactory().getIcon(iconSize, "status", "dialog-warning"));
		severityIcons.put(Severity.INFO, 		Views.getIconFactory().getIcon(iconSize, "status", "dialog-information"));
		severityIcons.put(Severity.UPDATE, 		null);
		severityIcons.put(Severity.FINE, 		null);
		severityIcons.put(Severity.FINER, 		null);
		severityIcons.put(Severity.FINEST, 		null);
	}
	
	@Override
	public boolean canRender(Object o) {
		return (o instanceof Severity || o instanceof WithSeverity);
	}

	@Override
	public String getHTML(Object o) {
		
		if (!showText) {
			return "";
		}
		
		Severity sev = toSeverity(o);
		
		if (sev != null) {
			StringBuilder s = new StringBuilder();
			s.append(severityStrings.get(sev));
			s.append("<br />");
			s.append(o.toString());

			return s.toString();
		} else {
			return o.toString();
		}
	}

	@Override
	public Icon getIcon(Object o) {
		Severity sev = toSeverity(o);

		if (sev != null) {
			return severityIcons.get(sev);
		} else {
			return null;
		}
	}

	private Severity toSeverity(Object o) {
		Severity sev = null;
		if (o instanceof Severity) {
			sev = (Severity)o;
		} else if (o instanceof WithSeverity) {
			sev = ((WithSeverity)o).severity().get();
		}
		return sev;
	}

}
