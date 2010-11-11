package org.jpropeller.view.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.ui.IconAndHTMLRenderer;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.info.Described;
import org.jpropeller.view.info.Illustrated;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link JView} of any Object, which will display its
 * {@link Object#toString()} result as a {@link JLabel}
 */
public class LabelView implements JView, ChangeListener {

	private Prop<?> prop;
	private UpdateManager updateManager;
	private JLabel label;
	private final IconAndHTMLRenderer renderer;

	private final static DecimalFormat format = new DecimalFormat("#.##");
	
	/**
	 * Create a {@link LabelView}
	 * 
	 * @param prop		The {@link Prop} to display
	 * @param renderer	{@link IconAndHTMLRenderer} to render (suitable) contents.
	 * 					Ignored if null
	 */
	public LabelView(Prop<?> prop, IconAndHTMLRenderer renderer) {
		super();
		this.prop = prop;
		this.renderer = renderer;
		
		label = new JLabel();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		//Listen to the prop
		prop.features().addListener(this);

		//Initial update
		update();
	}
	
	/**
	 * Create a {@link LabelView}
	 * 
	 * @param prop		The {@link Prop} to display
	 */
	public LabelView(Prop<?> prop) {
		this(prop, null);
	}
	
	/**
	 * Create a {@link LabelView}
	 * 
	 * @param ref		The reference to display
	 */
	public LabelView(Reference<?> ref) {
		this(ref.value(), null);
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Always update - we only listen to the value, so we only see
		//a change when we need to update
		updateManager.updateRequiredBy(this);
	}

	@Override
	public void update() {
		Object value = prop.get();
		
		String s = "";
		Icon icon = null;
		if (value != null) {
			if (renderer != null && renderer.canRender(value)) {
				s = renderer.getHTML(value);
				icon = renderer.getIcon(value);
			} else {
				if (value instanceof Number) {
					s = format.format(value);
				}
				if(value instanceof Illustrated) {
					label.setIcon(((Illustrated)value).illustration().get());
				} 
				if(value instanceof Described) {
					s = ((Described)value).description().get();
				} else {
					s = value.toString();
				}
				if(value instanceof Icon) {
					icon = (Icon)value;
					s = "";
				}
			}
		}
		
		if (!s.equals(label.getText())) {
			label.setText(s);
		}
		Icon labelIcon = label.getIcon();
		if (!GeneralUtils.equalIncludingNull(icon, labelIcon)) {
			label.setIcon(icon);
		}
		
	}

	@Override
	public JComponent getComponent() {
		return label;
	}

	@Override
	public boolean selfNaming() {
		return false;
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
	public boolean isEditing() {
		//Never edits
		return false;
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		prop.features().removeListener(this);
	}

	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}

}
