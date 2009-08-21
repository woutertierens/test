package org.jpropeller.view.bean.impl;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.ui.impl.CollapsiblePanel;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.Views;
import org.jpropeller.view.update.UpdateManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editable {@link View} for a {@link Bean}
 * Shows all primitive {@link Prop}s of a {@link Bean}
 * If the Bean contains complex {@link Prop}s, these may
 * be displayed in sub-{@link RecursiveBeanEditor}s (if a {@link PropViewFactoryRecursive}
 * is used).
 * Sub-{@link RecursiveBeanEditor}s are preceded by a named separator, and (if followed by
 * a component other than another {@link RecursiveBeanEditor}) followed with a blank separator.
 *  
 * @param <M> The type of model
 */
public class RecursiveBeanEditor<M extends Bean> implements JView, UpdatableSingleValueView<M>, ChangeListener {

	private Reference<M> model;
	
	private PropViewFactory factory;

	//The panel containing our subeditor components
	private JPanel panel;
	
	//The views used for each property of current model bean, indexed by prop name
	private Map<PropName<?>, JView> subViews = new HashMap<PropName<?>, JView>();
	
	//The views in order we will display them
	private List<PropName<?>> subViewNameList = new LinkedList<PropName<?>>();
	
	private M beanUsedForUI = null;

	private UpdateManager updateManager;
		
	private boolean selfNaming = false;
	
	private RecursiveBeanEditor(Reference<M> model, PropViewFactory factory, boolean selfNaming) {
		super();
		this.model = model;
		this.factory = factory;
		this.selfNaming = selfNaming;
		
		panel = new JPanel(new BorderLayout());
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//When model has a change, we require an update
		model.features().addListener(this);
		
		//Initial update
		update();
		
	}
	
	/**
	 * Make a new editor with default prop view factory
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @return	 		A new {@link RecursiveBeanEditor}
	 */
	public static <M extends Bean> RecursiveBeanEditor<M> create(Reference<M> model) {
		return new RecursiveBeanEditor<M>(model, new PropViewFactoryDefault(), false);
	}

	/**
	 * Make a new editor
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @param factory 	The {@link PropViewFactory} to use to produce {@link JView}s
	 * @return 			A new {@link RecursiveBeanEditor}
	 */
	public static <M extends Bean> RecursiveBeanEditor<M> create(Reference<M> model, PropViewFactory factory) {
		return new RecursiveBeanEditor<M>(model, factory, false);
	}
	
	/**
	 * Make a new editor
	 * 
	 * @param <M> 		The type of bean in the model 
	 * @param model 	The model containing the bean
	 * @param factory 	The {@link PropViewFactory} to use to produce {@link JView}s
	 * @param selfNaming If the editor is self-naming
	 * @return 			A new {@link RecursiveBeanEditor}
	 */
	public static <M extends Bean> RecursiveBeanEditor<M> create(Reference<M> model, PropViewFactory factory, boolean selfNaming) {
		return new RecursiveBeanEditor<M>(model, factory, selfNaming);
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

			String labelString = nameFor(bean, name, view);
			
			//Add only non-large components here
			if (view.format() != Format.LARGE) {				
				if (view.selfNaming()) {
					builder.append(component, 3);
				} else {
					builder.append(labelString + ":", component);
				}
			}
		}
				
		// Fill the grid with components
		for (PropName<?> name : subViewNameList) {
			JView view = subViews.get(name);
			JComponent component = view.getComponent();

			String labelString = nameFor(bean, name, view);
			
			//Add only large components here, putting them in a CollapsiblePanel
			if (view.format() == Format.LARGE) {
				CollapsiblePanel panel = new CollapsiblePanel(labelString);
				panel.contents().add(component);
				builder.append(panel.main(), 3);
			} 
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

	private String nameFor(M bean, PropName<?> name, JView view) {
		String labelString;
		if(name.isTGeneric()) {
			labelString = name.getString();
		} else {
			labelString = view.selfNaming() ? "" : PropUtils.localisedName(bean.getClass(), bean.features().get(name));
		}
		return labelString;
	}

	@Override
	public void update() {

		//logger.finest("update()");

		M newModel = getModel().value().get();
		M oldModel = beanUsedForUI;
		
		//If we have not changed value, nothing to do
		if (oldModel == newModel) return;
		
		//logger.finest("  ->new model");

		//Find the non-generic names of the new model
		List<PropName<?>> newViewNameList = new LinkedList<PropName<?>>();
		if (newModel != null) {
			for (Prop<?> prop : newModel.features()) {
				if (!prop.features().hasMetadata(BeanEditor.NO_DISPLAY)) {
					PropName<?> name = prop.getName();
					if(factory.providesFor(name)) {
						newViewNameList.add(name);					
					}
				}
			}
		}

		//If the old and new values have the same class, and
		//the list of displayed names is the same as current one,
		//then we don't need to alter displayed views
		if (sameClass(oldModel, newModel) && newViewNameList.equals(subViewNameList)) {
			return;
		}
		
		//Find the views we will use
		Map<PropName<?>, JView> newViews = new HashMap<PropName<?>, JView>();
		
		//If we have a non-null new value, get views and prop names from it
		if (newModel != null) {
			for (PropName<?> newName : newViewNameList) {
				
				//Note that we create the view using our own model, so it will always
				//(try to) display our current model
				JView newView = factory.viewFor(model, newName);
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
		return selfNaming;
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}
	
}
