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
	
	C component;
	CompositeViewHelper helper;
	List<View> views;
	boolean selfNaming;

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
		this.views = views;
		this.component = component;
		helper = new CompositeViewHelper(views);
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
