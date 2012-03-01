package org.jpropeller.ui.table;

import java.util.List;

import org.jpropeller.transformer.Transformer;

/**
 * Allows export of a specific class of data to a column-based format, with names (headers).
 */
public class ValueExporter {
	private final Class<?> leastSpecific;
	private final String[] subColumnNames;
	private final int numColumns;
	private final List<Transformer<Object, String>> transformers;
	
	/**
	 * Create a {@link ValueExporter}
	 * @param leastSpecific	The least specific class handled by this exporter - i.e. the superclass of all handled values.
	 * @param transformers	The transformers used to transform from an input object to the output string, one per subcolumn.
	 */
	public ValueExporter(final Class<?> leastSpecific, final List<Transformer<Object, String>> transformers) {
		this(leastSpecific, transformers, "");
	}
	
	/**
	 * Create a {@link ValueExporter}
	 * @param leastSpecific	The least specific class handled by this exporter - i.e. the superclass of all handled values.
	 * @param transformers	The transformers used to transform from an input object to the output string, one per subcolumn.
	 * @param subColumnNames	The names of subcolumns
	 */
	public ValueExporter(final Class<?> leastSpecific, final List<Transformer<Object, String>> transformers, String...subColumnNames) {
		this.leastSpecific = leastSpecific;
		this.transformers = transformers;
		this.numColumns = transformers.size();
		this.subColumnNames = subColumnNames;
	}
	
	/**
	 * Test whether a class can be exported
	 * @param clazz	The class to check
	 * @return	True if class can be exported, false otherwise
	 */
	public boolean canExport(Class<?> clazz) {
		return leastSpecific.isAssignableFrom(clazz);
	}
	
	/**
	 * Get the transformer for a given subcolumn
	 * @param subColumn	The index of the subcolumn
	 * @return	Transformer for given subcolumn
	 */
	public Transformer<Object, String> transformer(int subColumn) {
		return transformers.get(subColumn);
	}
	
	/**
	 * Get the name for a given subcolumn
	 * @param subColumn	The index of the subcolumn
	 * @return	Name (header) for given subcolumn
	 */
	public String subColumnName(int subColumn) {
		if(numColumns == 1) {
			throw new RuntimeException("Why are you asking for a sub-column name for a single column view?");
		}
		return subColumnNames[subColumn];
	}
	
	/**
	 * Number of subcolumns
	 * @return	Subcolumn count
	 */
	public int numColumns() {
		return numColumns;
	}
	
	/**
	 * True if there are multiple subcolumns
	 * @return True if there are multiple subcolumns
	 */
	public boolean multiColumn() {
		return numColumns > 1;
	}
}
