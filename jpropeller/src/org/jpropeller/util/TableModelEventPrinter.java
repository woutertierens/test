package org.jpropeller.util;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TableModelEvent;

/**
 * {@link TableModelEvent} does not have a very helpful {@link TableModelEvent#toString()}
 * implementation, so this provides one.
 */
public class TableModelEventPrinter {

	private final static Map<Integer, String> TYPE_NAMES = new HashMap<Integer, String>();
	static {
		TYPE_NAMES.put(TableModelEvent.INSERT, "insert");
		TYPE_NAMES.put(TableModelEvent.UPDATE, "update");
		TYPE_NAMES.put(TableModelEvent.DELETE, "delete");
	}

	/**
	 * Print a {@link TableModelEvent} to a helpful string
	 * @param e		The {@link TableModelEvent}
	 * @return		String description.
	 */
	public static String toString(TableModelEvent e) {
		String columnString = e.getColumn() == TableModelEvent.ALL_COLUMNS ? "all columns" : "column " + e.getColumn();
		
		String scopeString = "";
		//This represents a change equivalent to setting a new table model - 
		//column structure has changed, and so essentially everything has.
		if (e.getColumn() == TableModelEvent.ALL_COLUMNS && e.getFirstRow() == TableModelEvent.HEADER_ROW) {
			scopeString = " entire model (column structure and rows)";
			
		//This represents a change to header row - not sure whether this can
		//ever happen without ALL_COLUMNS as well, and indeed not sure what
		//this would mean
		} else if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
			scopeString = columnString + ", header row (-1 to " + e.getLastRow() + ")";
			
		//This represents a change where the number of rows may have changed, and
		//any row may have changed
		} else if (e.getFirstRow() == 0 && e.getLastRow() == 2147483647) {
			scopeString = columnString + ", all rows (0 to int max), row count may have changed";
			
		//This represents a change where the row count is still the same, but a range
		//of rows may have changed contents (up to and including all rows).
		//Column structure has not changed
		} else {
			scopeString = columnString + ", rows " + e.getFirstRow() + " to " + e.getLastRow();
		}
		return TYPE_NAMES.get(e.getType()) + " on " + scopeString; 
	}
	
}
