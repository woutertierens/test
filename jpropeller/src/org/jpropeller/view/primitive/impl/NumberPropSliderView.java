package org.jpropeller.view.primitive.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.system.Props;
import org.jpropeller.transformer.BiTransformer;
import org.jpropeller.transformer.impl.IdentityBiTransformer;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link View} using a {@link JSlider} to display a 
 * {@link Prop}'s {@link Integer} value.
 */
public class NumberPropSliderView implements JView, org.jpropeller.properties.change.ChangeListener {

	Logger logger = GeneralUtils.logger(NumberPropSliderView.class);
	
	private final JSlider slider;
	private final Prop<Integer> model;
	private final Prop<Boolean> locked;
	
	private final BiTransformer<Integer, Integer> displayTransformer;

	private UpdateManager updateManager;

	
	/**
	 * Create a {@link NumberPropSliderView}
	 * @param model					The model for this {@link View} 
	 * @param locked				While this {@link Prop} is true, editor 
	 * 								will not make changes to model
	 * @param displayTransformer	The transformer used for display. The FORWARDS
	 * 								transform, {@link BiTransformer#transform(Object)} is
	 * 								used to convert an actual value to a displayed value,
	 * 								and the REVERSE transform, {@link BiTransformer#transformBack(Object)}
	 * 								is used to convert a displayed value back to an actual value.
	 * 								This is probably most useful for converting between good, honest
	 * 								0-based index values and the horror of 1-based indices for the user.
	 * @param min					Minimum (model) value for slider 
	 * @param max 					Maximum (model) value for slider
	 */
	public NumberPropSliderView(final Prop<Integer> model, Prop<Boolean> locked,
			BiTransformer<Integer, Integer> displayTransformer, int min, int max) {

		super();
		this.model = model;
		if (displayTransformer != null) {
			this.displayTransformer = displayTransformer;
		} else {
			this.displayTransformer = new IdentityBiTransformer<Integer>();
		}

		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		model.features().addListener(this);
		
		this.locked = locked;
		if (locked != null) {
			locked.features().addListener(this);
		}

		slider = new JSlider(this.displayTransformer.transform(min), this.displayTransformer.transform(max), this.displayTransformer.transform(min));

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
//				if (!slider.getValueIsAdjusting()) {
					commit();
//				}
			}
		});
		
		//Start out up to date
		update();
	}

	@Override
	public void dispose() {
		model.features().removeListener(this);
		if (locked != null) {
			locked.features().removeListener(this);
		}
		updateManager.deregisterUpdatable(this);
	}
	
	private Integer currentSliderValueAsModelValue() {
		return displayTransformer.transformBack(slider.getValue());
	}
	
	@Override
	public void commit() {
		//If we are editing, set the new prop value
		if (isEditing()) {
			try {
				model.set(currentSliderValueAsModelValue());
			} catch (ReadOnlyException e) {
				update();
			} catch (InvalidValueException e) {
				update();
			}
		}
	}

	private boolean checkNull(Integer value) {
		boolean n = (value == null);
		if (slider.isEnabled() != (!n)) {
			slider.setEnabled(!n);
		}
		return n;
	}
	
	@Override
	public boolean isEditing() {
		Integer value = model.get();

		//If value is null, not editing
		if (checkNull(value)) {
			return false;
		}
		
		//We are editing if a commit now would do something
		return (!currentSliderValueAsModelValue().equals(value));
	}

	@Override
	public void update() {
		
		//If the spinner is not already showing prop value, update it
		if (isEditing()) {
			
			Integer value = model.get();
			
			//Can't display null values
			if (checkNull(value)) {
				return;
			}
			
			//Convert the value to what we will actually display in the
			//spinner
			Integer displayValue = displayTransformer.transform(value);
			
			//Expand the range of the number model as necessary to contain the
			//new value
			BoundedRangeModel boundedRangeModel = slider.getModel();
			if (boundedRangeModel.getMinimum() > displayValue){
				boundedRangeModel.setMinimum(displayValue);
			}
			if (boundedRangeModel.getMaximum()< displayValue){
				boundedRangeModel.setMaximum(displayValue);
			}
			
			if (slider.getValue() != displayValue) {
				slider.setValue(displayValue);
			}
		}
		
		boolean enabled = !Props.isTrue(locked);
		if (slider.isEnabled() != enabled) {
			slider.setEnabled(enabled);
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
	public JSlider getComponent() {
		return slider;
	}

	@Override
	public void cancel() {
		//Instant editing
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		Props.getPropSystem().getUpdateManager().updateRequiredBy(this);
	}
}
