package demo.maplist;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.maplist.impl.MapListTableModelColumnOrder;
import org.jpropeller.view.table.impl.TableRowViewDirect;

/**
 * Demonstrate display of a map of lists using {@link MapListColumnOrderDemo}
 */
public class MapListColumnOrderDemo {

	/**
	 * A {@link CList} of {@link String}
	 */
	static class StringList extends CListDefault<String> {
		/**
		 * Create a {@link StringList}
		 */
		public StringList() {
			super();
		}
	}
	
	static class MapAndKeys extends BeanDefault {
		Prop<CList<Integer>> keys = editableList(Integer.class, "keys");
		Prop<CMap<Integer, StringList>> map = editableMap(Integer.class, StringList.class, "map");
		
		/**
		 * Get keys
		 * @return	keys
		 */
		public Prop<CList<Integer>> keys() {return keys;}
		
		/**
		 * Get map
		 * @return	map
		 */
		public Prop<CMap<Integer, StringList>> map() {return map;}
	}
	
	/**
	 * Run demo
	 * @param args		Ignored
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp();
			}
		});
		
	}
	
	static void runApp() {
		MapAndKeys m = new MapAndKeys();
		
		for (int i = 0; i < 5; i++) {
			m.keys().get().add(i);
		}
		
		for (int i = 0; i < 5; i++) {
			StringList list = new StringList();
			for (int j = 0; j < 3; j++) {
				list.add("StringList " + i + ", entry " + j);
			}
			m.map().get().put(i, list);
		}
		
		MapListTableModelColumnOrder<Integer, String, StringList> tableModel = 
			new MapListTableModelColumnOrder<Integer, String, StringList>(
					m.map(), m.keys(), 
					new TableRowViewDirect<String>(String.class, "String"));
		
		System.out.println("Rows x columns: " + tableModel.getRowCount() + ", " + tableModel.getColumnCount());
		
		JTable table = new JTableImproved(tableModel);
		
		JScrollPane scroll = new JScrollPane(table);
		
		JFrame frame = new JFrame("Map List Column Order Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scroll);
		frame.pack();
		frame.setVisible(true);
	}
	
}
