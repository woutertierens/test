package demo.view;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Source;
import org.jpropeller.view.combo.impl.ListComboBoxModel;
import org.jpropeller.view.combo.impl.ListComboView;
import org.jpropeller.view.list.impl.ListEditView;
import org.jpropeller.view.table.impl.BeanRowView;

import test.example.LotsOfProps;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Simple demonstration of {@link ListComboBoxModel}
 */
public class ComboModelDemo {

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
		final CList<LotsOfProps> l = makeBeanList();

		final LotsOfProps example = new LotsOfProps();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				GeneralUtils.enableNimbus();
				
				JFrame frame = new JFrame("Combo Model Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				ListEditView<LotsOfProps> view = ListEditView.create(l, LotsOfProps.class, new BeanRowView<Bean>(example), source, true);
				final ListAndSelectionAndValueReference<LotsOfProps> reference = view.getReference();
				
				ListComboView<LotsOfProps> listComboView = ListComboView.create(reference.value(), LotsOfProps.class, true, true);
				
				FormLayout layout = new FormLayout(
						"fill:default:grow",
						"fill:default:grow, 7dlu, pref");
				
				DefaultFormBuilder builder = new DefaultFormBuilder(layout);
				builder.setDefaultDialogBorder();
				
				builder.append(view.getComponent());
				builder.nextRow();
				builder.append(listComboView.getComponent());
				
				frame.add(builder.getPanel());
				
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
