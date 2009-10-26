package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Provides a {@link JPanel} allowing selection of one
 * of a list of colors
 */
public class ColorPaletteChooser implements ActionListener {

	private final JPanel panel;
	private final Map<Object, Color> buttonToColor;
	private Color selectedColor = null;
	private Color previousColor = Color.BLACK;
	private final JTabButton previousButton;
	private final JTabButton selectedButton;
	
	/**
	 * Create a chooser
	 * @param columns	Number of columns to display in color grid
	 * @param colors	List of colors to display
	 */
	public ColorPaletteChooser(int columns, List<Color> colors) {
		
		if (columns < 1) columns = 1;
		int count = colors.size();

		buttonToColor = new HashMap<Object, Color>(count);
		
		JPanel swatchPanel = new JPanel(new GridLayout(0, columns, 5, 5));

		ButtonGroup group = new ButtonGroup();
		
		for (Color c : colors) {
			JTabButton button = JTabButton.createToggle(" ");
			button.setBackground(c);
			group.add(button);
			swatchPanel.add(button);
			buttonToColor.put(button, c);
			button.addActionListener(this);
		}
		
		previousButton = JTabButton.createToggle(" ");
		previousButton.setBackground(previousColor);
		previousButton.addActionListener(this);
		group.add(previousButton);
		group.setSelected(previousButton.getModel(), true);

		selectedButton = JTabButton.createToggle(" ");
		selectedButton.setBackground(previousColor);
		selectedButton.setEnabled(false);

		//Build the entire panel
		FormLayout layout = new FormLayout(
				"fill:pref:grow",				
				"pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref"
				);
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();
		
		builder.addSeparator("Color Palette", 	cc.xy(1, 1));
		builder.add(swatchPanel, 				cc.xy(1, 3));
		builder.addSeparator("Previous", 		cc.xy(1, 5));
		builder.add(previousButton, 			cc.xy(1, 7));
		builder.addSeparator("Selected", 		cc.xy(1, 9));
		builder.add(selectedButton, 			cc.xy(1, 11));
		
		panel = builder.getPanel();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == previousButton) {
			selectedColor = previousColor;
		} else {
			Color c = buttonToColor.get(e.getSource());
			if (c != null) {
				selectedColor = c;
			}
		}
		
		if (selectedColor != null) {
			selectedButton.setBackground(selectedColor);
			selectedButton.repaint();
		}
	}

	/**
	 * Get the panel itself
	 * @return	panel
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Get the currently selected color, null if none is selected
	 * @return	selected color
	 */
	public Color getSelectedColor() {
		return selectedColor;
	}
	
	/**
	 * Set color to be displayed as "Previous" color in
	 * button
	 * @param c		The color
	 */
	public void setPreviousColor(Color c) {
		previousColor = c;
		previousButton.setBackground(c);
		previousButton.repaint();
		previousButton.setSelected(true);
		selectedButton.setBackground(c);
		selectedButton.repaint();
	}

	/**
	 * Make a default {@link ColorPaletteChooser}
	 * @return	default chooser
	 */
	public final static ColorPaletteChooser create() {
		List<Color> colors = makeDefaultPalette();
		return new ColorPaletteChooser(8, colors);
	}
	
	/**
	 * Make a default palette of colors in different
	 * hues and brightness/saturation
	 * @return	A default palette
	 */
	public static List<Color> makeDefaultPalette() {
		List<Color> colors = new LinkedList<Color>();
		
		int variations = 4;
		int shades = 8;
		for(int var=0; var<variations; var++) {
			if(var == 0) {
				for(int sh=0; sh<shades; sh++) {
					float shFrac = (shades-sh)/(float)shades;
					colors.add(new Color(shFrac, shFrac, shFrac));
				}
			} else {
				float saturation = (1+var)/(float)(variations);
				float brightness = 1;
				if (var == 1) {
					saturation = 1f;
					brightness = 1f;
				} else if (var == 2){
					saturation = 0.3f;
					brightness = 0.8f;
				} else if (var == 3){
					saturation = 0.8f;
					brightness = 0.4f;					
				}
				for(int sh=0; sh<shades; sh++) {
					float shFrac = sh/(float)shades;
					colors.add(Color.getHSBColor(shFrac, saturation, brightness));
				}
			}
		}	
		
		return colors;
	}

	
}
