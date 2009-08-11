package org.jpropeller.view.primitive.impl;

import javax.swing.JProgressBar;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.util.NumberConverter;
import org.jpropeller.util.NumberConverterDefaults;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;

/**
 * A display-only {@link JView} for a {@link Bean}, showing
 * the value of a {@link Prop} with value of type {@link Number}
 * in the form of a {@link JProgressBar}
 * 
 * @param <T> 	The type of value in the edited {@link Prop}
 */
public class NumberProgressBarEditor<T extends Number & Comparable<T>> implements JView, UpdatableSingleValueView<Bean> {

	private final PropViewHelp<Bean, T> help;

	private final Reference<? extends Bean> model;

	private final JProgressBar bar;
	
	private final NumberConverter<T> converter;


	/**
	 * Create a {@link NumberProgressBarEditor}
	 * @param model			The {@link Reference} for this {@link View} 
	 * @param displayedName	The name of the displayed property 
	 * @param converter 	Converter to move from {@link Number} to T and back,
	 * 						also performing any required scaling of the values - 
	 * 						{@link NumberConverter#toNumber(Number)} should convert
	 * 						to the correct range of values from min to max, bearing
	 * 						in mind that the progress bar will only display integer 
	 * 						increments. So for example, it would be sensible to multiply
	 * 						a Double that ranges from 0 to 1 by 100, and use min and max
	 * 						of 0 and 100 to give a percentage display.
	 * @param min			The minimum for the {@link JProgressBar}
	 * @param max			The minimum for the {@link JProgressBar}
	 * @param stringPainted	True to make the bar paint the progress number as a string,
	 * 						false to just display the bar. May not be supported on all
	 * 						look and feels.
	 */
	public NumberProgressBarEditor(Reference<? extends Bean> model,
			PropName<T> displayedName,
			NumberConverter<T> converter,
			int min, int max, boolean stringPainted) {
		super();
		this.model = model;
		this.converter = converter;

		help = new PropViewHelp<Bean, T>(this, displayedName);

		//Make a progress bar
		bar = new JProgressBar(min, max);
		bar.setStringPainted(stringPainted);

		help.connect();
	}

	@Override
	public void dispose() {
		help.dispose();
	}

	/**
     * Constructs a default view with min 0, max 100, and
     * string painted
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @param converter 	Converter to move from {@link Number} to T and back,
	 * 						also performing any required scaling of the values - 
	 * 						{@link NumberConverter#toNumber(Number)} should convert
	 * 						to the correct range of values from min to max, bearing
	 * 						in mind that the progress bar will only display integer 
	 * 						increments. So for example, it would be sensible to multiply
	 * 						a Double that ranges from 0 to 1 by 100, and use min and max
	 * 						of 0 and 100 to give a percentage display.
	 */
	public NumberProgressBarEditor(Reference<? extends Bean> model,
			PropName<T> displayedName,
			NumberConverter<T> converter) {
		this(model, displayedName, converter, 0, 100, true);
	}
	
	@Override
    public Reference<? extends Bean> getModel() {
		return model;
	}

	/**
	 * Get the {@link JProgressBar} used for display
	 * @return
	 * 		The bar
	 */
	public JProgressBar getComponent() {
		return bar;
	}
	
	@Override
	public void cancel() {
		//Nothing to do - display only
	}

	@Override
	public void commit() {
		//Nothing to do - display only
	}

	private boolean checkNull(T value) {
		boolean n = (value == null);
		bar.setEnabled(!n);
		return n;
	}
	
	@Override
	public boolean isEditing() {
		//Display only
		return false;
	}

	@Override
	public void update() {
		
		T value = help.getPropValue();
		
		//Can't display null values
		if (checkNull(value)) {
			return;
		}

		int setting = converter.toNumber(value).intValue();
		
		if (bar.getValue() != setting) bar.setValue(setting);
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
	

	//Factory methods
	
    /**
     * Create an editor for {@link Double} values, displays range
     * of {@link Double} values from 0 to 1 as 0 to 100 on the
     * progress bar.
     * 
	 * @param model
	 * 		The model {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberProgressBarEditor<Double> createDouble(Reference<? extends Bean> model,
			PropName<Double> displayedName){
    	return new NumberProgressBarEditor<Double>(model, displayedName, 
    			new NumberConverter<Double>() {
					@Override
					public Double toT(Number n) {
						double val = n.doubleValue();
						if (val > 1) val = 1;
						if (val < 0) val = 0;
						return val;
					}
					@Override
					public Number toNumber(Double t) {
						double val = t * 100;
						if (val > 100) val = 100;
						if (val < 0) val = 0;
						return val;
					}
				});
    }
    
    /**
     * Create an editor for {@link Float} values, displays range
     * of {@link Float} values from 0 to 1 as 0 to 100 on the
     * progress bar.
     * 
	 * @param model
	 * 		The model {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberProgressBarEditor<Float> createFloat(Reference<? extends Bean> model,
			PropName<Float> displayedName){
    	return new NumberProgressBarEditor<Float>(model, displayedName, 
    			new NumberConverter<Float>() {
					@Override
					public Float toT(Number n) {
						float val = n.floatValue();
						if (val > 1) val = 1;
						if (val < 0) val = 0;
						return val;
					}
					@Override
					public Number toNumber(Float t) {
						float val = t * 100;
						if (val > 100) val = 100;
						if (val < 0) val = 0;
						return val;
					}
				});
    }
    
    /**
     * Create an editor for {@link Integer} values, displaying a range 
     * from 0 to 100
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberProgressBarEditor<Integer> createInteger(Reference<? extends Bean> model,
			PropName<Integer> displayedName){
    	return new NumberProgressBarEditor<Integer>(
    			model, displayedName,
    			NumberConverterDefaults.getIntegerConverter());
    }
    
    /**
     * Create an editor for {@link Long} values, displaying a range
     * from 0 to 100
     * 
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
     * @return
     * 		A new editor
     */
    public static NumberProgressBarEditor<Long> createLong(Reference<? extends Bean> model,
			PropName<Long> displayedName){
    	return new NumberProgressBarEditor<Long>(model, displayedName,
    					NumberConverterDefaults.getLongConverter());
    }
    
}
