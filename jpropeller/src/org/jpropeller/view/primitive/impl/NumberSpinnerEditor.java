package org.jpropeller.view.primitive.impl;

import java.awt.Color;
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
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.reference.Reference;
import org.jpropeller.transformer.BiTransformer;
import org.jpropeller.transformer.impl.IdentityBiTransformer;
import org.jpropeller.transformer.impl.ZeroToOneBaseTransformer;
import org.jpropeller.transformer.impl.ZeroToOneBaseTransformerLong;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.NumberConverter;
import org.jpropeller.util.NumberConverterDefaults;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.Views;
import org.jpropeller.view.impl.PropViewHelp;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link Prop} with value of type {@link Number}
 * 
 * @param <T> 
 * 		The type of value in the edited {@link Prop}
 */
public class NumberSpinnerEditor<T extends Number & Comparable<T>> implements JView, UpdatableSingleValueView<Bean> {

	Logger logger = GeneralUtils.logger(NumberSpinnerEditor.class);
	
	private final PropViewHelp<Bean, T> help;

	private final Reference<? extends Bean> model;

	private final JSpinner spinner;
	private final SpinnerNumberModel numberModel;
	private final NumberConverter<T> converter;
	
	private final BiTransformer<T, T> displayTransformer;

	/**
	 * Original default bg color for spinner
	 */
	private Color defaultBackground;

