package org.jpropeller.ui.table;

import java.util.List;

import org.jpropeller.transformer.Transformer;

public class ValueExporter {
	private final Class<?> leastSpecific;
	private final String[] subColumnNames;
	private final int numColumns;
	private final List<Transformer<Object, String>> transformers;
	
	public ValueExporter(final Class<?> leastSpecific, final List<Transformer<Object, String>> transformers) {
		this(leastSpecific, transformers, "");
	}
	
	public ValueExporter(final Class<?> leastSpecific, final List<Transformer<Object, String>> transformers, String...subColumnNames) {
		this.leastSpecific = leastSpecific;
		this.transformers = transformers;
		this.numColumns = transformers.size();
		this.subColumnNames = subColumnNames;
	}
	
	public boolean canExport(Class<?> clazz) {
		return leastSpecific.isAssignableFrom(clazz);
	}
	public Transformer<Object, String> transformer(int subColumn) {
		return transformers.get(subColumn);
	}
	
	public String subColumnName(int subColumn) {
		if(numColumns == 1) {
			throw new RuntimeException("Why are you asking for a sub-column name for a single column view?");
		}
		return subColumnNames[subColumn];
	}
	
	public int numColumns() {
		return numColumns;
	}
	
	public boolean multiColumn() {
		return numColumns > 1;
	}
}
