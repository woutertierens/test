package org.jpropeller.ui.impl;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * A {@link CellEditor} using a {@link JComboBox} to choose
 * an {@link Enum} value from an {@link Iterable} of that {@link Enum}'s
 * values
 */
public class EnumCellEditor extends DefaultCellEditor {

	/**
	 * Create an {@link EnumCellEditor}
	 * @param enumValues	The values of {@link Enum} to be chosen between
	 * @return				An {@link EnumCellEditor}
	 */
	public static EnumCellEditor create(Iterable<? extends Enum<?>> enumValues) {
		JComboBox box = new JComboBox();
		for (Enum<?> e : enumValues) {
			box.addItem(e);
		}
		return new EnumCellEditor(box);
	}
	
	/**
	 * Create an {@link EnumCellEditor}
	 * @param enumValues	The values of {@link Enum} to be chosen between
	 * @return				An {@link EnumCellEditor}
	 */
	public static EnumCellEditor create(Enum<?>[] enumValues) {
		JComboBox box = new JComboBox();
		for (Enum<?> e : enumValues) {
			box.addItem(e);
		}
		return new EnumCellEditor(box);
	}
	
	private EnumCellEditor(JComboBox box) {
		super(box);
	}

}
