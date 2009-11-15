package org.jpropeller.view.impl;

import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} of a {@link Prop} of Integer value, which will display its
 * value as a count, with an icon appearing when the count is not 0
 */
public class CountView implements JView, ChangeListener {

	private Prop<Integer> prop;
	private UpdateManager updateManager;
	private JLabel label;
	private final Icon icon;
	
	/**
	 * Create a {@link CountView}
	 * 
	 * @param prop		The {@link Prop} to display
	 * @param icon		The icon to display for non-zero counts, or null if no icon is required.
	 */
	public CountView(Prop<Integer> prop, Icon icon) {
		super();
		this.prop = prop;
		this.icon = icon;
		
		label = new JLabel();
		label.setHorizontalTextPosition(SwingConstants.LEFT);
		label.setVerticalTextPosition(SwingConstants.TOP);
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to the prop
		prop.features().addListener(this);

		//Initial update
		update();
	}
	
	/**
	 * Create a {@link CountView}
	 * 
	 * @param ref		The reference to display
	 * @param icon		The icon to display for non-zero counts, or null if no icon is required.
	 */
	public CountView(Reference<Integer> ref, Icon icon) {
		this(ref.value(), icon);
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Always update - we only listen to the value, so we only see
		//a change when we need to update
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		Integer value = prop.get();
		
		String s = "";
		if (value != null && value.intValue() != 0) {
			s = value.toString();
		}
		
		if (!s.equals(label.getText())) {
			label.setText(s);
		}
		
		if (value != 0) {
			label.setIcon(icon);
		} else {
			label.setIcon(null);
		}
	}

	@Override
	public JComponent getComponent() {
		return label;
	}

	@Override
	public boolean selfNaming() {
		return false;
	}

	@Override
	public void cancel() {
		//Never edits - nothing to do
	}

	@Override
	public void commit() throws CompletionException {
		//Never edits - nothing to do
	}

	@Override
	public boolean isEditing() {
		//Never edits
		return false;
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		prop.features().removeListener(this);
	}

	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}

}
