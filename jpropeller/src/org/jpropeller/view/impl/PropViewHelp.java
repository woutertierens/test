package org.jpropeller.view.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;


/**
 * Helper class to handle viewing a named {@link Prop}
 * of {@link Bean} values. This helps in the implementation
 * of an {@link UpdatableView}, where the view must display
 * only contents of the {@link Bean} in its model value.
 * @param <M>
 * 		The type of model {@link Bean} for the helped view 
 * @param <T> 
 * 		The type of value in the displayed prop
 */
public class PropViewHelp<M extends Bean, T> implements ChangeListener {

	private final static Logger logger = GeneralUtils.logger(PropViewHelp.class);
	
	private PropName<T> name;
	private UpdatableSingleValueView<M> view;
	private Reference<? extends M> model;
	private UpdateManager updateManager;
	private Prop<T> viewedProp = null;
	private final Prop<Boolean> locked;
	
	//Track whether we have been disposed
	private boolean disposed = false;
	
	private ChangeListener lockedListener;
	
	/**
	 * Make a {@link PropViewHelp}
	 * 
	 * @param view		The view to be helped
	 * @param name		The name of the property displayed by the view
	 * @param locked	If this is non null, editing will not be possible when
	 * 					it contains value true.
	 */
	public PropViewHelp(final UpdatableSingleValueView<M> view, PropName<T> name, Prop<Boolean> locked) {
		
		this.view = view;
		this.name = name;
		
		this.model = view.getModel();
		this.locked = locked;
		
		updateManager = Props.getPropSystem().getUpdateManager();
	}

	/**
	 * This will connect the {@link UpdatableView} to the update manager, and to its own model.
	 * The {@link UpdatableView} must be otherwise fully constructed, and have its
	 * model set.
	 * 
	 * This makes sure that:
	 * The view is registered with the update manager.
	 * When the model has a prop change, the update manager is informed that the view
	 * requires an update.
	 * An first request is made for an update to display the initial value
	 *
	 * Remember to {@link #dispose()} when the view is no longer in use
	 */
	public void connect() {
		updateManager.registerUpdatable(view);

		//When model value or locked prop has a change, we require an update
		model.value().features().addListener(this);
		
		
		if (locked != null) {
			//A change to locked prop always involves an update
			lockedListener = new ChangeListener() {
				@Override
				public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
					updateManager.updateRequiredBy(view);
				}
			};
			locked.features().addListener(lockedListener);
		}
		
