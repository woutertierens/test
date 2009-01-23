package org.jpropeller.view.impl;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.ui.ImmutableIcon;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdatableView;

/**
 * A non-editing {@link View} for a single {@link Prop} of {@link ImmutableIcon}
 * type in a {@link Bean}. Displays the {@link ImmutableIcon} in a {@link JLabel}.
 */
public class ImmutableIconPropView implements UpdatableView<Bean>, JView<Bean> {

	PropViewHelp<Bean, ImmutableIcon> help;
	Reference<? extends Bean> model;
	PropName<? extends Prop<ImmutableIcon>, ImmutableIcon> displayedName;

	//JScrollPane scroll;
	JLabel label;

	private ImmutableIconPropView(Reference<? extends Bean> model, PropName<? extends Prop<ImmutableIcon>, ImmutableIcon> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		label = new JLabel();
		//scroll = new JScrollPane(label);
		//scroll.setPreferredSize(new Dimension(32, 32));			
		
		help = new PropViewHelp<Bean, ImmutableIcon>(this, displayedName, null);
		help.connect();
	}

	@Override
	public void dispose() {
		help.dispose();
	}

	/**
	 * Create an {@link ImmutableIconPropView}
	 * @param model 
	 * 		The model {@link Reference} for this {@link View}
	 * @param displayedName
	 * 		The name of the displayed property 
	 * @return
	 * 		A {@link ImmutableIconPropView} 
	 */
	public static ImmutableIconPropView create(Reference<? extends Bean> model, PropName<? extends Prop<ImmutableIcon>, ImmutableIcon> displayedName) {
		return new ImmutableIconPropView(model, displayedName);
	}
	
	@Override
	public Reference<? extends Bean> getModel() {
		return model;
	}

	/**
	 * Get the {@link JComponent} used for display
	 * @return
	 * 		The component
	 */
	public JComponent getComponent() {
		//return scroll;
		return label;
	}
	
	@Override
	public void cancel() {
		//Nothing to do, view only
	}

	@Override
	public void commit() {
		//Nothing to do, view only
	}

	@Override
	public boolean isEditing() {
		//Never editing, view only
		return false;
	}

	@Override
	public void update() {
		ImmutableIcon icon = help.getPropValue();

		if (icon == null) return;
		
		label.setIcon(icon);
	}

	@Override
	public boolean selfNaming() {
		//Doesn't display name - just image
		return false;
	}

}
