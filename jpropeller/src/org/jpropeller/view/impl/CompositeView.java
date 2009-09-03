package org.jpropeller.view.impl;

import java.util.List;

import javax.swing.JComponent;

import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;

/**
 * A {@link CompositeView} is a {@link JView} made up entirely
 * of child {@link JView}s.
 * @param <C> 		The type of {@link JComponent} provided
 */
public class CompositeView<C extends JComponent> implements JView {
	
	private final C component;
	private final CompositeViewHelper helper;
	private final boolean selfNaming;
	private final Runnable disposeRunnable;

	/**
	 * Create a {@link CompositeView}
	 * @param views
	 * 		The child views that together
	 * form this view - they will be commited,
	 * cancelled and disposed when this view
	 * is, but are expected to update themselves.
	 * @param component
	 * 		The component used to display this view - 
	 * generally this is formed from the components
	 * of the child views, see {@link JView#getComponent()}
	 * @param selfNaming
	 * 		Whether the component is selfNaming,
	 * see {@link JView#selfNaming()}
	 */
	public CompositeView(
			List<View> views,
			C component,
			boolean selfNaming) {
		this(views, component, selfNaming, null);
	}
	
	/**
	 * Create a {@link CompositeView}
	 * 
	 * @param views				The child views that together
	 * 							form this view - they will be commited,
	 * 							cancelled and disposed when this view
	 * 							is, but are expected to update themselves.
	 * @param component			The component used to display this view - 
	 * 							generally this is formed from the components
	 * 							of the child views, see {@link JView#getComponent()}
	 * @param selfNaming		Whether the component is selfNaming,
	 * 							see {@link JView#selfNaming()}
	 * @param disposeRunnable	{@link Runnable} to be executed as
	 * 							last stage of {@link #dispose()}, or
	 * 							null if there is nothing to do
	 */
	public CompositeView(
			List<View> views,
			C component,
			boolean selfNaming,
			Runnable disposeRunnable) {
		this.component = component;
		this.selfNaming = selfNaming;
		helper = new CompositeViewHelper(views);
		this.disposeRunnable = disposeRunnable;
	}


	@Override
	public C getComponent() {
		return component;
	}

	@Override
	public boolean selfNaming() {
		return selfNaming;
	}
	
	//JView methods delegated to helper
	
	@Override
	public void cancel() {
		helper.cancel();
	}

	@Override
	public void commit() throws CompletionException {
		helper.commit();
	}

	@Override
	public void dispose() {
		helper.dispose();
		if (disposeRunnable != null) {
			disposeRunnable.run();
		}
	}

	@Override
	public boolean isEditing() {
		return helper.isEditing();
	}

	@Override
	public void update() {
		helper.update();
	}
	
	@Override
	public Format format() {
		return Format.LARGE;
	}

}
