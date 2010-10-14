package demo.list;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Source;
import org.jpropeller.view.combo.impl.ListComboBoxModel;
import org.jpropeller.view.table.impl.BeanRowView;
import org.jpropeller.view.table.impl.DropDownListView;

import test.example.LotsOfProps;

/**
 * Simple demonstration of {@link ListComboBoxModel}
 */
public class DropDownListViewDemo {

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
	 * Run demonstration
	 * @param args
	 */
	public static void main(String[] args) {
		GeneralUtils.enableNimbus();

		final CList<LotsOfProps> l = makeBeanList();

		final LotsOfProps example = new LotsOfProps();
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				
				JFrame frame = new JFrame("List Popup Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Prop<CList<LotsOfProps>> lProp = Props.editableList(LotsOfProps.class, "lProp", l);
				BeanRowView<Bean> rowView = new BeanRowView<Bean>(example);
				DropDownListView<LotsOfProps> view = new DropDownListView<LotsOfProps>(LotsOfProps.class, lProp, rowView, 400, 400, false);

				JPanel panel = new JPanel(new FlowLayout());
				panel.add(new JButton("Other"));
				panel.add(view.getComponent());
				panel.add(new JButton("Buttons"));
				
				frame.add(panel);
				
				frame.pack();
				frame.setVisible(true);
				
			}
		});
		
	}

	private static CList<LotsOfProps> makeBeanList() {
		final CList<LotsOfProps> l = new CListDefault<LotsOfProps>();
		for (int i = 0; i < 20; i++) {
			l.add(source.get());
		}
		return l;
	}
	
}
