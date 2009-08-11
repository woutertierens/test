package org.jpropeller.ui.impl;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A {@link JTable} with improvements (mostly improved
 * editors and displays for standard types)
 */
public class JTableImproved extends JTable {

    /**
     * Constructs a default <code>JTable</code> that is initialized with a default
     * data model, a default column model, and a default selection
     * model.
     *
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
	public JTableImproved() {
		super();
		init();
	}

    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, a default column model,
     * and a default selection model.
     *
     * @param dm        the data model for the table
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
	public JTableImproved(TableModel dm) {
		super(dm);
		init();
	}

    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code>
     * as the column model, and a default selection model.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @see #createDefaultSelectionModel
     */
	public JTableImproved(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		init();
	}

    /**
     * Constructs a <code>JTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code> as the
     * column model, and <code>sm</code> as the selection model.
     * If any of the parameters are <code>null</code> this method
     * will initialize the table with the corresponding default model.
     * The <code>autoCreateColumnsFromModel</code> flag is set to false
     * if <code>cm</code> is non-null, otherwise it is set to true
     * and the column model is populated with suitable
     * <code>TableColumns</code> for the columns in <code>dm</code>.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @param sm        the row selection model for the table
     * @see #createDefaultDataModel
     * @see #createDefaultColumnModel
     * @see #createDefaultSelectionModel
     */
	public JTableImproved(TableModel dm, TableColumnModel cm,
			ListSelectionModel sm) {
		super(dm, cm, sm);
		init();
	}

	private void init() {
		//Improved editors and renderers
		setDefaultEditor(Double.class, 	NumberCellEditor.doubleEditor());
		setDefaultEditor(Float.class, 	NumberCellEditor.floatEditor());
		setDefaultEditor(Integer.class, 	NumberCellEditor.integerEditor());
		setDefaultEditor(Long.class, 		NumberCellEditor.longEditor());
		setDefaultEditor(String.class, 	SelectingTextCellEditor.editor());
		setDefaultEditor(Color.class, 	new ColorCellEditor());

		setDefaultRenderer(ImmutableIcon.class, 	new IconCellRenderer());
		setDefaultRenderer(Color.class, 			new ColorCellRenderer());

		//Set sensible row height
		setRowHeight(26);
	}
}
