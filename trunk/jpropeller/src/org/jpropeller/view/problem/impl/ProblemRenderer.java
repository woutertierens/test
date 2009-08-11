package org.jpropeller.view.problem.impl;

import java.util.EnumMap;

import javax.swing.Icon;

import org.jpropeller.problem.Problem;
import org.jpropeller.problem.Problem.ProblemSeverity;
import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.ui.impl.IconAndHTMLCellRenderer;
import org.jpropeller.view.Views;

/**
 * Renders {@link Problem}s, use this for example with {@link IconAndHTMLCellRenderer}
 */
public class ProblemRenderer implements IconAndHTMLRenderer {

	private final static ProblemRenderer INSTANCE = new ProblemRenderer();
	
	/**
	 * Get an instance of {@link ProblemRenderer}
	 * 
	 * @return		A {@link ProblemRenderer}
	 */
	public final static ProblemRenderer get() {
		return INSTANCE;
	}

	EnumMap<ProblemSeverity, String> severityStrings = 
		new EnumMap<ProblemSeverity, String>(ProblemSeverity.class);

	EnumMap<ProblemSeverity, Icon> severityIcons = 
		new EnumMap<ProblemSeverity, Icon>(ProblemSeverity.class);

	/**
	 * Create a {@link ProblemRenderer}
	 */
	private ProblemRenderer() {
		severityStrings.put(ProblemSeverity.ERROR, 		"<font color=\"#dd0000\"><b>Error</b></font>");
		severityStrings.put(ProblemSeverity.WARNING, 	"<font color=\"#cc8800\"><b>Warning</b></font>");
		severityStrings.put(ProblemSeverity.INFO, 		"<font color=\"blue\">Info</font>");
		
		severityIcons.put(ProblemSeverity.ERROR, 		Views.getIconFactory().getIcon(IconSize.LARGE, "status", "dialog-error"));
		severityIcons.put(ProblemSeverity.WARNING, 		Views.getIconFactory().getIcon(IconSize.LARGE, "status", "dialog-warning"));
		severityIcons.put(ProblemSeverity.INFO, 		Views.getIconFactory().getIcon(IconSize.LARGE, "status", "dialog-information"));
	}
	
	@Override
	public boolean canRender(Object o) {
		return (o instanceof Problem);
	}

	@Override
	public String getHTML(Object o) {
		if (canRender(o)) {
			Problem p = (Problem)o;
			StringBuilder s = new StringBuilder();
			s.append(severityStrings.get(p.severity().get()));
			s.append("<br />");
			s.append(p.location().get());
			s.append("<br />");
			s.append(p.description().get());
			
			return s.toString();
		} else {
			return o.toString();
		}
	}

	@Override
	public Icon getIcon(Object o) {
		if (canRender(o)) {
			Problem p = (Problem)o;
			return severityIcons.get(p.severity().get());
		} else {
			return null;
		}
	}

}
