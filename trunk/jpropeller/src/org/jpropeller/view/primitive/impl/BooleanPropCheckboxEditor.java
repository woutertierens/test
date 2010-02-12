package org.jpropeller.view.primitive.impl;

import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdateManager;

/**
 * An editing {@link View} for a {@link Prop} with value of type {@link Boolean}
 */
public class BooleanPropCheckboxEditor implements JView, ChangeListener {

	private Prop<Boolean> value;
	private Prop<String> name;
	private UpdateManager updateManager;

	private JCheckBox checkBox;

	private boolean settingEnabled = false;
	
	private BooleanPropCheckboxEditor(Prop<Boolean> value, Prop<String> name) {
		super();
		this.value = value;
		this.name = name;
		
		checkBox = new JCheckBox("");
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		value.features().addListener(this);
		name.features().addListener(this);
		
		checkBox.addChangeListener(new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//We can't tell whether a change event is for the
				//actual selected state of the checkbox changing,
				//or its enabled state. So we need to flag the difference
				//ourself, to avoid committing when we change enabled state.
				//This is important because we may change enabled state in
				//response to a Changeable change, and we don't want to
				//then commit() and break ChangeListener contract about
				//not writing from an event.
				if (!settingEnabled) {
					commit();
				}
			}
		});
		
		//Initial update
		update();

	}
	
	/**
	 * Make a new {@link BooleanPropCheckboxEditor}
	 * @param value		The {@link Boolean} value to display
	 * @param name		The name to display alongside checkbox
	 * @return			A new {@link BooleanPropCheckboxEditor}
	 */
	public static BooleanPropCheckboxEditor create(Prop<Boolean> value, Prop<String> name) {
		return new BooleanPropCheckboxEditor(value, name);
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	@Override
	public void dispose() {
		name.features().removeListener(this);
		value.features().removeListener(this);
	}
	
	/**
	 * Get the {@link JCheckBox} used for display/editing
	 * 
	 * @return		The {@link JCheckBox}
	 */
	public JCheckBox getComponent() {
		return checkBox;
	}
	
	@Override
	public void cancel() {
		update();
	}

	private boolean checkNull(Boolean value) {
		boolean n = (value == null);
		settingEnabled  = true;
		checkBox.setEnabled(!n);
		settingEnabled = false;
		return n;
	}
	
	@Override
	public void commit() {
		if (isEditing()) {
			//TODO indicate error to user
			try {
				value.set(checkBox.isSelected());
			} catch (ReadOnlyException e) {
				update();
			} catch (InvalidValueException e) {
				update();
			}
		}
	}

	@Override
	public boolean isEditing() {
		
		Boolean b = value.get();
		
		//If value is null, not editing
		if (checkNull(b)) return false;
		
		return checkBox.isSelected() != b.booleanValue();
	}
	
	@Override
	public void update() {
		
		Boolean b = value.get();
		
		//If value is null, can't display
		if (!checkNull(b)) {
			if (isEditing()) {
				checkBox.setSelected(b);
			}
		}

		//Update the checkbox name display
		String s = name.get();
		if (s == null) {
			s = "";
		} 
		if (!s.equals(checkBox.getText())) {
			checkBox.setText(s);
		}
	}

	@Override
	public boolean selfNaming() {
		//Displays name of property as well as its value
		return true;
	}
	
	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}
}
