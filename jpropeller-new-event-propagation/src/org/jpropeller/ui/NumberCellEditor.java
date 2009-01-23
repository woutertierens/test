package org.jpropeller.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.NumberConverter;
import org.jpropeller.util.NumberConverterDefaults;

/**
 * Implements a {@link CellEditor} that uses a {@link JFormattedTextField} 
 * to edit {@link Number} values.
 */
public class NumberCellEditor extends DefaultCellEditor {

	private static Logger logger = GeneralUtils.logger(NumberCellEditor.class);
	
	private JFormattedTextField ftf;

	private NumberFormat numberFormat;

	private Comparable<?> minimum, maximum;

	private String requiredContentsDescription = "number";
	
	private NumberConverter<?> converter = null;
	
	private final static NumberCellEditor FLOAT = new NumberCellEditor(NumberFormat.getNumberInstance(), NumberConverterDefaults.getFloatConverter(), "a number");
	private final static NumberCellEditor DOUBLE = new NumberCellEditor(NumberFormat.getNumberInstance(), NumberConverterDefaults.getDoubleConverter(), "a number");
	
	private final static NumberCellEditor LONG = new NumberCellEditor(
			NumberFormat.getIntegerInstance(), 
			NumberConverterDefaults.getLongConverter(),
			
			//TODO find a way of getting the text field to reject edits that overflow a Long - 
			//currently, the overflow occurs BEFORE the number is checked against the limits, so these are no use
			//new Long(-9000000000000000000L),
			//new Long(9000000000000000000L),
			"a whole number");
	
	private final static NumberCellEditor INTEGER = new NumberCellEditor(
			NumberFormat.getIntegerInstance(), 
			NumberConverterDefaults.getIntegerConverter(),
			
			//TODO find a way of getting the text field to reject edits that overflow an Integer - 
			//currently, the overflow occurs BEFORE the number is checked against the limits, so these are no use
			//-2000000000,
			//2000000000,
			"a whole number");

	/**
	 * Get a shared instance of {@link NumberCellEditor} for editing {@link Float} values
	 * @return
	 * 		A shared {@link NumberCellEditor}
	 */
	public final static NumberCellEditor floatEditor() {
		return FLOAT;
	}

	/**
	 * Get a shared instance of {@link NumberCellEditor} for editing {@link Double} values
	 * @return
	 * 		A shared {@link NumberCellEditor}
	 */
	public final static NumberCellEditor doubleEditor() {
		return DOUBLE;
	}

	/**
	 * Get a shared instance of {@link NumberCellEditor} for editing {@link Integer} values
	 * @return
	 * 		A shared {@link NumberCellEditor}
	 */
	public final static NumberCellEditor integerEditor() {
		return INTEGER;
	}

	/**
	 * Get a shared instance of {@link NumberCellEditor} for editing {@link Long} values
	 * @return
	 * 		A shared {@link NumberCellEditor}
	 */
	public final static NumberCellEditor longEditor() {
		return LONG;
	}

	/**
	 * Create a {@link NumberCellEditor}
	 * @param numberFormat
	 * 		The required {@link NumberFormat} - this is used for the {@link JFormattedTextField}
	 * used to edit values. For example, this could be an integer format, to restrict
	 * user to entering valid integers.
	 * @param converter
	 * 		A converter used to ensure that the {@link Number}s returned by the cell editor 
	 * are the required type of {@link Number} - return values are converted using the
	 * {@link NumberConverter#toT(Number)} method. This may be null, in which case numbers
	 * are not converted. Note that this may not be what you want - the {@link JFormattedTextField}
	 * and/or {@link NumberFormat} may return {@link Number}s of a different class to that expected - 
	 * for example, returning a {@link Long} whenever the {@link Number} has no fractional part, etc. 
	 * @param requiredContentsDescription
	 * 		A description of what is required of the user in terms of inputs, to fit
	 * with this prompt:
	 * 		"The value must be " + requiredContentsDescription;
	 * Note that there may also be added text in the prompt to specify minimum and 
	 * maximum values. Examples would be "an integer", or "a whole number", etc.
	 */
	public NumberCellEditor(NumberFormat numberFormat, NumberConverter<?> converter, String requiredContentsDescription) {
		this(numberFormat, converter, null, null, requiredContentsDescription);
	}
	
