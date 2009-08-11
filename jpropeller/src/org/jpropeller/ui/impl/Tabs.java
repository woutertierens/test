package org.jpropeller.ui.impl;

import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.jpropeller.bean.impl.BeanDefault;
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
 * A {@link JView} that doesn't actually view any data - just
 * manages the selection of a tab in a {@link JTabbedPane} to
 * allow for convenient behaviour with undo system
 */
public class Tabs implements JView, ChangeListener{

	TabSelectionModel model;	
	JTabbedPane tabs;
	private UpdateManager updateManager;

	/**
	 * Create a {@link Tabs} instance
	 */
	public Tabs() {
		
		model = new TabSelectionModel();

		tabs = new JTabbedPane();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		//Start out updated
		updateManager.updateRequiredBy(this);

		//Listen to the model selection
		model.selectedTabIndex().features().addListener(this);

		tabs.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateModelFromTabs();
			}
		});
		
	}
	
	private void updateModelFromTabs() {
		//If prop does not match currently selected tab, update prop
		int index = tabs.getSelectedIndex();
		if (index != model.selectedTabIndex().get()) {
			model.selectedTabIndex().set(index);
		}
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		model.selectedTabIndex().features().removeListener(this);
	}

	@Override
	public void update() {
		//If currently selected tab does not match prop, update selected tab (if possible)
		int index = model.selectedTabIndex().get();
		if (index != tabs.getSelectedIndex() && index >= 0 && index < tabs.getTabCount()) {
			tabs.setSelectedIndex(index);
		}
	}
	
	/**
	 * Special selection model for {@link Tabs} 
	 */
	private class TabSelectionModel extends BeanDefault implements Reference<String> {

		//The (unused) value prop
		private final Prop<String> value = create("value", "tabs default value");
		
		//The actual point of the reference - the currently selected tab index
		private final Prop<Integer> selectedTabIndex = editable("selectedTabIndex", 0);
		
		@Override
		public Prop<String> value() {
			return value;
		}

		public Prop<Integer> selectedTabIndex() {
			return selectedTabIndex;
		}
	}
	
	//Boring implementation
	@Override
	public JTabbedPane getComponent() {
		return tabs;
	}

	@Override
	public boolean selfNaming() {
		//No name is required - so might as well declare as self naming
		return true;
	}

	@Override
	public void cancel() {
		//No editing, so no need to cancel
	}

	@Override
	public void commit() throws CompletionException {
		//No editing, so no need to commit
	}

	@Override
	public boolean isEditing() {
		//No editing
		return false;
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}
}
