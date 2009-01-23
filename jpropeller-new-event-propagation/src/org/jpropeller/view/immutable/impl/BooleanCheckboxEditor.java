package org.jpropeller.view.immutable.impl;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.reference.Reference;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;
import org.jpropeller.view.update.UpdatableView;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link EditableProp} with value of type {@link Boolean}
 */
public class BooleanCheckboxEditor implements UpdatableView<Bean>, JView<Bean> {

	private PropViewHelp<Bean, Boolean> help;

	private Reference<? extends Bean> model;

	private JCheckBox checkBox;
	private PropName<? extends EditableProp<Boolean>, Boolean> displayedName;
	
	private BooleanCheckboxEditor(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Boolean>, Boolean> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		
		checkBox = new JCheckBox("");
		help = new PropViewHelp<Bean, Boolean>(this, displayedName, displayedName);
		
		checkBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				commit();
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
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @return
	 * 		A new{@link BooleanCheckboxEditor}
	 */
	public static BooleanCheckboxEditor create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Boolean>, Boolean> displayedName) {
		return new BooleanCheckboxEditor(model, displayedName);
	}

	@Override
	public Reference<? extends Bean> getModel() {
		return model;
	}
	
	/**
	 * Get the {@link JCheckBox} used for display/editing
	 * @return
	 * 		The {@link JCheckBox}
	 */
	public JCheckBox getComponent() {
		return checkBox;
	}
	
	@Override
	public void cancel() {
		update();
	}

	@Override
	public void commit() {
		if (isEditing()) {
			help.setPropValue(checkBox.isSelected());
		}
	}

	@Override
	public boolean isEditing() {
		
		Boolean value = help.getPropValue();
		if (value == null) return false;
		
		return checkBox.isSelected() != value;
	}
	
	@Override
	public void update() {
		
		Boolean value = help.getPropValue();
		
		if (value == null) return;
		
		//First time we see a non-null value, set our name from its prop
		if (checkBox.getText().length()==0) {
			Bean bean = model.value().get();
			checkBox.setText(PropUtils.localisedName(bean, bean.features().get(displayedName)));
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
	
}
