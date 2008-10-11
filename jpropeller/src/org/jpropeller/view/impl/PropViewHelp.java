package org.jpropeller.view.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.system.Props;
import org.jpropeller.view.proxy.ViewProxy;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;


/**
 * Helper class to handle registering 
 * @param <M>
 * 		The type of model {@link Bean} for the helped view 
 * @param <T> 
 * 		The type of value in the displayed prop
 */
public class PropViewHelp<M extends Bean, T> implements PropListener {

	private PropName<? extends Prop<T>, T> name;
	private PropName<? extends EditableProp<T>, T> editableName;
	private UpdatableView<M> view;
	private ViewProxy<? extends M> proxy;
	private UpdateManager updateManager;
	private Prop<T> viewedProp = null;
	
	/**
	 * Make a {@link PropViewHelp}
	 * 
	 * @param view
	 * 		The view to be helped
	 * @param name
	 * 		The name of the property displayed by the view
	 * @param editableName
	 * 		The name of the property displayed by the view, as an editable name if
	 * possible. If the prop is not editable this should be null
	 */
	public PropViewHelp(final UpdatableView<M> view, PropName<? extends Prop<T>, T> name, PropName<? extends EditableProp<T>, T> editableName) {
		
		this.view = view;
		this.name = name;
		this.editableName = editableName;
		
		this.proxy = view.getProxy();
		
		updateManager = Props.getPropSystem().getUpdateManager();
	}

	/**
	 * This will connect the {@link UpdatableView} to the update manager, and to its own proxy.
	 * The {@link UpdatableView} must be otherwise fully constructed, and have its
	 * proxy set.
	 * 
	 * This makes sure that:
	 * The view is registered with the update manager.
	 * When the proxy has a prop change, the update manager is informed that the view
	 * requires an update.
	 * An first request is made for an update to display the initial value
	 *
	 * Remember to {@link #dispose()} when the view is no longer in use
	 */
	public void connect() {
		updateManager.registerView(view);

		//When proxy has a change, we require an update
		proxy.props().addListener(this);
		
		//Initial update
		updateManager.updateRequiredBy(view);
	}
	
	/**
	 * Make a {@link PropViewHelp} for a view displaying a prop not known to be editable
	 * 
	 * This will connect the {@link UpdatableView} to the update manager, and to its own proxy.
	 * The {@link UpdatableView} must be otherwise fully constructed, and have its
	 * proxy set.
	 * 
	 * This makes sure that:
	 * The view is registered with the update manager.
	 * When the proxy has a prop change, the update manager is informed that the view
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
	public static <M extends Bean, T> PropViewHelp<M, T> create(UpdatableView<M> view, PropName<? extends Prop<T>, T> name) {
		return new PropViewHelp<M, T>(view, name, null);
	}
	
	/**
	 * Make a {@link PropViewHelp} for a view displaying an editable prop
	 * 
	 * This will connect the {@link UpdatableView} to the update manager, and to its own proxy.
	 * The {@link UpdatableView} must be otherwise fully constructed, and have its
	 * proxy set.
	 * 
	 * This makes sure that:
	 * The view is registered with the update manager.
	 * When the proxy has a prop change, the update manager is informed that the view
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
	public static <M extends Bean, T> PropViewHelp<M, T> createEditable(UpdatableView<M> view, PropName<? extends EditableProp<T>, T> name) {
		return new PropViewHelp<M, T>(view, name, name);
	}
	
	@Override
	public <S> void propChanged(PropEvent<S> event) {
		
		//OPTIMISATION, around 25% - the alternative to the method implementation
		//below is just to always call updateManager.updateRequiredBy(view);
		//This optimisation will break if any events have incorrect deep/shallow status
		
		//We receive events in response to changes to our proxy. If 
		//(and ONLY if) the event is shallow, we may be viewing a
		//new model bean, and so might have a new viewed prop.
		//In other words, if we see a deep change to the proxy,
		//it cannot have had its model() prop set to a new instance,
		//so it is safe to assume we are still looking at the same
		//prop in that bean
		if ((!event.isDeep()) || viewedProp == null) {
			//Get the model bean
			M currentModel = view.getProxy().model().get();
			
			//If model bean is null, prop is null
			if (currentModel == null) {
				viewedProp = null;
			//Otherwise get the viewed prop
			} else {
				viewedProp = currentModel.props().get(name); 
			}
			
			//We definitely need an update, since we have a completely new viewed prop
			updateManager.updateRequiredBy(view);
			
		//We have a shallow event, and we don't have a null viewed prop, so
		//we only need to update if the viewed prop was affected by the event
		} else if (event.getPropsInChain().contains(viewedProp)) {
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
		updateManager.deregisterView(view);
		proxy.props().removeListener(this);
	}
	
	/**
	 * Look up the value displayed by a prop view, which is the value
	 * of the named prop of its displayed bean
	 * @return
	 * 		The value of the named property, or null if the model itself is null
	 */
	public  T getPropValue() {
		//Get the model bean
		M currentModel = view.getProxy().model().get();
		
		//If model bean is null, return null
		if (currentModel == null) {
			return null;
		//Otherwise return the value of the named prop
		} else {
			return currentModel.props().get(name).get(); 
		}
	}
	
	/**
	 * Set the value displayed by a prop view, which is the value
	 * of the named prop of its displayed bean. This does nothing
	 * if the model is currently null, or the prop is not editable
	 * @param value
	 * 		The value to set in the property
	 */
	public void setPropValue(T value) {
		
		if (editableName == null) return;
		
		//Get the model bean
		M currentModel = view.getProxy().model().get();
		
		//If model bean is null, do nothing
		if (currentModel == null) {
			return ;
		//Otherwise set the value of the named prop
		} else {
			currentModel.props().get(editableName).set(value); 
		}
	}
	
}
