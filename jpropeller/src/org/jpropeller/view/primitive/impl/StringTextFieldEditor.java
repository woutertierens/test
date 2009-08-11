package org.jpropeller.view.primitive.impl;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.Views;
import org.jpropeller.view.impl.PropViewHelp;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link Prop} with value of type {@link String}
 */
public class StringTextFieldEditor implements JView, UpdatableSingleValueView<Bean> {

	PropViewHelp<Bean, String> help;

	Reference<? extends Bean> model;
	PropName<String> displayedName;
	
	JTextField field;
	Color defaultBackground;
	
	String valueAtStartOfEditing = null;
	
	private StringTextFieldEditor(Reference<? extends Bean> model,
			PropName<String> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		buildField();
		
		help = new PropViewHelp<Bean, String>(this, displayedName);
		help.connect();
	}

	/**
	 * Create a {@link StringTextFieldEditor}
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @return
	 * 		A new {@link StringTextFieldEditor}
	 */
	public final static StringTextFieldEditor create(Reference<? extends Bean> model,
			PropName<String> displayedName) {
		return new StringTextFieldEditor(model, displayedName);
	}

	@Override
	public void dispose() {
		help.dispose();
	}

	@Override
	public Reference<? extends Bean> getModel() {
		return model;
	}

	/**
	 * Get the {@link JTextField} used for display/editing
	 * @return
	 * 		The text field
	 */
	public JTextField getComponent() {
		return field;
	}
	
	private boolean checkNull(String value) {
		boolean n = (value == null);
		if (n) {
			field.setText("");
		}
		field.setEnabled(!n);
		return n;
	}

	
	private void buildField() {
		field = new JTextField();
		defaultBackground = field.getBackground();
		field.addFocusListener(new FocusListener() {
			//On focus lost, try to commit any pending edit.
			public void focusLost(FocusEvent e) {
				commit();
			}
			public void focusGained(FocusEvent e) {
				//Always update when we get focus
				display();
				
				//Note the value we started from
				valueAtStartOfEditing = field.getText();
			}
		});
		field.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commit();
			}
		});
	}
	
	@Override
	public void cancel() {
		//Just reset the text field to the actual prop value to cancel editing
		update();
	}

	private void error(boolean errored) {
		if (errored) {
			field.setBackground(Views.getViewSystem().getErrorBackgroundColor());
		} else {
			field.setBackground(defaultBackground);
		}
	}
	
	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			try {
				help.setPropValue(field.getText());
				error(false);
				
			//Notify user of any error
			} catch (ReadOnlyException e) {
				error(true);
			} catch (InvalidValueException e) {
				error(true);
			}
		}
	}

	@Override
	public boolean isEditing() {
		String value = help.getPropValue();
		
		//Not editing if value is null
		if (checkNull(value)) return false;
		
		//We are editing if text field value is not the same as prop value
		return (!field.getText().equals(value));
	}

	@Override
	public void update() {
		//If the text field is not focussed, then always update the display
		//to the new prop value, if necessary.
		if (!field.isFocusOwner()) {
			display();
			
		//If the field is focussed, we only need to update if the new value
		//is different to the valueAtStartOfEditing
		} else {
			if (valueAtStartOfEditing != null && !valueAtStartOfEditing.equals(help.getPropValue())) {
				display();
				//Restart editing
				valueAtStartOfEditing = field.getText();
			}
		}
	}
	
	private void display() {
		//If the text field is not already showing prop value, 
		//and we are not focussed, update it
		if (isEditing()) {
			String value = help.getPropValue();
			
			//Can't display null values
			if (checkNull(value)) value = "";
			
			field.setText(value);
			
			//We now know we are not errored, since we are displaying the actual value
			error(false);
		}
	}

	@Override
	public boolean selfNaming() {
		//Just displays value
		return false;
	}
	
	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}
	
}
