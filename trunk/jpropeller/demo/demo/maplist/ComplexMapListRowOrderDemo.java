package demo.maplist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.ui.impl.Tabs;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Source;
import org.jpropeller.view.list.impl.ListEditView;
import org.jpropeller.view.maplist.impl.MapListTableModelRowOrder;
import org.jpropeller.view.table.impl.BeanRowView;
import org.jpropeller.view.table.impl.TableRowViewDirect;

import test.example.LotsOfProps;

import com.jgoodies.forms.factories.Borders;

/**
 * Demonstrate display of a map of lists using {@link MapListTableModelRowOrder}
 */
public class ComplexMapListRowOrderDemo {

	private final static Source<LotsOfProps> source = new Source<LotsOfProps>() {
		int i = 0;
		@Override
		public LotsOfProps get() {
			
			LotsOfProps p = new LotsOfProps();
			p.stringProp().set("Item " + i);
			p.booleanProp().set(i%2==0);
			p.intProp().set(i);
			p.longProp().set(100l + i);
			p.floatProp().set(i + 1/10f);
			p.doubleProp().set(i + i/10d + i/100d);
			p.colorProp().set(new Color(Color.HSBtoRGB((i-0.5f)/20f, 1, 1f)));
			
			i++;
			return p;
		}
	};
	
	/**
	 * A {@link CList} of {@link LotsOfProps}
	 */
	static class LotsList extends CListDefault<LotsOfProps> {
		/**
		 * Create a {@link LotsList}
		 */
		public LotsList() {
			super();
		}
	}
	
	static class MapAndKeys extends BeanDefault {
		Prop<CList<Integer>> keys = editableList(Integer.class, "keys");
		Prop<CMap<Integer, LotsList>> map = editableMap(Integer.class, LotsList.class, "map");
		
		/**
		 * Get keys
		 * @return	keys
		 */
		public Prop<CList<Integer>> keys() {return keys;}
		
		/**
		 * Get map
		 * @return	map
		 */
		public Prop<CMap<Integer, LotsList>> map() {return map;}
	}
	
	private static LotsList makeBeanList() {
		final LotsList l = new LotsList();
		for (int i = 0; i < 20; i++) {
			l.add(source.get());
		}
		return l;
	}
	
	private static JPanel dialogSurround(JComponent c) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(c);
		panel.setBorder(Borders.DIALOG_BORDER);
		return panel;
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
		
		GeneralUtils.enableConsoleLogging();
		GeneralUtils.enableNimbus();
		
		LotsOfProps example = new LotsOfProps();
		
		final LotsList l = makeBeanList();
		final LotsList l2 = makeBeanList();

		ListEditView<LotsOfProps> view = ListEditView.create(l, LotsOfProps.class, new BeanRowView(example), source, true);
		ListEditView<LotsOfProps> view2 = ListEditView.create(l2, LotsOfProps.class, new BeanRowView(example), source, true);

		Tabs tabs = new Tabs();
		JTabbedPane tabbedPane = tabs.getComponent();
		
		tabbedPane.insertTab("list 1", null, dialogSurround(view.getComponent()), "The first list", 0);
		tabbedPane.insertTab("list 2", null, dialogSurround(view2.getComponent()), "The second list", 1);
		
		MapAndKeys m = new MapAndKeys();
		
		for (int i = 0; i < 3; i++) {
			m.keys().get().add(i);
		}
		
		ListEditView<Integer> keysView = ListEditView.create(m.keys.get(), Integer.class, new TableRowViewDirect<Integer>(Integer.class, "Key"), null, true);
		
		m.map().get().put(0, l);
		m.map().get().put(1, l2);
		
		BeanRowView rowView = new BeanRowView(example);
		
		MapListTableModelRowOrder<Integer, LotsOfProps, LotsList> tableModel = 
			new MapListTableModelRowOrder<Integer, LotsOfProps, LotsList>(m.map(), m.keys(), rowView, true, "Key", Integer.class, true, "Index", 0);
		
		JTable table = new JTableImproved(tableModel);
		
		JScrollPane scroll = new JScrollPane(table);

		JPanel main = new JPanel(new GridLayout(1, 2, 5, 5));
		main.add(tabs.getComponent());
		main.add(scroll);
		
		JFrame frame = new JFrame("Map List Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(main);
		frame.pack();
		frame.setVisible(true);
		
		JFrame keysFrame = new JFrame("Keys");
		keysFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		keysFrame.add(keysView.getComponent());
		keysFrame.pack();
		keysFrame.setVisible(true);

	}
	
}