	private boolean changingSelf = false;

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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.	 
	 */
	private NumberSpinnerEditor(Reference<? extends Bean> model,
			PropName<T> displayedName,
			SpinnerNumberModel numberModel, NumberConverter<T> converter, 
			Prop<Boolean> locked) {
		this(model, displayedName, numberModel, converter, locked, null);
	}
	
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.	 
	 * @param displayTransformer	The transformer used for display. The FORWARDS
	 * 								transform, {@link BiTransformer#transform(Object)} is
	 * 								used to convert an actual value to a displayed value,
	 * 								and the REVERSE transform, {@link BiTransformer#transformBack(Object)}
	 * 								is used to convert a displayed value back to an actual value.
	 * 								This is probably most useful for converting between good, honest
	 * 								0-based index values and the horror of 1-based indices for the user.
	 */
	private NumberSpinnerEditor(Reference<? extends Bean> model,
			PropName<T> displayedName,
			SpinnerNumberModel numberModel, NumberConverter<T> converter, 
			Prop<Boolean> locked,
			BiTransformer<T, T> displayTransformer) {
		super();
		this.model = model;
		this.numberModel = numberModel;
		this.converter = converter;
		if (displayTransformer != null) {
			this.displayTransformer = displayTransformer;
		} else {
			this.displayTransformer = new IdentityBiTransformer<T>();
		}

		help = new PropViewHelp<Bean, T>(this, displayedName, locked);

		//Make a spinner that will behave properly on lost focus
		spinner = new JSpinner(numberModel);
		defaultBackground = spinner.getBackground();
		applyFocusFix(spinner);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//Ignore changes we make to our own state
				if (!changingSelf) {
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
	private NumberSpinnerEditor(Reference<? extends Bean> model,
			PropName<T> displayedName,
			NumberConverter<T> converter) {
		this(model, displayedName, new SpinnerNumberModel(), converter, null);
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

	private void error(boolean errored) {
		if (errored) {
			spinner.setBackground(Views.getViewSystem().getErrorBackgroundColor());
		} else {
			spinner.setBackground(defaultBackground);
		}
	}
	
	//Take the actual number in the spinner, and convert it to T. Then we
	//transform this T value BACK from display to model.
	private T currentSpinnerValueAsModelValue() {
		return displayTransformer.transformBack(converter.toT(numberModel.getNumber()));
	}
	
	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			try {
				help.setPropValue(currentSpinnerValueAsModelValue());
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

	
	
	@Override
	public boolean isEditing() {
		T value = help.getPropValue();

		//If value is null, not editing
		if (value==null) {
			return false;
		}
		
		//We are editing if a commit now would do something
		return (!currentSpinnerValueAsModelValue().equals(value));
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
		
		//Update enabled state
		T value = help.getPropValue();
		spinner.setEnabled(value != null && !help.isLocked());
		
		//If the spinner is not already showing prop value, update it
		if (isEditing()) {
			
			error(false);

			//Can't display null values
			if (value == null) {
				return;
			}
			
			//Convert the value to what we will actually display in the
			//spinner
			T displayValue = displayTransformer.transform(value);
			
			changingSelf  = true;
			try {
				//Expand the range of the number model as necessary to contain the
				//new value
				if (numberModel.getMinimum() != null && numberModel.getMinimum().compareTo(displayValue) > 0){
					numberModel.setMinimum(displayValue);
				}
				if (numberModel.getMaximum() != null && numberModel.getMaximum().compareTo(displayValue) < 0){
					numberModel.setMaximum(displayValue);
				}
			} finally {
				changingSelf = false;
			}
			
			spinner.setValue(converter.toNumber(displayValue));
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Double> create(Reference<? extends Bean> model,
			PropName<Double> displayedName, double min, double max, double step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Double>(model, displayedName, 
    			new SpinnerNumberModel(
    					new Double(min), new Double(min), new Double(max), new Double(step)), 
    			NumberConverterDefaults.getDoubleConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Float> create(Reference<? extends Bean> model,
			PropName<Float> displayedName, float min, float max, float step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Float>(model, displayedName, 
    			new SpinnerNumberModel(
    					new Float(min), new Float(min), new Float(max), new Float(step)), 
    					NumberConverterDefaults.getFloatConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> create(Reference<? extends Bean> model,
			PropName<Integer> displayedName, int min, int max, int step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Integer>(
    			model, displayedName,
    			new SpinnerNumberModel(
    					new Integer(min), new Integer(min), new Integer(max), new Integer(step)), 
    			NumberConverterDefaults.getIntegerConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> create(Reference<? extends Bean> model,
			PropName<Long> displayedName, long min, long max, long step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Long>(model, displayedName,
    			new SpinnerNumberModel(
    					new Long(min),  new Long(min), new Long(max), new Long(step)), 
    					NumberConverterDefaults.getLongConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Double> create(Reference<? extends Bean> model,
			PropName<Double> displayedName, double step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Double>(model, displayedName,
    			new SpinnerNumberModel(
    					new Double(1), null, null, new Double(step)), 
    			NumberConverterDefaults.getDoubleConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Float> create(Reference<? extends Bean> model,
			PropName<Float> displayedName, float step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Float>(model, displayedName, 
    			new SpinnerNumberModel(
    					new Float(1), null, null, new Float(step)), 
    					NumberConverterDefaults.getFloatConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> create(Reference<? extends Bean> model,
			PropName<Integer> displayedName, int step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Integer>(model, displayedName,
    			new SpinnerNumberModel(
    					new Integer(1), null, null, new Integer(step)), 
    			NumberConverterDefaults.getIntegerConverter(), locked);
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit,
     * displaying numbers as 1 greater than their actual model value, so that
     * a 0-based index will be displayed as a 1-based index.
     * 
	 * @param model				The {@link Reference} for this {@link View} 
	 * @param displayedName 	The name of the displayed property 
     * @param step				Step size for spinner
	 * @param locked			If this is non-null, the view will not support
	 * 							editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> createOneBasedDisplayInteger(Reference<? extends Bean> model,
			PropName<Integer> displayedName, int step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Integer>(model, displayedName,
    			new SpinnerNumberModel(
    					new Integer(1), null, null, new Integer(step)), 
    			NumberConverterDefaults.getIntegerConverter(), locked,
    			new ZeroToOneBaseTransformer());
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> create(Reference<? extends Bean> model,
			PropName<Long> displayedName, long step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Long>(model, displayedName,
    			new SpinnerNumberModel(
    					new Long(1), null, null, new Long(step)), 
    					NumberConverterDefaults.getLongConverter(), locked);
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
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> createOneBasedDisplayLong(Reference<? extends Bean> model,
			PropName<Long> displayedName, long step, Prop<Boolean> locked){
    	return new NumberSpinnerEditor<Long>(model, displayedName,
    			new SpinnerNumberModel(
    					new Long(1), null, null, new Long(step)), 
    					NumberConverterDefaults.getLongConverter(), locked,
    					new ZeroToOneBaseTransformerLong());
    }
    
    /**
     * Create an editor for {@link Double} values, with no min/max limit
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * and step 0.1
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Double> createDouble(Reference<? extends Bean> model,
			PropName<Double> displayedName, Prop<Boolean> locked){
    	return create(model, displayedName, 0.1d, locked);
    }
    
    /**
     * Create an editor for {@link Float} values, with no min/max limit
     * and step 0.1
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Float> createFloat(Reference<? extends Bean> model,
			PropName<Float> displayedName, Prop<Boolean> locked){
    	return create(model, displayedName, 0.1f, locked);
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit
     * and step 1
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Integer> createInteger(Reference<? extends Bean> model,
			PropName<Integer> displayedName, Prop<Boolean> locked){
    	return create(model, displayedName, 1, locked);
    }
    
    /**
     * Create an editor for {@link Integer} values, with no min/max limit
     * and step 1
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
     * @return
     * 		A new editor
     */
    public static NumberSpinnerEditor<Long> createLong(Reference<? extends Bean> model,
			PropName<Long> displayedName, Prop<Boolean> locked){
    	return create(model, displayedName, 1l, locked);
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
