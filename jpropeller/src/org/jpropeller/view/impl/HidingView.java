package org.jpropeller.view.impl;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} that just collapses or expands a panel depending
 * on a {@link Prop} of type {@link Boolean}.
 */
public class HidingView implements JView, ChangeListener {

	private Prop<Boolean> prop;
	private UpdateManager updateManager;
	private final JPanel panel;
	private final JPanel hidingPanel;
	private final boolean show;
	private boolean isVisible;
	
	/**
	 * Create a {@link HidingView}
	 * 
	 * @param prop		The {@link Prop} to display
	 * @param show		If true, the panel is visible when
	 * 					prop value is true. Otherwise, it
	 * 					is visible when prop value is false.
	 * @param component	The {@link JComponent} to show in the view
	 */
	public HidingView(Prop<Boolean> prop, boolean show, JComponent component) {
		super();
		this.prop = prop;
		this.show = show;
		
		panel = new JPanel(new BorderLayout());
		hidingPanel = new JPanel(new BorderLayout());
		hidingPanel.setOpaque(false);
		hidingPanel.add(component);
		
		panel.add(hidingPanel);
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		isVisible = true;
		
		//Listen to the prop
		prop.features().addListener(this);

		//Initial update
		update();
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Always update - we only listen to the value, so we only see
		//a change when we need to update
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		Boolean value = prop.get();
		if (value == null) {
			value = true;
		}

		boolean shouldBeVisible = (value == show); 
		
		if (shouldBeVisible != isVisible) {
			hidingPanel.setVisible(shouldBeVisible);
			isVisible = shouldBeVisible;
		}
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		return true;
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
		return Format.LARGE;
	}
	
}
