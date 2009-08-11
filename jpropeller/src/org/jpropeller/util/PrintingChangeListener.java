package org.jpropeller.util;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;

/**
 * {@link ChangeListener} that just prints changes to the console
 */
public class PrintingChangeListener implements ChangeListener {

	private final String description;
	
	/**
	 * Create a {@link PrintingChangeListener} with default description
	 * "Change:"
	 */
	public PrintingChangeListener() {
		this("Change:");
	}
	
	/**
	 * Create a {@link PrintingChangeListener}
	 * @param description		To be printed before change details
	 */
	public PrintingChangeListener(String description) {
		this.description = description;
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		System.out.println(description);
		System.out.print(PropUtils.describeChanges(initial, changes));
		System.out.println();
	}
	
}
