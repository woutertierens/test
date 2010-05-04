package org.jpropeller.view.primitive.impl;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.NumberConverter;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.Views;
import org.jpropeller.view.update.UpdateManager;

/**
 *
 * JSpinner that displays a Prop<Number>
 * @param <T> Type of number.
 */
public class NumberPropSpinnerEditor<T extends Number & Comparable<T>> implements JView, org.jpropeller.properties.change.ChangeListener {

	Logger logger = GeneralUtils.logger(NumberPropSpinnerEditor.class);
	
	private final JSpinner spinner;
	private final SpinnerNumberModel numberModel;
	private final NumberConverter<T> converter;
	private final Prop<T> model;
	
	/**
	 * Original default bg colour for spinner
	 */
	private Color defaultBackground;

	private UpdateManager updateManager;

	/**
	 * Create a {@link NumberPropSpinnerEditor}
	 * @param model
	 * 		The model for this {@link View} 
	 * @param numberModel
	 * 		The number model for the spinner. Please do not use
	 * this number model except to pass it to this constructor.
	 * @param converter 
	 * 		Converter to move from {@link Number} to T and back
	 */
	public NumberPropSpinnerEditor(final Prop<T> model, 
			SpinnerNumberModel numberModel, NumberConverter<T> converter) {
		super();
		this.model = model;
		this.numberModel = numberModel;
		this.converter = converter;

		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		model.features().addListener(this);
		
		//Make a spinner that will behave properly on lost focus
		spinner = new JSpinner(numberModel);
		defaultBackground = spinner.getBackground();
		applyFocusFix(spinner);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				commit();
			}
		});
		
		//Start out up to date
		update();
	}

	@Override
	public void dispose() {
		model.features().removeListener(this);
		updateManager.deregisterUpdatable(this);
	}

	private void error(boolean errored) {
		if (errored) {
			spinner.setBackground(Views.getViewSystem().getErrorBackgroundColor());
		} else {
			spinner.setBackground(defaultBackground);
		}
	}
	
	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			try {
				model.set(converter.toT(numberModel.getNumber()));
				error(false);
			} catch (ReadOnlyException e) {
				error(true);
				update();
			} catch (InvalidValueException e) {
				error(true);
				update();
			}

		}
	}

	private boolean checkNull(T value) {
		boolean n = (value == null);
		spinner.setEnabled(!n);
		return n;
	}
	
	@Override
	public boolean isEditing() {
		T value = model.get();

		//If value is null, not editing
		if (checkNull(value)) {
			return false;
		}
		
		//We are editing if a commit now would do something
		return (!converter.toT(numberModel.getNumber()).equals(value));
	}

	//We need to use the raw Comparable provided by NumberModel
	@SuppressWarnings("unchecked")
	@Override
	public void update() {
		/*if (logger.isLoggable(Level.FINEST)) {
			logger.finest("update()");
			logger.finest("prop value " + help.getPropValue());
			logger.finest("spinner value " + numberModel.getNumber());
		}*/
		
		//If the spinner is not already showing prop value, update it
		if (isEditing()) {
			
			T value = model.get();
			
			error(false);

			//Can't display null values
			if (checkNull(value)) {
				return;
			}
			
			//Expand the range of the number model as necessary to contain the
			//new value
			if (numberModel.getMinimum() != null && numberModel.getMinimum().compareTo(value) > 0){
				numberModel.setMinimum(value);
			}
			if (numberModel.getMaximum() != null && numberModel.getMaximum().compareTo(value) < 0){
				numberModel.setMaximum(value);
			}
			
			spinner.setValue(converter.toNumber(value));
		}
	}
	
	private void applyFocusFix(final JSpinner spinner) {
		final JComponent editor = spinner.getEditor();
		
		//If the editor is a default editor, we can work around
		//failure to commit when selecting a menu (and possibly other
		//problems) by monitoring the editor, and committing its edits when it loses
		//focus. This should happen automatically, and sometimes does by some
		//means I have not yet located, but fails when immediately selecting a menu 
		//after editing, etc.
		if (editor instanceof DefaultEditor) {
			final DefaultEditor dEditor = (DefaultEditor)editor;
			dEditor.getTextField().addFocusListener(new FocusListener() {
				
				//On focus lost, try to commit any pending edit.
				//If this fails, then revert editor to the actual
				//current value to avoid confusion
				public void focusLost(FocusEvent e) {
					try {
						spinner.commitEdit();
					} catch (ParseException e1) {
						cancel();
					}
				}
				
				//Do nothing on focus gained
				public void focusGained(FocusEvent e) {
				}
			});
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

	@Override
	public JComponent getComponent() {
		return spinner;
	}

	@Override
	public void cancel() {
		//??
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		Props.getPropSystem().getUpdateManager().updateRequiredBy(this);
	}
}
