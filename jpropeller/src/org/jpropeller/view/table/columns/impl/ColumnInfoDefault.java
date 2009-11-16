package org.jpropeller.view.table.columns.impl;

import org.jpropeller.view.table.columns.ColumnInfo;

/**
 * Default implementation of {@link ColumnInfo}
 */
public class ColumnInfoDefault implements ColumnInfo {

	private final int modelIndex;
	private final int preferredWidth;
	private final int minWidth;
	private final int maxWidth;

	/**
	 * Create a {@link ColumnInfoDefault}
	 * @param modelIndex		See {@link #modelIndex()}
	 * @param preferredWidth	See {@link #preferredWidth()}
	 * @param minWidth			See {@link #minWidth()}
	 * @param maxWidth			See {@link #maxWidth()}
	 */
	public ColumnInfoDefault(int modelIndex, int preferredWidth,
			int minWidth, int maxWidth) {
		super();
		this.modelIndex = modelIndex;
		this.preferredWidth = preferredWidth;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
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

}
