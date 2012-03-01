package org.jpropeller.ui.table;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;

import org.jpropeller.transformer.Transformer;

/**
 * Exports tables to CSV format
 */
public class TableExporter {
	
	private final List<ValueExporter> defaultExporters;
	private final List<ValueExporter> userExporters;
	private final ValueExporter fallback;
	
	/**
	 * Create a {@link TableExporter}
	 */
	@SuppressWarnings("unchecked")
	public TableExporter() {
		defaultExporters = new LinkedList<ValueExporter>();
		userExporters = new LinkedList<ValueExporter>();
		
		fallback = new ValueExporter(Object.class, Arrays.asList(TOSTRING_TRANSFORMER));
		
		defaultExporters.add(new ValueExporter(Number.class, Arrays.asList(NUMBER_TRANSFORMER)));
		defaultExporters.add(new ValueExporter(BigDecimal.class, Arrays.asList(NUMBER_TRANSFORMER)));
		defaultExporters.add(new ValueExporter(BigInteger.class, Arrays.asList(NUMBER_TRANSFORMER)));
		
		defaultExporters.add(new ValueExporter(Color.class, Arrays.asList(COLOR_TRANSFORMER)));
		
	}
	
	/**
	 * Add a user defined {@link ValueExporter}
	 * @param exporter	The {@link ValueExporter}
	 */
	public void addUserExporter(ValueExporter exporter) {
		userExporters.add(exporter);
	}
	
	/**
	 * Export a table
	 * @param table		The table whose data will be exported
	 * @param to		The {@link Writer} to accept data from table
	 */
	public void exportTable(final JTable table, final Writer to) {
		PrintWriter out = new PrintWriter(to);
		
		final List<ValueExporter> transformers = new ArrayList<ValueExporter>(table.getColumnCount());
		
		for(int col=0; col<table.getColumnCount(); col++) {
			final Class<?> clazz = table.getColumnClass(col);
			transformers.add(exporterFor(clazz));
		}
		
		printHeader(table, out, transformers);
		
		for(int row=0; row<table.getRowCount(); row++) {
			printRow(table, out, transformers, row);
		}
		
		out.flush();
		
	}

	private void printRow(final JTable table, PrintWriter out,
			final List<ValueExporter> transformers, int row) {
		
		for(int col=0; col<table.getColumnCount()-1; col++) {
			printItem(table, out, transformers, row, col);
			out.print(",");
		}
		final int col = table.getColumnCount()-1;
		printItem(table, out, transformers, row, col);
		out.print("\n");
	}

	private void printItem(final JTable table, PrintWriter out,
			final List<ValueExporter> transformers, int row,
			int col) {
		final ValueExporter transformer = transformers.get(col);
		final Object obj = table.getValueAt(row, col);
		for(int i=0; i<transformer.numColumns(); i++) {
			if(obj != null) {
				out.print(sanitize(transformer.transformer(i).transform(obj)));
			}
			if(!last(i, transformer.numColumns())) {
				out.print(",");
			}
		}
	}

	private void printHeader(final JTable table, PrintWriter out, List<ValueExporter> transformers) {
		for(int col=0; col<table.getColumnCount(); col++) {
			final ValueExporter valueExporter = transformers.get(col);
			final String title = table.getColumnName(col);
			if(valueExporter.multiColumn()) {
				for(int i=0; i<valueExporter.numColumns(); i++) {
					out.print(sanitize(title + " [" + valueExporter.subColumnName(i) + "]"));
					out.print(sep(last(col, table.getColumnCount()) && last(i, valueExporter.numColumns())));
				}
			} else {
				out.print(sanitize(title));
				out.print(sep(last(col, table.getColumnCount())));
			}
		}		
	}

	private boolean last(int col, int columnCount) {
		return col == (columnCount-1);
	}
	
	private String sep(boolean last) {
		return last ? "\n" : ",";
	}

	private static String sanitize(String dirty) {
		if(dirty == null) return "";
		return "\"" + dirty.replaceAll("[\"]", "\"\"") + "\"";
	}

	/**
	 * Get the exported for a class
	 * @param clazz		The class
	 * @return			An exporter for the class, by preference a user exporter, then
	 * 					a default exporter, then finally a fallback if nothing else is available.
	 */
	public ValueExporter exporterFor(Class<?> clazz) {
		for(final ValueExporter exporter : userExporters) {
			if(exporter.canExport(clazz)) {
				return exporter;
			}
		}
		for(final ValueExporter exporter : defaultExporters) {
			if(exporter.canExport(clazz)) {
				return exporter;
			}
		}
		return fallback;
	}

	/**
	 * Default transformer for {@link Color}s
	 */
	public final static Transformer<Object, String> COLOR_TRANSFORMER = new Transformer<Object, String>() {
		@Override
		public String transform(Object s) {
			Color c = (Color)s;
			String rHex = byteString(c.getRed());
			String gHex = byteString(c.getGreen());
			String bHex = byteString(c.getBlue());
			return "#" + rHex + gHex + bHex;	
		}

		private String byteString(int color) {
			String hex = Integer.toHexString((color & 0xff));
			if(hex.length() == 1) {
				return "0" + hex;
			} else {
				return hex;
			}
			
		}
	};
	
	/**
	 * Default transformer for {@link Color}s
	 */
	public final static Transformer<Object, String> TOSTRING_TRANSFORMER = new Transformer<Object, String>() {
		@Override
		public String transform(Object s) {
			return "" + s;
		}
	};
	
	/**
	 * Default transformer for numbers
	 */
	public final static Transformer<Object, String> NUMBER_TRANSFORMER = new Transformer<Object, String>() {
		private final NumberFormat NUMBER_FORMAT = new DecimalFormat();
		
		@Override
		public String transform(Object s) {
			return NUMBER_FORMAT.format(s);
		}
	};
}
