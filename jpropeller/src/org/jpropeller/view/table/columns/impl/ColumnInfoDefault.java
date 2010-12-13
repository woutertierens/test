package org.jpropeller.view.table.columns.impl;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.view.table.columns.ColumnInfo;

/**
 * Default implementation of {@link ColumnInfo}
 */
public class ColumnInfoDefault implements ColumnInfo {

	private final int modelIndex;
	private final int preferredWidth;
	private final int minWidth;
	private final int maxWidth;
	private final TableCellEditor editor;
	private final TableCellRenderer renderer;

	/**
	 * Create a {@link ColumnInfoDefault}
	 * @param modelIndex		See {@link #modelIndex()}
	 * @param width				Used for {@link #preferredWidth()}, {@link #minWidth()}, {@link #maxWidth()}
	 */
	public ColumnInfoDefault(int modelIndex, int width) {
		this(modelIndex, width, null, null);
	}
	
	/**
	 * Create a {@link ColumnInfoDefault}
	 * @param modelIndex		See {@link #modelIndex()}
	 * @param preferredWidth	See {@link #preferredWidth()}
	 * @param minWidth			See {@link #minWidth()}
	 * @param maxWidth			See {@link #maxWidth()}
	 */
	public ColumnInfoDefault(int modelIndex, int preferredWidth,
			int minWidth, int maxWidth) {
		this(modelIndex, preferredWidth, minWidth, maxWidth, null, null);
	}

	/**
	 * Create a {@link ColumnInfoDefault}
	 * @param modelIndex		See {@link #modelIndex()}
	 * @param width				Used for {@link #preferredWidth()}, {@link #minWidth()}, {@link #maxWidth()}
	 * @param editor			The editor for this column, or null to leave default in place 
	 * @param renderer 			The renderer for this column, or null to leave default in place
	 */
	public ColumnInfoDefault(int modelIndex, int width, TableCellEditor editor, TableCellRenderer renderer) {
		this(modelIndex, width, width, width, editor, renderer);
	}
	
	/**
	 * Create a {@link ColumnInfoDefault}
	 * @param modelIndex		See {@link #modelIndex()}
	 * @param preferredWidth	See {@link #preferredWidth()}
	 * @param minWidth			See {@link #minWidth()}
	 * @param maxWidth			See {@link #maxWidth()}
	 * @param editor			The editor for this column, or null to leave default in place 
	 * @param renderer 			The renderer for this column, or null to leave default in place
	 */
	public ColumnInfoDefault(int modelIndex, int preferredWidth,
			int minWidth, int maxWidth, TableCellEditor editor, TableCellRenderer renderer) {
		super();
		this.modelIndex = modelIndex;
		this.preferredWidth = preferredWidth;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.editor = editor;
		this.renderer = renderer;
	}
	
	@Override
	public int maxWidth() {
		return maxWidth;
	}

	@Override
	public int minWidth() {
		return minWidth;
	}

	@Override
	public int modelIndex() {
		return modelIndex;
	}

	@Override
	public int preferredWidth() {
		return preferredWidth;
	}

	@Override
	public TableCellEditor editor() {
		return editor;
	}
	
	@Override
	public TableCellRenderer renderer() {
		return renderer;
	}
}
