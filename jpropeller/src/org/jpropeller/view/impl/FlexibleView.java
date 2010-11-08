package org.jpropeller.view.impl;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.ReferenceWithClassFilter;
import org.jpropeller.system.Props;
import org.jpropeller.util.ViewUtils;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.JViewSource;
import org.jpropeller.view.ViewSystem;
import org.jpropeller.view.Views;
import org.jpropeller.view.bean.impl.BeanEditor;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link FlexibleView} will view any class for which a
 * {@link JView} is available from {@link Views#getViewSystem()},
 * and will also fall back to using a {@link BeanEditor}
 * for {@link Bean}s without a specific {@link JView}, or to
 * a {@link LabelView} for other Objects.
 * <p/>
 * {@link JView}s are cached for performance, and so {@link FlexibleView}
 * is best used in cases where there are a relatively small number
 * of viewed classes.
 */
public class FlexibleView implements JView, ChangeListener {

	private JPanel panel;
	/**
	 * Store the views we have used in the past - we use these
	 * in preference to new ones
	 */
	private Map<Class<?>, JView> views = new HashMap<Class<?>, JView>();
	
	private LabelView labelView;
	private BeanEditor<?> beanEditor;
	private Reference<?> ref;
	
	/**
	 * The currently-used view
	 */
	private JView currentView;
	
	private UpdateManager updateManager;
	
	private ViewSystem viewSystem = null;
	private final JView blank;
	private boolean displayBeanEditor = false;
	
	/**
	 * Make a {@link FlexibleView}
	 * 
	 * @param ref			The reference to view
	 * @param displayBeanEditor	True to fallback to a bean editor for beans rather than the defaultView. 
	 * @param defaultView 	View to display when no other view is available, can be null.
	 */
	public FlexibleView(Reference<?> ref, boolean displayBeanEditor, final JView defaultView) {
		this(ref, null, displayBeanEditor, defaultView);
	}
	
	/**
	 * Make a {@link FlexibleView}
	 * 
	 * @param ref			The reference to view 
	 * @param displayBeanEditor	True to fallback to a bean editor for beans rather than the defaultView. 
	 * @param string 		String to display when no other view is available.
	 */
	public FlexibleView(Reference<?> ref, boolean displayBeanEditor, String string) {
		this(ref, null, displayBeanEditor, new FixedComponentView(string));
	}
	
	/**
	 * Make a {@link FlexibleView}
	 * 
	 * @param ref			The reference to view
	 * @param viewSystem	The viewsystem to use. 
	 * @param displayBeanEditor	True to fallback to a bean editor for beans rather than the defaultView. 
	 * @param string 		String to display when no other view is available.
	 */
	public FlexibleView(Reference<?> ref, ViewSystem viewSystem, boolean displayBeanEditor, String string) {
		this(ref, viewSystem, displayBeanEditor, new FixedComponentView(string));
	}
	
	/**
	 * Make a {@link FlexibleView}
	 * 
	 * @param ref			The reference to view
	 * @param viewSystem 	The {@link ViewSystem} to use to look up views for classes,
	 * 						this will be checked before {@link Views#getViewSystem()}
	 * @param displayBeanEditor	True to fallback to a bean editor for beans rather than the defaultView. 
	 * @param defaultView 	View to display when no other view is available, can be null.
	 */
	public FlexibleView(Reference<?> ref, ViewSystem viewSystem, final boolean displayBeanEditor, final JView defaultView) {
		init(ref, viewSystem);
		
		this.displayBeanEditor = displayBeanEditor;
		
		blank = defaultView == null ? new FixedComponentView("") : defaultView;
		
		registerListeners(ref);
		
		//Initial update
		update();	
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//We only require an update on a shallow change to ref - that is,
		//when a new value of ref is set, NOT when one of the props of ref
		//changes. When a deep change occurred, it will be displayed by a
		//sub view update
		
		//First see if the ref value has changed at all
		Prop<?> val = ref.value();
		if (changes.containsKey(val)) {
			//Only update if the value has a change to any instances
			if (!changes.get(val).sameInstances()) {
				updateManager.updateRequiredBy(this);
			}
		}

	}
	
	@Override
	public void update() {
		
		//FIXME
		//We could easily set a view here that showed a "busy" icon in case
		//it takes some time to create the new view
		
		//Views may change props, so we need to do this later, outside
		//change response.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//We only update when we may have a new ref value - when this is the case,
				//we need to check whether we need a new editor.
				JView newView = findView();

				//New value may be same (or compatible) class
				if (currentView == newView) {
					return;
				}
				
				changeToView(newView);
			}
		});		
	}

	private void changeToView(JView newView) {
		panel.removeAll();
		
		panel.add(newView.getComponent());
		panel.revalidate();
		panel.repaint();

		currentView = newView;
	}
	
	private void registerListeners(Reference<?> ref) {
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to just the value of the reference
		ref.value().features().addListener(this);
	}


	private void init(Reference<?> ref, ViewSystem viewSystem) {
		this.ref = ref;
		this.viewSystem = viewSystem;

		panel = new JPanel(new BorderLayout());
		
		ViewUtils.outerise(panel);
		
		//Initial view using label
		labelView = new LabelView(ref);
		currentView = labelView;
		changeToView(labelView);
	}
	
	//Safe to ignore the raw reference made by class filter - it exists to
	//ensure that the class is correct
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void makeView(Class<?> clazz) {
		//If we have done class already, return
		if (views.containsKey(clazz)) {
			return;
		}
		
		//Try to get a view from specific view system, if we have one
		if (viewSystem != null) {
			if (findViewFromSystem(viewSystem, clazz)) return;
		}
		
		//Try to get a view from system
		if (findViewFromSystem(Views.getViewSystem(), clazz)) return;
		
		//We failed to get a system view, so if we have a bean, use a bean editor
		if (Bean.class.isAssignableFrom(clazz)) {
			//If we have made a bean editor already, return
			if (beanEditor != null) {
				return;
			} else {
				//Make a new bean editor
				ReferenceWithClassFilter beanRef = ReferenceWithClassFilter.createUnsafe(Bean.class, ref);
				beanEditor = BeanEditor.create(beanRef);
				return;				
			}
		}
		
		//No specific view is available for the class, so give up
	}
	
	//We can suppress the warning because:
	//We rely on the view source to provide a view
	//that will accept instances of the class of value
	//we currently have. Then we create a ReferenceWithClassFilter
	//that will only ever have a value of that class, or null. We
	//then know it is safe to provide this as the reference for
	//the view source. We cannot track this type safety through
	//at compile time, due to type erasure.
	//Note - we need to be careful with this, since the filtered
	//reference has a editable value (if the original reference
	//does). However, if we have SEEN a value of a certain class
	//in the reference, it is safe to assume that the type of the
	//reference is "super" that class
	//This is why we create a BeanEditor in this method, when we
	//have seen a Bean in the reference, rather than creating
	//one when the FlexibleView is created (like we do with the 
	//LabelView)
	@SuppressWarnings({"unchecked", "rawtypes"})
	private boolean findViewFromSystem(ViewSystem system, Class clazz) {
		JViewSource<?> sourceFor = system.jviewSourceFor(clazz);
		if (sourceFor != null) {
			//We need a new reference that will filter to the right class, so
			//that we don't give the view a value of a class it can't display.
			ReferenceWithClassFilter filteredRef = ReferenceWithClassFilter.createUnsafe(clazz, ref);
			
			JView newView = sourceFor.get(filteredRef);
			
			//Add the view to the map
			views.put(clazz, newView);
			return true;
		}

		return false;
	}
	
	private JView findView() {
		Object value = ref.value().get();
		
		
		//Just use labelView for nulls
		if (value == null) {
			return blank;
		}
		
		//Otherwise look up from class
		Class<?> clazz = value.getClass();
		
		//Lazily register the view for this class.
		if(!views.containsKey(clazz)) {
			makeView(clazz);
		}
		
		//By preference, use a specific view
		if (views.containsKey(clazz)) {
			return views.get(clazz);
		}
		
		//We failed to get a specific view, so if we have a bean, 
		//use a bean editor if we have one
		if(displayBeanEditor) {
			if (value instanceof Bean) {
				if (beanEditor != null) {
					return beanEditor;
				}
			}
		} 
		
		//We failed to get anything else, use label view
		return blank;
	}
	
	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public boolean selfNaming() {
		//Just displays editor, not in general self-naming
		return false;
	}

	@Override
	public boolean isEditing() {
		//Check if current view is editing
		if (currentView != null) {
			return currentView.isEditing();
		} else {
			return false;
		}
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		ref.features().removeListener(this);
		
		labelView.dispose();
		
		//Dispose of other views
		for (JView view : views.values()) {
			view.dispose();
		}
		views.clear();
	}

	@Override
	public void cancel() {
		//Cancel our current view
		if (currentView != null) {
			currentView.cancel();
		}
	}

	@Override
	public void commit() throws CompletionException {
		//Commit our current view
		if (currentView != null) {
			currentView.commit();
		}
	}
	
	@Override
	public Format format() {
		//May fall back to using a bean editor, and so it is safest for this to be "Large"
		return Format.LARGE;
	}

	
}
