package org.jpropeller.view.primitive.impl;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.reference.Reference;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link Prop} with value of type {@link Boolean}
 */
public class BooleanCheckboxEditor implements JView, UpdatableSingleValueView<Bean> {

	private PropViewHelp<Bean, Boolean> help;

	private Reference<? extends Bean> model;

	private JCheckBox checkBox;
	private PropName<Boolean> displayedName;

	private boolean settingEnabled = false;
	
	private BooleanCheckboxEditor(Reference<? extends Bean> model,
			PropName<Boolean> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		
		checkBox = new JCheckBox("");
		help = new PropViewHelp<Bean, Boolean>(this, displayedName);
		
		checkBox.addChangeListener(new ChangeListener() {
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
		help.connect();
	}

	@Override
	public void dispose() {
		help.dispose();
	}

	/**
	 * Create a {@link BooleanCheckboxEditor}
	 * 
	 * @param model			The {@link Reference} for this {@link View} 
	 * @param displayedName	The name of the displayed property 
	 * @return				A new{@link BooleanCheckboxEditor}
	 */
	public static BooleanCheckboxEditor create(Reference<? extends Bean> model,
			PropName<Boolean> displayedName) {
		return new BooleanCheckboxEditor(model, displayedName);
	}

	@Override
	public Reference<? extends Bean> getModel() {
		return model;
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
				help.setPropValue(checkBox.isSelected());
			} catch (ReadOnlyException e) {
				update();
			} catch (InvalidValueException e) {
				update();
			}
		}
	}

	@Override
	public boolean isEditing() {
		
		Boolean value = help.getPropValue();
		
		//If value is null, not editing
		if (checkNull(value)) return false;
		
		return checkBox.isSelected() != value;
	}
	
	@Override
	public void update() {
		
		Boolean value = help.getPropValue();
		
		//If value is null, can't display
		if (checkNull(value)) return;
		
		//First time we see a non-null value, set our name from its prop
		if (checkBox.getText().length()==0) {
			Bean bean = model.value().get();
			checkBox.setText(PropUtils.localisedName(bean.getClass(), bean.features().get(displayedName)));
		}
		
		if (isEditing()) {
			checkBox.setSelected(value);
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