		//Initial update
		updateManager.updateRequiredBy(view);
	}
	
	/**
	 * Make a {@link PropViewHelp}
	 * 
	 * This will connect the {@link UpdatableView} to the update manager, and to its own model.
	 * The {@link UpdatableView} must be otherwise fully constructed, and have its
	 * model set.
	 * 
	 * This makes sure that:
	 * The view is registered with the update manager.
	 * When the model has a prop change, the update manager is informed that the view
	 * requires an update.
	 * An first request is made for an update to display the initial value
	 *
	 * Remember to {@link #dispose()} when the view is no longer in use
	 * 
	 * @param <M>
	 * 		The type of model {@link Bean} for the helped view 
	 * @param <T> 
	 * 		The type of value in the displayed prop
	 * 
	 * @param view
	 * 		The view to be helped
	 * @param name
	 * 		The name of the property displayed by the view
	 * @return 
	 * 		A new {@link PropViewHelp}
	 */
	public static <M extends Bean, T> PropViewHelp<M, T> create(UpdatableSingleValueView<M> view, PropName<T> name) {
		return new PropViewHelp<M, T>(view, name, null);
	}
	
	/**
	 * Make a {@link PropViewHelp}
	 * 
	 * This will connect the {@link UpdatableView} to the update manager, and to its own model.
	 * The {@link UpdatableView} must be otherwise fully constructed, and have its
	 * model set.
	 * 
	 * This makes sure that:
	 * The view is registered with the update manager.
	 * When the model has a prop change, the update manager is informed that the view
	 * requires an update.
	 * An first request is made for an update to display the initial value
	 *
	 * Remember to {@link #dispose()} when the view is no longer in use
	 * 
	 * @param <M>
	 * 		The type of model {@link Bean} for the helped view 
	 * @param <T> 
	 * 		The type of value in the displayed prop
	 * 
	 * @param view
	 * 		The view to be helped
	 * @param name
	 * 		The name of the property displayed by the view
	 * @param locked	If this is not null, then editing will be disabled when 
	 * 					the {@link Prop} value is true
	 * @return 
	 * 		A new {@link PropViewHelp}
	 */
	public static <M extends Bean, T> PropViewHelp<M, T> create(UpdatableSingleValueView<M> view, PropName<T> name, Prop<Boolean> locked) {
		return new PropViewHelp<M, T>(view, name, locked);
	}
	
	/**
	 * Check whether the view is locked
	 * @return	True if view is locked (and so should not edit), false otherwise
	 */
	public boolean isLocked() {
		return Props.isTrue(locked);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		
		//OPTIMISE seems like there must be a neater way to do this now we can listen
		//directly to props, but actually there may not be without having to
		//create a pathprop that always points at the right value, since we
		//want to retain the ability of one View to display multiple beans without
		//being recreated. In any case this code is not too complex
		
		//OPTIMISATION, around 25% - the alternative to the method implementation
		//below is just to always call updateManager.updateRequiredBy(view);
		//This optimisation will break if any events have incorrect deep/shallow status
		
		//See whether the value of the model contains a new instance. If it does,
		//we will have a new Prop to look at, otherwise we are just watching the
		//old one still
		boolean modelValueInstanceChanged = !changes.get(model.value()).sameInstances();
		
		//If we have a new bean value in the model, we have a new viewed prop,
		//so we need to get it.
		//Also do this if we have a null viewedProp, for example on first change.
		if (modelValueInstanceChanged || viewedProp == null) {
			//Get the model bean
			M currentModel = model.value().get();
			
			//If model bean is null, prop is null
			if (currentModel == null) {
				viewedProp = null;
			//Otherwise get the viewed prop
			} else {
				viewedProp = currentModel.features().get(name); 
			}
			
			//We definitely need an update, since we have a completely new viewed prop
			updateManager.updateRequiredBy(view);
			
		//We do not have a new viewedProp instance, so 
		//we only need to update if the old viewed prop 
		//was affected by the event
		} else if (changes.containsKey(viewedProp)) {
			updateManager.updateRequiredBy(view);
		}
		
		//This is the unoptimised version of this method
		//updateManager.updateRequiredBy(view);
	}
	
	/**
	 * Dispose of the view, disconnecting it from the update manager 
	 * and events, so it can be garbage collected, etc.
	 */
	public void dispose() {
		if (disposed) {
			logger.fine("PropViewHelp already disposed.");
		}
		disposed = true;
		updateManager.deregisterUpdatable(view);
		model.value().features().removeListener(this);
		
		if (locked != null) {
			locked.features().removeListener(lockedListener);
		}
	}
	
	/**
	 * Look up the value displayed by a prop view, which is the value
	 * of the named prop of its displayed bean
	 * @return
	 * 		The value of the named property, or null if the model itself is null
	 */
	public  T getPropValue() {
		//Get the model's value - the bean we are viewing
		M currentValue = model.value().get();
		
		//If value is null, return null
		if (currentValue == null) {
			return null;
		//Otherwise return the value of the named prop
		} else {
			Prop<T> prop = currentValue.features().get(name);
			
			//We may not have a prop with given name
			if (prop == null) {
				logger.fine("Can't find named property to get: " + name);
				return null;
			} else {
				return prop.get();
			}
		}
	}
	
	/**
	 * Set the value displayed by a prop view, which is the value
	 * of the named prop of its displayed bean. This does nothing
	 * if the model is currently null.
	 * @param value						The value to set in the property
	 * @throws ReadOnlyException 		If property is read only
	 * @throws InvalidValueException 	If value is invalid
	 */
	public void setPropValue(T value) throws ReadOnlyException, InvalidValueException {
		
		if (disposed) {
			logger.severe("setPropValue called after PropViewHelp disposed");
		}

		//If locked, do nothing
		if (Props.isTrue(locked)) {
			return;
		}
		
		//Get the model's value - the bean we are viewing
		M currentValue = model.value().get();
		
		//If value is null, do nothing
		if (currentValue == null) {
			return ;
		//Otherwise set the value of the named prop
		} else {
			Prop<T> prop = currentValue.features().get(name);
			//We may not have a prop with given name
			if (prop != null) {
				prop.set(value);
			} else {
				logger.fine("Can't find named property to set: " + name);
			}
		}
	}
	
}
