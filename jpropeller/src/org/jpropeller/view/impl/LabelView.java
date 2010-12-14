package org.jpropeller.view.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
	private final static DateTimeFormatter dateTimeFormat = DateTimeFormat.shortDateTime();
	
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
				s = "<html>" + renderer.getHTML(value) + "</html>";
				icon = renderer.getIcon(value);
			} else {

				if(value instanceof Illustrated) {
					icon = (((Illustrated)value).illustration().get());
				} else	if(value instanceof Icon) {
					icon = (Icon)value;
					s = "";
				}
				
				if (value instanceof Described) {
					s = ((Described)value).description().get();
				} else if (value instanceof Number) {
					s = format.format(value);
				} else if (value instanceof DateTime) {
					s = dateTimeFormat.print((DateTime)value);
				} else {
					s = value.toString();
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
	public JLabel getComponent() {
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
