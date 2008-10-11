package org.jpropeller.view.primitive.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;
import org.jpropeller.view.proxy.ViewProxy;
import org.jpropeller.view.update.UpdatableView;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link EditableProp} with value of type {@link String}
 */
public class StringTextFieldEditor implements UpdatableView<Bean>, JView<Bean> {

	PropViewHelp<Bean, String> help;

	ViewProxy<? extends Bean> proxy;
	PropName<? extends EditableProp<String>, String> displayedName;
	
	JTextField field;
	
	private StringTextFieldEditor(ViewProxy<? extends Bean> proxy,
			PropName<? extends EditableProp<String>, String> displayedName) {
		super();
		this.proxy = proxy;
		this.displayedName = displayedName;
		buildField();
		
		help = new PropViewHelp<Bean, String>(this, displayedName, displayedName);
		help.connect();
	}

	/**
	 * Create a {@link StringTextFieldEditor}
	 * @param proxy
	 * 		The {@link ViewProxy} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @return
	 * 		A new {@link StringTextFieldEditor}
	 */
	public final static StringTextFieldEditor create(ViewProxy<? extends Bean> proxy,
			PropName<? extends EditableProp<String>, String> displayedName) {
		return new StringTextFieldEditor(proxy, displayedName);
	}

	@Override
	public void dispose() {
		help.dispose();
	}

	@Override
	public ViewProxy<? extends Bean> getProxy() {
		return proxy;
	}

	/**
	 * Get the {@link JTextField} used for display/editing
	 * @return
	 * 		The text field
	 */
	public JTextField getComponent() {
		return field;
	}
	
	private void buildField() {
		field = new JTextField();
		field.addFocusListener(new FocusListener() {
			//On focus lost, try to commit any pending edit.
			public void focusLost(FocusEvent e) {
				commit();
			}
			public void focusGained(FocusEvent e) {
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

	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			help.setPropValue(field.getText());
		}
	}

	@Override
	public boolean isEditing() {
		//We are editing if text field value is not the same as prop value
		return (!field.getText().equals(help.getPropValue()));
	}

	@Override
	public void update() {
		//If the text field is not already showing prop value, update it
		if (isEditing()) {
			field.setText(help.getPropValue());
		}
	}
	
}
