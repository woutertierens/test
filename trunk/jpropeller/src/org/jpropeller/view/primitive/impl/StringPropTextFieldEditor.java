package org.jpropeller.view.primitive.impl;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.Views;
import org.jpropeller.view.update.UpdateManager;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link Prop} with value of type {@link String}
 */
public class StringPropTextFieldEditor implements JView, ChangeListener {

	Prop<String> model;

	Prop<Boolean> locked;
	
	JTextComponent text;
	JComponent component;
	
	Color defaultBackground;
	
	String valueAtStartOfEditing = null;
	
	private final boolean multiline;

	private UpdateManager updateManager;

	private StringPropTextFieldEditor(Prop<String> model,
			boolean multiline, Prop<Boolean> locked) {

		super();
		this.model = model;
		this.locked = locked;
		this.multiline = multiline;
		buildField();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		model.features().addListener(this);
		if (locked != null) {
			locked.features().addListener(this);
		}

		//Start out up to date
		update();
	}

	/**
	 * Create a {@link StringPropTextFieldEditor}
	 * @param model		The {@link Prop} to display 
	 * @return			A new {@link StringPropTextFieldEditor}
	 */
	public final static StringPropTextFieldEditor create(Prop<String> model) {
		return create(model, false);
	}

	/**
	 * Create a {@link StringPropTextFieldEditor}
	 * @param model		The {@link Prop} to display 
	 * @param multiline	True if the view should support multiline editing - if so,
	 * 					it will not commit on pressing enter, only on losing focus.
	 * @return			A new {@link StringPropTextFieldEditor}
	 */
	public final static StringPropTextFieldEditor create(Prop<String> model, boolean multiline) {
		return new StringPropTextFieldEditor(model, multiline, null);
	}
	
	/**
	 * Create a {@link StringPropTextFieldEditor}
	 * @param model		The {@link Prop} to display 
	 * @param multiline	True if the view should support multiline editing - if so,
	 * 					it will not commit on pressing enter, only on losing focus.
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
	 * @return
	 * 		A new {@link StringPropTextFieldEditor}
	 */
	public final static StringPropTextFieldEditor create(Prop<String> model,
			boolean multiline, Prop<Boolean> locked) {
		return new StringPropTextFieldEditor(model, multiline, locked);
	}
	
	/**
	 * Create a single line {@link StringPropTextFieldEditor}
	 * @param model		The {@link Prop} to display 
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
	 * @return
	 * 		A new {@link StringPropTextFieldEditor}
	 */
	public final static StringPropTextFieldEditor create(Prop<String> model,
			Prop<Boolean> locked) {
		return new StringPropTextFieldEditor(model, false, locked);
	}

	@Override
	public void dispose() {
		model.features().removeListener(this);
		if (locked != null) {
			locked.features().removeListener(this);
		}
		updateManager.deregisterUpdatable(this);
	}

	/**
	 * Get the {@link JComponent} used for display/editing
	 * @return
	 * 		The text field
	 */
	public JComponent getComponent() {
		return component;
	}
	
	private void buildField() {
		if (multiline) {
			JTextArea jTextArea = new JTextArea(10, 20);
			jTextArea.setLineWrap(true);
			jTextArea.setWrapStyleWord(true);
			text = jTextArea;
			
			component = new JScrollPane(text);
		} else {
			JTextField field = new JTextField();
			field.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					commit();
				}
			});
			text = field;
			component = text;
		}
		
		defaultBackground = text.getBackground();
		text.addFocusListener(new FocusListener() {
			//On focus lost, try to commit any pending edit.
			public void focusLost(FocusEvent e) {
				commit();
			}
			public void focusGained(FocusEvent e) {
				//Always update when we get focus
				display();
				
				//Note the value we started from
				valueAtStartOfEditing = text.getText();
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
			text.setBackground(Views.getViewSystem().getErrorBackgroundColor());
		} else {
			text.setBackground(defaultBackground);
		}
	}
	
	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			
			//If we are locked, just revert
			if (Props.isTrue(locked)) {
				update();
				return;
			}
			
			try {
				model.set(text.getText());
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
		String value = model.get();
		
		//Not editing if value is null
		if (value==null) return false;
		
		//We are editing if text field value is not the same as prop value
		return (!text.getText().equals(value));
	}

	@Override
	public void update() {
		
		String value = model.get();
		text.setEnabled(value!=null && !Props.isTrue(locked));
		
		//If the text field is not focused, or value is locked, then always update the display
		//to the new prop value, if necessary.
		if (!text.isFocusOwner() || Props.isTrue(locked)) {
			display();
			
		//If the field is focused, we only need to update if the new value
		//is different to the valueAtStartOfEditing
		} else {
			if (valueAtStartOfEditing != null && !valueAtStartOfEditing.equals(model.get())) {
				display();
				//Restart editing
				valueAtStartOfEditing = text.getText();
			}
		}
	}
	
	private void display() {
		String value = model.get();
		
		//Always try to clear on null
		if (value == null) {

			if (!text.getText().equals("")) {
				text.setText("");
			}
			error(false);
		
		//If the text field is not already showing prop value, 
		//and we are not focused, update it
		} else if (isEditing()) {
			text.setText(value);
			
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
		return multiline ? Format.MEDIUM : Format.SINGLE_LINE;
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		Props.getPropSystem().getUpdateManager().updateRequiredBy(this);
	}
	
}
