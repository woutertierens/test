package org.jpropeller.view.primitive.impl;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;
import org.jpropeller.view.update.UpdatableView;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link EditableProp} with value of type {@link Boolean}
 */
public class BooleanCheckboxEditor implements UpdatableView<Bean>, JView<Bean> {

	PropViewHelp<Bean, Boolean> help;

	Reference<? extends Bean> model;
	PropName<? extends EditableProp<Boolean>, Boolean> displayedName;

	JCheckBox checkBox;
	
	private BooleanCheckboxEditor(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Boolean>, Boolean> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		
		//Make a checkbox
		checkBox = new JCheckBox();
		checkBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				commit();
			}
		});
		
		help = new PropViewHelp<Bean, Boolean>(this, displayedName, displayedName);
		help.connect();
	}

	@Override
	public void disposeNow() {
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
		updateNow();
	}

	@Override
	public void commit() {
		if (isEditing()) {
			help.setPropValue(checkBox.isSelected());
		}
	}

	@Override
	public boolean isEditing() {
		return checkBox.isSelected() != help.getPropValue();	
	}
	
	@Override
	public void updateNow() {
		if (isEditing()) {
			checkBox.setSelected(help.getPropValue());
		}
	}
	
}