	/**
	 * Create a number editor
	 * @param numberFormat
	 * 		The required {@link NumberFormat} - this is used for the {@link JFormattedTextField}
	 * used to edit values. For example, this could be an integer format, to restrict
	 * user to entering valid integers.
	 * @param converter
	 * 		A converter used to ensure that the {@link Number}s returned by the cell editor 
	 * are the required type of {@link Number} - return values are converted using the
	 * {@link NumberConverter#toT(Number)} method. This may be null, in which case numbers
	 * are not converted. Note that this may not be what you want - the {@link JFormattedTextField}
	 * and/or {@link NumberFormat} may return {@link Number}s of a different class to that expected - 
	 * for example, returning a {@link Long} whenever the {@link Number} has no fractional part, etc.
	 * @param minimum
	 * 		The minimum value for the formatted text field, or null for no restriction
	 * @param maximum
	 * 		The maximum value for the formatted text field, or null for no restriction
	 * @param requiredContentsDescription
	 * 		A description of what is required of the user in terms of inputs, to fit
	 * with this prompt:
	 * 		"The value must be " + requiredContentsDescription;
	 * Note that there may also be added text in the prompt to specify minimum and 
	 * maximum values. Examples would be "an integer", or "a whole number", etc.
	 */
	public NumberCellEditor(NumberFormat numberFormat, NumberConverter<?> converter, 
			Comparable<?> minimum, Comparable<?> maximum, 
			String requiredContentsDescription) {
		super(new JFormattedTextField());
		ftf = (JFormattedTextField) getComponent();

		this.numberFormat = numberFormat;
		this.converter = converter;
		this.minimum = minimum;
		this.maximum = maximum;
		this.requiredContentsDescription = requiredContentsDescription;

		// Set up the number format
		NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
		numberFormatter.setFormat(numberFormat);
		if (minimum != null) numberFormatter.setMinimum(minimum);
		if (maximum != null) numberFormatter.setMaximum(maximum);

		ftf.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));
		ftf.setValue(minimum);
		ftf.setHorizontalAlignment(JTextField.TRAILING);
		ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

		// React when the user presses Enter while the editor is
		// active. (Tab is handled as specified by
		// JFormattedTextField's focusLostBehavior property.)
		ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"check");
		ftf.getActionMap().put("check", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!ftf.isEditValid()) { // The text is invalid.
					if (userSaysRevert()) { // reverted
						ftf.postActionEvent(); // inform the editor
					}
				} else
					try { // The text is valid,
						ftf.commitEdit(); // so use it.
						ftf.postActionEvent(); // stop editing
					} catch (java.text.ParseException exc) {
						logger.severe("actionPerformed: can't parse: " + exc);
					}
			}
		});
	}

	// Override to invoke setValue on the formatted text field.
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		JFormattedTextField ftf = 
			(JFormattedTextField) super.getTableCellEditorComponent(
					table, value, isSelected, row, column);
		ftf.setValue(value);
		
		//Select all text for sane editing behaviour - contents are replaced if
		//the user starts an edit by typing, editing proceeds as normal if they 
		//double click
		ftf.selectAll();
		return ftf;
	}

	// Override to ensure that the value remains valid.
	public Object getCellEditorValue() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		Object o = ftf.getValue();
		try {
			Object parsed = numberFormat.parseObject(o.toString());
			return Convert((Number)parsed);
		} catch (ParseException exc) {
			logger.severe("getCellEditorValue: can't parse o: " + o);
			return null;
		} catch (ClassCastException exc) {
			logger.severe("getCellEditorValue: non-Number from numberFormat on " + o);
			return null;
		}
	}

	private Object Convert(Number n) {
		if (converter == null) {
			return n;
		} else {
			return converter.toT(n);
		}
	}
	
	// Override to check whether the edit is valid,
	// setting the value if it is and complaining if
	// it isn't. If it's OK for the editor to go
	// away, we need to invoke the superclass's version
	// of this method so that everything gets cleaned up.
	public boolean stopCellEditing() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		if (ftf.isEditValid()) {
			try {
				ftf.commitEdit();
			} catch (java.text.ParseException exc) {
			}

		} else { // text is invalid
			if (!userSaysRevert()) { // user wants to edit
				return false; // don't let the editor go away
			}
		}
		return super.stopCellEditing();
	}

	/**
	 * Lets the user know that the text they entered is bad. Returns true if the
	 * user elects to revert to the last good value. Otherwise, returns false,
	 * indicating that the user wants to continue editing.
	 */
	protected boolean userSaysRevert() {
		Toolkit.getDefaultToolkit().beep();
		ftf.selectAll();
		
		String prompt = "The value must be " + requiredContentsDescription;
		if (minimum != null) prompt +=  ", between " + minimum;
		if (maximum != null) prompt +=  " and " + maximum + ", inclusive";
		prompt += ".\n";
		prompt += "You can either continue editing or revert to the last valid value.";

		
		Object[] options = { "Edit", "Revert" };
		int answer = JOptionPane.showOptionDialog(SwingUtilities
				.getWindowAncestor(ftf),
				prompt,
				"Invalid Text Entered", JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE, null, options, options[1]);

		if (answer == 1) { // Revert!
			ftf.setValue(ftf.getValue());
			return true;
		}
		return false;
	}
}