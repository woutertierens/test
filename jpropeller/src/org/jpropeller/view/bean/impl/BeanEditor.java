package org.jpropeller.view.bean.impl;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.ReferenceDefault;
import org.jpropeller.system.Props;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.SingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.Views;
import org.jpropeller.view.update.UpdateManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editable {@link View} for a {@link Bean}
 * Shows all primitive {@link Prop}s of a {@link Bean}
 *  
 * @param <M> The type of model
 */
public class BeanEditor<M extends Bean> implements JView, SingleValueView<M>, ChangeListener {

	//FIXME make this use a Prop directly, and make reference-using constructors just extract the value prop
	
	/**
	 * Metadata key to indicate props should not be displayed in this editor
	 */
	public final static String NO_DISPLAY = "org.jpropeller.view.bean.impl.NO_DISPLAY";
	
	private Reference<M> model;
	
	private PropViewFactory factory;

	//The panel containing our subeditor components
	private JPanel panel;
	
	//The views used for each property of current model bean, indexed by prop name
	private Map<PropName<?>, JView> subViews = new HashMap<PropName<?>, JView>();
	
	//The view names in order we will display them
	private List<PropName<?>> subViewNameList = new LinkedList<PropName<?>>();
	
	//The prop editability in order we will display props
	private List<PropEditability> subViewEditabilityList = new LinkedList<PropEditability>();
	
	private M beanUsedForUI = null;

	private UpdateManager updateManager;
	
	private final Prop<Boolean> locked;
	
	private BeanEditor(Reference<M> model, PropViewFactory factory, Prop<Boolean> locked) {
		super();
		this.model = model;
		this.factory = factory;
		this.locked = locked;
		
		panel = new JPanel(new BorderLayout());
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//When model has a change, we require an update
		model.features().addListener(this);
		
		//Initial update
		update();
		
	}

	/**
	 * Make a new editor
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param value		A {@link Prop} containing the bean to edit
	 * @param factory 	The {@link PropViewFactory} to use to produce {@link JView}s
	 * @return 			A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Prop<M> value, PropViewFactory factory) {
		return new BeanEditor<M>(ReferenceDefault.create(value), factory, null);
	}
	
	/**
	 * Make a new editor with default prop view factory
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @return	 		A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Reference<M> model) {
		return new BeanEditor<M>(model, new PropViewFactoryDefault(), null);
	}

	/**
	 * Make a new editor
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @param factory 	The {@link PropViewFactory} to use to produce {@link JView}s
	 * @return 			A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Reference<M> model, PropViewFactory factory) {
		return new BeanEditor<M>(model, factory, null);
	}

	/**
	 * Make a new editor with default prop view factory
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param value		A {@link Prop} containing the bean to edit
	 * @return	 		A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Prop<M> value) {
		return new BeanEditor<M>(ReferenceDefault.create(value), new PropViewFactoryDefault(), null);
	}
	
	/**
	 * Make a new editor
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @param factory 	The {@link PropViewFactory} to use to produce {@link JView}s
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.	
	 * @return 			A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Reference<M> model, PropViewFactory factory, Prop<Boolean> locked) {
		return new BeanEditor<M>(model, factory, locked);
	}

	/**
	 * Make a new editor with default prop view factory
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param value		A {@link Prop} containing the bean to edit
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.	
	 * @return	 		A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Prop<M> value, Prop<Boolean> locked) {
		return new BeanEditor<M>(ReferenceDefault.create(value), new PropViewFactoryDefault(), locked);
	}
	
	/**
	 * Make a new editor with default prop view factory
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.	
	 * @return	 		A new {@link BeanEditor}
	 */
	public static <M extends Bean> BeanEditor<M> create(Reference<M> model, Prop<Boolean> locked) {
		return new BeanEditor<M>(model, new PropViewFactoryDefault(), locked);
	}
	
	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		model.features().removeListener(this);
		
