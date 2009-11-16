package org.jpropeller.view.table.columns.impl;

import java.util.LinkedList;

import javax.swing.table.TableModel;

import org.jpropeller.view.table.columns.ColumnInfo;
import org.jpropeller.view.table.columns.ColumnLayout;

/**
 * The simplest {@link ColumnLayout}, just maps each model column
 * to an output column in standard ordering, with min width 0, max width
 * 10000, preferred width 100.
 */
public class ColumnLayoutDirect implements ColumnLayout {

	@Override
	public Iterable<ColumnInfo> layout(TableModel model) {
		int columnCount = model.getColumnCount();
		LinkedList<ColumnInfo> list = new LinkedList<ColumnInfo>();
		for (int i = 0; i < columnCount; i++) {
			list.add(new ColumnInfoDefault(i, 100, 0, 10000));
		}
		
		return list;
	}

}
