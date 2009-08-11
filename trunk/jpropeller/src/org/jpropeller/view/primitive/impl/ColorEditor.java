package org.jpropeller.view.primitive.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;
import org.jpropeller.view.UpdatableSingleValueView;
import org.jpropeller.view.View;
import org.jpropeller.view.impl.PropViewHelp;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * An editing {@link View} for an {@link Bean}, displaying and editing
 * the value of an {@link Prop} with value of type {@link String}
 */
public class ColorEditor implements JView, UpdatableSingleValueView<Bean> {

	PropViewHelp<Bean, Color> help;

	Reference<? extends Bean> model;
	PropName<Color> displayedName;
	
	JButton editButton;
	JPanel swatch;
	JPanel panel;
	
	boolean isNull = true;
	
	private ColorEditor(Reference<? extends Bean> model,
			PropName<Color> displayedName) {
		super();
		this.model = model;
		this.displayedName = displayedName;
		buildComponent();
		
		help = new PropViewHelp<Bean, Color>(this, displayedName);
		help.connect();
	}

	/**
	 * Create a {@link ColorEditor}
	 * @param model
	 * 		The {@link Reference} for this {@link View} 
	 * @param displayedName 
	 * 		The name of the displayed property 
	 * @return
	 * 		A new {@link ColorEditor}
	 */
	public final static ColorEditor create(Reference<? extends Bean> model,
			PropName<Color> displayedName) {
		return new ColorEditor(model, displayedName);
	}

	@Override
	public void dispose() {
		help.dispose();
	}

	@Override
	public Reference<? extends Bean> getModel() {
		return model;
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

		JPanel surround = new JPanel();
		surround.setLayout(new BorderLayout());
		surround.add(swatch);
		surround.setBorder(new EmptyBorder(3,2,3,0));
		
		surround.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				edit();
			}
		});
		
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
		Color value = help.getPropValue();
		Color selected = JColorChooser.showDialog(panel, "Choose new color", value);
		if (selected != null) {
			//TODO indicate error to user
			try {
				help.setPropValue(selected);
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
		Color value = help.getPropValue();

		if (value == null) {
			if (!isNull) {
				swatch.setBackground(Color.BLACK);
				editButton.setEnabled(false);
				isNull = true;
			}
		} else {
			//If not already showing prop value, update it
			if (isNull || (!swatch.getBackground().equals(value))) {
				editButton.setEnabled(true);
				swatch.setBackground(value);
			}
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