		//Dispose of the old views
		for (JView oldView : subViews.values()) {
			oldView.dispose();
		}
	}

	@Override
	public Reference<M> getModel() {
		return model;
	}

	@Override
	public void cancel() {
		//Cancel all subeditors
		for (View sub : subViews.values()) {
			sub.cancel();
		}
	}

	@Override
	public void commit() throws CompletionException {
		//Commit all subeditors
		for (View sub : subViews.values()) {
			sub.commit();
		}
	}

	@Override
	public boolean isEditing() {
		//We are editing if any subview is editing
		for (View sub : subViews.values()) {
			if (sub.isEditing()) {
				return true;
			}
		}
		return false;
	}
	
	private void rebuild() {
		
		//logger.finest("rebuild()");
		
		FormLayout layout = new FormLayout(Views.getViewSystem().getStandard3ColumnDefinition());
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		//builder.setDefaultDialogBorder();

		M bean = getModel().value().get();
		
		// Fill the grid with components
		for (PropName<?> name : subViewNameList) {

			JView view = subViews.get(name);
			JComponent component = view.getComponent();

			String labelString = view.selfNaming() ? "" : PropUtils.localisedName(bean.getClass(), bean.features().get(name)) + ":";
			
			builder.append(labelString, component);
		}

		//int[] rowGroup = new int[subViewNameList.size()];
		//for (int i = 0; i < rowGroup.length; i++) rowGroup[i] = i+1;
		//builder.getLayout().setRowGroups(new int[][]{rowGroup});

		//Use the new panel in this editor
		panel.removeAll();
		panel.add(builder.getPanel());
		panel.revalidate();

		//Store the bean we just used - this is used on update to
		//check whether we have changed model instance, and might
		//hence need to rebuild again etc.
		beanUsedForUI = bean;
		
	}

	@Override
	public void update() {

		//logger.finest("update()");

		M newModel = getModel().value().get();
		M oldModel = beanUsedForUI;
		
		//If we have not changed value, nothing to do
		if (oldModel == newModel) return;

		//FIXME remove this FIXME after checking new behaviour is compatible - this
		//line makes sure that we minimise the name/editability/class check below - 
		// even if we don't rebuild the UI, we DO remember the model as being
		//that used for UI, so if the bean has a deep change we don't recheck
		//props, we just recognise that we are still viewing the same instance.
		//The old behaviour was not to update beanUsedForUI until the UI is actually
		//rebuilt, meaning that if we switch from one Bean to another instance with
		//the same props, we will perform a name/editability/class check every
		//time it has a deep change.
		beanUsedForUI = newModel;
		
		//logger.finest("  ->new model");

		//Find the non-generic names of the new model
		List<PropName<?>> newViewNameList = new LinkedList<PropName<?>>();
		List<PropEditability> newViewEditabilityList = new LinkedList<PropEditability>();
		if (newModel != null) {
			for (Prop<?> prop : newModel.features()) {
				PropName<?> name = prop.getName();
				if (!name.isTGeneric() && !prop.features().hasMetadata(NO_DISPLAY)) {
					newViewNameList.add(name);
					newViewEditabilityList.add(prop.getEditability());					
				}
			}
		}

		//If the old and new values have the same class, and
		//the list of displayed names is the same as current one,
		//then we don't need to alter displayed views
		if (sameClass(oldModel, newModel) && newViewNameList.equals(subViewNameList) && newViewEditabilityList.equals(subViewEditabilityList)) {
			return;
		}
		
		//Find the views we will use
		Map<PropName<?>, JView> newViews = new HashMap<PropName<?>, JView>();
		
		//If we have a non-null new value, get views and prop names from it
		if (newModel != null) {
			for (PropName<?> newName : newViewNameList) {
				
				//Note that we create the view using our own model, so it will always
				//(try to) display our current model
				JView newView = factory.viewFor(model, newName, locked);
				if (newView!=null) {
					newViews.put(newName, newView);
				}
			}
		}
		
		//logger.finest("  ->need to use new views");

		//Dispose of the old views
		for (JView oldView : subViews.values()) {
			oldView.dispose();
		}
		
		//Use the new views
		subViews = newViews;
		subViewNameList = newViewNameList;
		subViewEditabilityList = newViewEditabilityList;
		
		//Rebuild using new views
		rebuild();
	}
	
	/**
	 * Return true if a and b are the same class, or are both null
	 * @param a
	 * 		first object
	 * @param b
	 * 		second object
	 * @return
	 * 		True if a and b are same class, or both null
	 */
	private static boolean sameClass(Object a, Object b) {
		if (a == null) {
			return (b == null);
		} else if (b == null) {
			return false;
		} else {
			return a.getClass().equals(b.getClass());
		}
	}
	
	@Override
	public JPanel getComponent() {
		return panel;
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//OPTIMISATION, around 10% - the alternative to the method implementation
		//below is just to always call updateManager.updateRequiredBy(view);
		//This optimisation will break if any events have incorrect deep/shallow status
		
		//We only require an update on a shallow change to model - that is,
		//when a new value of model is set, NOT when one of the props of model
		//changes. When a prop of model changes, it will be displayed by a
		//sub model update
		
		//First see if the model has changed at all
		if (changes.containsKey(model)) {
			//Only update if the model has a change to any instances
			if (!changes.get(model).sameInstances()) {
				updateManager.updateRequiredBy(this);
			}
		}
		
		//This is the unoptimised code - always update
		//updateManager.updateRequiredBy(this);
		
	}

	@Override
	public boolean selfNaming() {
		return true;
	}
	
	@Override
	public Format format() {
		return Format.LARGE;
	}
	
}
