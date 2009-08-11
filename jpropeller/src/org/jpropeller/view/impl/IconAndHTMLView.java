package org.jpropeller.view.impl;

import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.SingleValueView;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} of any Object, which will display it
 * rendered as an {@link Icon} and html in a {@link JLabel},
 * using a specified {@link IconAndHTMLRenderer}
 */
public class IconAndHTMLView implements JView, SingleValueView<Object>, ChangeListener {

	private final Reference<?> ref;
	private final UpdateManager updateManager;
	private final JLabel label;
	private final IconAndHTMLRenderer renderer;
	private final boolean selfNaming;
	
	/**
	 * Create a {@link IconAndHTMLView} that is self naming 
	 * 
	 * @param ref		The reference to display
	 * @param renderer	The {@link IconAndHTMLRenderer} to use to display contents
	 */
	public IconAndHTMLView(Reference<?> ref, IconAndHTMLRenderer renderer) {
		this(ref, renderer, true);
	}
	
	/**
	 * Create a {@link IconAndHTMLView}
	 * 
	 * @param ref		The reference to display
	 * @param renderer	The {@link IconAndHTMLRenderer} to use to display contents
	 * @param selfNaming	True if the renderer is enough to show the name of the
	 * 						object on its own, or false if the name should also be 
	 * 						displayed separately
	 */
	public IconAndHTMLView(Reference<?> ref, IconAndHTMLRenderer renderer, boolean selfNaming) {
		super();
		this.ref = ref;
		this.renderer = renderer;
		this.selfNaming = selfNaming;
		
		label = new JLabel();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to just the value of the reference, update when it changes
		ref.value().features().addListener(this);

		//Initial update
		update();
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Always update - we only listen to the value, so we only see
		//a change when we need to update
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		Object value = ref.value().get();
		
		if (renderer.canRender(value)) {
			label.setText(renderer.getHTML(value));
			label.setIcon(renderer.getIcon(value));
		} else {
			String s = "";
			if (value != null) {
				s = value.toString();
			}
			if (!s.equals(label.getText())) {
				label.setText(s);
			}
			if (label.getIcon() != null) {
				label.setIcon(null);
			}
		}
	}

	@Override
	public JLabel getComponent() {
		return label;
	}

	@Override
	public boolean selfNaming() {
		return selfNaming;
	}

	@Override
	public void cancel() {
		//Never edits - nothing to do
	}

	@Override
	public void commit() throws CompletionException {
		//Never edits - nothing to do
	}

	@Override
	public Reference<?> getModel() {
		return ref;
	}

	@Override
	public boolean isEditing() {
		//Never edits
		return false;
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		ref.features().removeListener(this);
	}

	@Override
	public Format format() {
		return Format.MEDIUM;
	}

}
