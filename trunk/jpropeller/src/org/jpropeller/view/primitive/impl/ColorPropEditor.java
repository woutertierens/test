package org.jpropeller.view.primitive.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.update.UpdateManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link Prop} with value of type {@link String}
 */
public class ColorPropEditor implements JView, ChangeListener {

	Prop<Color> model;
	private final Prop<Boolean> locked;

	
	JButton editButton;
	JPanel swatch;
	JPanel panel;
	
	boolean isEnabled = true;
	
	private final boolean editable;

	private UpdateManager updateManager;
	
	private ColorPropEditor(Prop<Color> model, Prop<Boolean> locked, boolean editable) {
		super();
		this.model = model;
		this.locked = locked;
		this.editable = editable;
		buildComponent();

		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		model.features().addListener(this);
		if (locked != null) {
			locked.features().addListener(this);
		}
		
		update();
	}

	/**
	 * Create a {@link ColorPropEditor}
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @return
	 * 		A new {@link ColorPropEditor}
	 */
	public final static ColorPropEditor create(Prop<Color> model) {
		return new ColorPropEditor(model, null, true);
	}
	
	/**
	 * Create a {@link ColorPropEditor}
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
	 * @return
	 * 		A new {@link ColorPropEditor}
	 */
	public final static ColorPropEditor create(Prop<Color> model, Prop<Boolean> locked) {
		return new ColorPropEditor(model, locked, true);
	}

	/**
	 * Create a {@link ColorPropEditor}
	 * @param model				The {@link Reference} for this {@link View} 
	 * @param locked			If this is non-null, the view will not support
	 * 							editing while its value is true.
	 * @param editable			True to enable editing
	 * @return					A new {@link ColorPropEditor}
	 */
	public final static ColorPropEditor create(Prop<Color> model,
			Prop<Boolean> locked, boolean editable) {
		return new ColorPropEditor(model, locked, editable);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		updateManager.updateRequiredBy(this);
	}
	
	@Override
	public void dispose() {
		model.features().removeListener(this);
		if (locked != null) {
			locked.features().removeListener(this);
		}
		updateManager.deregisterUpdatable(this);
	}

	/**
	 * Get the {@link JComponent} used for display/editing
	 * @return
	 * 		The editing {@link JComponent}
	 */
	public JComponent getComponent() {
		return panel;
	}
	
	private void buildComponent() {
		swatch = new JPanel();
		swatch.setBackground(null);
		swatch.setOpaque(true);
		swatch.setBorder(new MatteBorder(1,1,1,1,Color.BLACK));
		swatch.setMinimumSize(new Dimension(10, 20));
		swatch.setPreferredSize(new Dimension(10, 20));

		JPanel surround = new JPanel();
		surround.setLayout(new BorderLayout());
		surround.add(swatch);
		surround.setBorder(new EmptyBorder(3,2,3,2));
		
		surround.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				edit();
			}
		});
		
		if (!editable) {
			panel = surround;
			return;
		}
		
		editButton = new JButton("Edit...");
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		});
		
		FormLayout layout = new FormLayout("fill:16dlu:grow, $lcgap, pref", "fill:pref");
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		
		// Fill the grid with components
		builder.append(surround);
		builder.append(editButton);
		panel = builder.getPanel();
	}
	
	private void edit() {
		Color value = model.get();
		Color selected = JColorChooser.showDialog(panel, "Choose new color", value);
		if (selected != null && value != selected) {
			//TODO indicate error to user
			try {
				model.set(selected);
			} catch (ReadOnlyException e) {
				update();
			} catch (InvalidValueException e) {
				update();
			}

		}
	}
	
	@Override
	public void cancel() {
		//Nothing to do
	}

	@Override
	public void commit() {
		//Nothing to do
	}

	@Override
	public boolean isEditing() {
		//Never editing
		return false;
	}

	@Override
	public void update() {
		Color value = model.get();
		boolean nowNull = (value == null);
		
		if (nowNull) {
			value = Color.BLACK;
		}
		
		if (!swatch.getBackground().equals(value)) {
			swatch.setBackground(value);
		}
		
		boolean enabled = !nowNull && !Props.isTrue(locked);

		if (isEnabled != enabled && editButton!=null) {
			editButton.setEnabled(enabled);
			isEnabled = enabled;
		}
	}

	@Override
	public boolean selfNaming() {
		//Just displays value
		return false;
	}
	
	@Override
	public Format format() {
		return Format.SINGLE_LINE;
	}
}
