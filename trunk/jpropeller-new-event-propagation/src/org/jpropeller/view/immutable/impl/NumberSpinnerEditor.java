package org.jpropeller.view.immutable.impl;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.NumberConverter;
import org.jpropeller.util.NumberConverterDefaults;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;
import org.jpropeller.view.update.UpdatableView;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link EditableProp} with value of type {@link Number}
 * 
 * @param <T> 
 * 		The type of value in the edited {@link Prop}
 */
public class NumberSpinnerEditor<T extends Number & Comparable<T>> implements UpdatableView<Bean>, JView<Bean> {

	Logger logger = GeneralUtils.logger(NumberSpinnerEditor.class);
	
	PropViewHelp<Bean, T> help;

	Reference<? extends Bean> model;
	PropName<? extends EditableProp<T>, T> displayedName;

	JSpinner spinner;
	SpinnerNumberModel numberModel;
	NumberConverter<T> converter;

	/**
	 * Create a {@link NumberSpinnerEditor}
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @param numberModel
	 * 		The number model for the spinner. Please do not use
	 * this number model except to pass it to this constructor.
	 * @param converter 
	 * 		Converter to move from {@link Number} to T and back
	 */
	public NumberSpinnerEditor(Reference<? extends Bean> model,
			PropName<? extends EditableProp<T>, T> displayedName,
			SpinnerNumberModel numberModel, NumberConverter<T> converter) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		this.numberModel = numberModel;
		this.converter = converter;

		help = new PropViewHelp<Bean, T>(this, displayedName, displayedName);

		//Make a spinner that will behave properly on lost focus
		spinner = new JSpinner(numberModel);
		applyFocusFix(spinner);
		spinner.addChangeListener(new ChangeListener() {
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
     * Constructs an editor using a {@link SpinnerNumberModel} with no
     * <code>minimum</code> or <code>maximum</code> value, 
     * <code>stepSize</code> equal to one, and an initial value of zero.
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @param converter 
	 * 		Converter to move from {@link Number} to T and back
	 */
	public NumberSpinnerEditor(Reference<? extends Bean> model,
			PropName<? extends EditableProp<T>, T> displayedName,
			NumberConverter<T> converter) {
		this(model, displayedName, new SpinnerNumberModel(), converter);
	}
	
	@Override
    public Reference<? extends Bean> getModel() {
		return model;
	}

	/**
	 * Get the {@link JSpinner} used for display/editing
	 * @return
	 * 		The spinner
	 */
	public JSpinner getComponent() {
		return spinner;
	}
	
	@Override
	public void cancel() {
		//Just reset the spinner to the actual prop value to cancel editing
		update();
	}

	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			help.setPropValue(converter.toT(numberModel.getNumber()));
		}
	}

	@Override
	public boolean isEditing() {
		T value = help.getPropValue();
		if (value == null) return false;
		
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
			
			T value = help.getPropValue();
			
			//Can't display null values
			if (value == null) return;
			
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
	
	//Factory methods
	
    /**
     * Create an editor for {@link Double} values
     * 
	 * @param model
	 * 		The model {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param min
     * 		Minimum value for spinner
     * @param max
     * 		Maximum value for spinner
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Double> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Double>, Double> displayedName, double min, double max, double step){
    	return new NumberSpinnerEditor<Double>(model, displayedName, 
    			new SpinnerNumberModel(
    					new Double(min), new Double(min), new Double(max), new Double(step)), 
    			NumberConverterDefaults.getDoubleConverter());
    }
    
    /**
     * Create an editor for {@link Float} values
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param min
     * 		Minimum value for spinner
     * @param max
     * 		Maximum value for spinner
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Float> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Float>, Float> displayedName, float min, float max, float step){
    	return new NumberSpinnerEditor<Float>(model, displayedName, 
    			new SpinnerNumberModel(
    					new Float(min), new Float(min), new Float(max), new Float(step)), 
    					NumberConverterDefaults.getFloatConverter());
    }
    
    /**
     * Create an editor for {@link Integer} values
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param min
     * 		Minimum value for spinner
     * @param max
     * 		Maximum value for spinner
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Integer>, Integer> displayedName, int min, int max, int step){
    	return new NumberSpinnerEditor<Integer>(
    			model, displayedName,
    			new SpinnerNumberModel(
    					new Integer(min), new Integer(min), new Integer(max), new Integer(step)), 
    			NumberConverterDefaults.getIntegerConverter());
    }
    
    /**
     * Create an editor for {@link Integer} values
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param min
     * 		Minimum value for spinner
     * @param max
     * 		Maximum value for spinner
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Long>, Long> displayedName, long min, long max, long step){
    	return new NumberSpinnerEditor<Long>(model, displayedName,
    			new SpinnerNumberModel(
    					new Long(min),  new Long(min), new Long(max), new Long(step)), 
    					NumberConverterDefaults.getLongConverter());
    }
    

    /**
     * Create an editor for {@link Double} values, with no min/max limit
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Double> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Double>, Double> displayedName, double step){
    	return new NumberSpinnerEditor<Double>(model, displayedName,
    			new SpinnerNumberModel(
    					new Double(1), null, null, new Double(step)), 
    			NumberConverterDefaults.getDoubleConverter());
    }
    
    /**
     * Create an editor for {@link Float} values, with no min/max limit
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Float> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Float>, Float> displayedName, float step){
    	return new NumberSpinnerEditor<Float>(model, displayedName, 
    			new SpinnerNumberModel(
    					new Float(1), null, null, new Float(step)), 
    					NumberConverterDefaults.getFloatConverter());
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Integer>, Integer> displayedName, int step){
    	return new NumberSpinnerEditor<Integer>(model, displayedName,
    			new SpinnerNumberModel(
    					new Integer(1), null, null, new Integer(step)), 
    			NumberConverterDefaults.getIntegerConverter());
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @param step
     * 		Step size for spinner
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> create(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Long>, Long> displayedName, long step){
    	return new NumberSpinnerEditor<Long>(model, displayedName,
    			new SpinnerNumberModel(
    					new Long(1), null, null, new Long(step)), 
    					NumberConverterDefaults.getLongConverter());
    }
    
    /**
     * Create an editor for {@link Double} values, with no min/max limit
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * and step 0.1
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Double> createDouble(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Double>, Double> displayedName){
    	return create(model, displayedName, 0.1d);
    }
    
    /**
     * Create an editor for {@link Float} values, with no min/max limit
     * and step 0.1
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Float> createFloat(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Float>, Float> displayedName){
    	return create(model, displayedName, 0.1f);
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit
     * and step 1
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> createInteger(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Integer>, Integer> displayedName){
    	return create(model, displayedName, 1);
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit
     * and step 1
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> createLong(Reference<? extends Bean> model,
			PropName<? extends EditableProp<Long>, Long> displayedName){
    	return create(model, displayedName, 1l);
    }

	@Override
	public boolean selfNaming() {
		//Just displays value
		return false;
	}
}
