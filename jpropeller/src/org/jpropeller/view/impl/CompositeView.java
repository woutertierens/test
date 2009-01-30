package org.jpropeller.view.impl;

import java.util.List;

import javax.swing.JComponent;

import org.jpropeller.reference.Reference;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;

/**
 * A {@link CompositeView} is a {@link JView} made up entirely
 * of child {@link JView}s.
 * @param <M>
 * 		The type of edited model
 */
public class CompositeView<M> implements JView<M> {
	
	JComponent component;
	Reference<? extends M> model;
	CompositeViewHelper helper;
	List<View<?>> views;
	boolean selfNaming;

	/**
	 * Create a {@link CompositeView}
	 * @param model
	 * 		The model displayed by the {@link JView}
	 * See {@link JView#getModel()}
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
			Reference<? extends M> model, 
			List<View<?>> views,
			JComponent component,
			boolean selfNaming) {
		this.model = model;
		this.views = views;
		this.component = component;
		helper = new CompositeViewHelper(views);
	}


	@Override
	public JComponent getComponent() {
		return component;
	}

	@Override
	public boolean selfNaming() {
		return selfNaming;
	}

	@Override
	public Reference<? extends M> getModel() {
		return model;
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
}
