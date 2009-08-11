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
import org.jpropeller.view.maplist.impl.MapListReference;
import org.jpropeller.view.maplist.impl.MapListTableModel;
import org.jpropeller.view.table.impl.TableRowViewDirect;

/**
 * Demonstrate display of a map of lists using {@link MapListTableModel}
 * and {@link MapListReference}
 */
public class MapListDemo {

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
		
		MapListReference<Integer, String, StringList> ref = MapListReference.create(m.map(), m.keys());
		
		MapListTableModel<Integer, String, StringList> tableModel = new MapListTableModel<Integer, String, StringList>(ref, new TableRowViewDirect<String>(String.class, "String"));
		
		JTable table = new JTable(tableModel);
		
		JScrollPane scroll = new JScrollPane(table);
		
		JFrame frame = new JFrame("Map List Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(scroll);
		frame.pack();
		frame.setVisible(true);
	}
	
}
