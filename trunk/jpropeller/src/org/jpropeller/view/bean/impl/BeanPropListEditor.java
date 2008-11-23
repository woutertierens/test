package org.jpropeller.view.bean.impl;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.util.PropUtils;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editable {@link View} for a {@link Bean}
 * Shows all primitive {@link Prop}s of a {@link Bean} 
 * @param <M> 
 * 		The type of model
 */
public class BeanPropListEditor<M extends Bean> implements UpdatableView<M>, JView<M>, PropListener {

	private Reference<M> model;
	
	private PropViewFactory factory;

	//The panel containing our subeditor components
	private JPanel panel;
	
	//The views used for each property of current model bean, indexed by prop name
	private Map<PropName<?,?>, JView<?>> subViews = new HashMap<PropName<?,?>, JView<?>>();
	
	//The views in order we will display them
	private List<PropName<?,?>> subViewNameList = new LinkedList<PropName<?,?>>();
	
	private M beanUsedForUI = null;

	private UpdateManager updateManager;
	
	private BeanPropListEditor(Reference<M> model, PropViewFactory factory) {
		super();
		this.model = model;
		this.factory = factory;
		
		panel = new JPanel(new BorderLayout());
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerView(this);

		//When model has a change, we require an update
		model.props().addListener(this);
		
		//Initial update
		updateNow();
		
	}
	
	/**
	 * Make a new editor with default prop view factory
	 * @param <M>
	 * 		The type of bean in the model 
	 * @param model 
	 * 		The model containing the bean
	 * @return
	 * 		A new {@link BeanPropListEditor}
	 */
	public static <M extends Bean> BeanPropListEditor<M> create(Reference<M> model) {
		return new BeanPropListEditor<M>(model, new PropViewFactoryDefault());
	}

	/**
	 * Make a new editor
	 * @param <M>
	 * 		The type of bean in the model 
	 * @param model 
	 * 		The model containing the bean
	 * @param factory
	 * 		The {@link PropViewFactory} to use to produce {@link JView}s
	 * @return
	 * 		A new {@link BeanPropListEditor}
	 */
	public static <M extends Bean> BeanPropListEditor<M> create(Reference<M> model, PropViewFactory factory) {
		return new BeanPropListEditor<M>(model, new PropViewFactoryDefault());
	}

	@Override
	public void disposeNow() {
		updateManager.deregisterView(this);
		model.props().removeListener(this);
	}

	@Override
	public Reference<M> getModel() {
		return model;
	}

	@Override
	public void cancel() {
		//Cancel all subeditors
		for (View<?> sub : subViews.values()) {
			sub.cancel();
		}
	}

	@Override
	public void commit() throws CompletionException {
		//Commit all subeditors
		for (View<?> sub : subViews.values()) {
			sub.commit();
		}
	}

	@Override
	public boolean isEditing() {
		//We are editing if any subview is editing
		for (View<?> sub : subViews.values()) {
			if (sub.isEditing()) {
				return true;
			}
		}
		return false;
	}
	
	private void rebuild() {
		
		//logger.finest("rebuild()");
		
		//Make a row per subview - for the subview itself and the gap to the next
		StringBuilder rows = new StringBuilder();
		for (int i = 0; i < subViewNameList.size(); i++) {
			if (i == 0) {
				rows.append("p");
			} else {
				rows.append(", 3dlu, p");
			}
		}
		
		FormLayout layout = new FormLayout(
			    "right:pref, $lcgap, fill:pref:grow", // columns
			    rows.toString());       // rows
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		// Obtain a reusable constraints object to place components in the grid.
		CellConstraints cc = new CellConstraints();

		// Fill the grid with components

		M bean = getModel().value().get();
		
		int y = 1;
		for (PropName<?,?> name : subViewNameList) {

			JView<?> view = subViews.get(name);
			JComponent component = view.getComponent();

			String localName = PropUtils.localisedName(bean.props().get(name));
			builder.addLabel(localName,			cc.xy (1,  y));
			builder.add(component,         		cc.xy (3,  y));

			y += 2;
		}

		panel.removeAll();
		panel.add(builder.getPanel());
		panel.revalidate();

		//Store the bean we just used - this is used on update to
		//check whether we have changed model instance, and might
		//hence need to rebuild again etc.
		beanUsedForUI = bean;
		
	}

	@Override
	public void updateNow() {

		//logger.finest("update()");

		M newModel = getModel().value().get();
		M oldModel = beanUsedForUI;
		
		//If we have not changed value, nothing to do
		if (oldModel == newModel) return;
		
		//logger.finest("  ->new model");

		//Find the non-generic names of the new model
		List<PropName<?,?>> newViewNameList = new LinkedList<PropName<?,?>>();
		if (newModel != null) {
			for (GeneralProp<?> prop : newModel.props()) {
				GenericPropName<?, ?> genericName = prop.getName();
				if (genericName instanceof PropName<?, ?>) {
					PropName<?, ?> name = (PropName<?, ?>) genericName;
					newViewNameList.add(name);					
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
		Map<PropName<?,?>, JView<?>> newViews = new HashMap<PropName<?,?>, JView<?>>();
		
		//If we have a non-null new value, get views and prop names from it
		if (newModel != null) {
			for (PropName<?, ?> newName : newViewNameList) {
				
				//Note that we create the view using our own model, so it will always
				//(try to) display our current model
				JView<?> newView = factory.viewFor(model, newName);
				if (newView!=null) {
					newViews.put(newName, newView);
				}
			}
		}
		
		//logger.finest("  ->need to use new views");

		//Dispose of the old views
		for (JView<?> oldView : subViews.values()) {
			oldView.disposeNow();
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
	public <T> void propChanged(PropEvent<T> event) {
		//OPTIMISATION, around 10% - the alternative to the method implementation
		//below is just to always call updateManager.updateRequiredBy(view);
		//This optimisation will break if any events have incorrect deep/shallow status
		
		//We only require an update on a shallow change to model - that is,
		//when a new value of model is set, NOT when one of the props of model
		//changes. When a prop of model changes, it will be displayed by a
		//sub model update
		if (!event.isDeep()) {
			updateManager.updateRequiredBy(this);
		}
		
		//This is the unoptimised code - always update
		//updateManager.updateRequiredBy(this);
	}
	
}
