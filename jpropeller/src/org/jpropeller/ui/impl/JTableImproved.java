package org.jpropeller.ui.impl;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
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
		setDefaultEditor(Integer.class, NumberCellEditor.integerEditor());
		setDefaultEditor(Long.class, 	NumberCellEditor.longEditor());
		setDefaultEditor(String.class, 	SelectingTextCellEditor.editor());
		
		setDefaultEditor(Color.class, 	new ColorCellEditor());
		setDefaultEditor(Boolean.class, new BooleanCellEditor());
		
		setDefaultRenderer(ImmutableIcon.class, 	new IconCellRenderer());
		setDefaultRenderer(Color.class, 			new ColorCellRenderer());
		setDefaultRenderer(Boolean.class, 			BooleanCellRenderer.opaque());

		//Set sensible row height
		setRowHeight(26);
		
		//Thank you again Sun. Thank you for valuing 10 year old applications written
		//by idiots who rely on detailed broken implementations above poor honest
		//coders just trying to get the CURRENT version to actually WORK. Gee, I wonder
		//why you got bought out?
		//Please see Sun/Oracle/Whoever's bug parade of shame, entry number 4709394 of billions,
		//where the evaluation mentions this little hidden gem of a property. Followed by
		//a bitter discussion of why it should be on by default.
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		//FIXME if Sun ever fix bug 4330950 (edits lost when going from editing to
		//clicking a column header), and they do it with a similar optional "fix", then
		//after finishing swearing, enable that fix here as well, and get rid of the 
		//overrides on columnMoved and columnMarginChanged
	}
	
	//Workaround for bug 4330950, stops editing before starting to move column
    public void columnMoved(TableColumnModelEvent e) {
        if (isEditing()) {
            cellEditor.stopCellEditing();
        }
        super.columnMoved(e);
    }

	//Workaround for bug 4330950, stops editing before starting to change column
    public void columnMarginChanged(ChangeEvent e) {
        if (isEditing()) {
            cellEditor.stopCellEditing();
        }
        super.columnMarginChanged(e);
    }

}
