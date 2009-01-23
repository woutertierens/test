package demo.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.properties.list.ListSelectionEditableValueReference;
import org.jpropeller.properties.list.impl.ListSelectionEditableValueReferenceDefault;
import org.jpropeller.ui.Tabs;
import org.jpropeller.undo.UndoSystem;
import org.jpropeller.undo.delegates.impl.UndoDelegateSourceDefault;
import org.jpropeller.undo.impl.RedoAction;
import org.jpropeller.undo.impl.UndoAction;
import org.jpropeller.undo.impl.UndoSystemDefault;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Source;
import org.jpropeller.view.list.impl.ListEditView;
import org.jpropeller.view.table.impl.BeanRowView;

import test.example.LotsOfProps;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Simple demonstration of {@link TabsDemo}
 */
public class TabsDemo {

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
		final ObservableList<LotsOfProps> l = makeBeanList();
		final ObservableList<LotsOfProps> l2 = makeBeanList();

		final ObservableList<ObservableList<LotsOfProps>> allLists = new ObservableListDefault<ObservableList<LotsOfProps>>();
		allLists.add(l);
		allLists.add(l2);
		
		final LotsOfProps example = new LotsOfProps();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				final UndoSystem undoSystem = new UndoSystemDefault(allLists, new UndoDelegateSourceDefault());
				
				GeneralUtils.enableNimbus();
				
				JFrame frame = new JFrame("BeanListTableModel Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				final ListSelectionEditableValueReference<LotsOfProps> reference = new ListSelectionEditableValueReferenceDefault<LotsOfProps>(l, LotsOfProps.class);
				ListEditView<LotsOfProps> view = new ListEditView<LotsOfProps>(reference, LotsOfProps.class, new BeanRowView(example), source);

				final ListSelectionEditableValueReference<LotsOfProps> reference2 = new ListSelectionEditableValueReferenceDefault<LotsOfProps>(l2, LotsOfProps.class);
				ListEditView<LotsOfProps> view2 = new ListEditView<LotsOfProps>(reference2, LotsOfProps.class, new BeanRowView(example), source);

				Tabs tabs = new Tabs();
				JTabbedPane tabbedPane = tabs.getComponent();
				
				tabbedPane.insertTab("list 1", null, dialogSurround(view.getComponent()), "The first list", 0);
				tabbedPane.insertTab("list 2", null, dialogSurround(view2.getComponent()), "The second list", 1);
				
				JPanel buttons = new JPanel();

				JButton undo = new JButton(UndoAction.create(undoSystem));
				buttons.add(undo);

				JButton redo = new JButton(RedoAction.create(undoSystem));
				buttons.add(redo);
				
				FormLayout layout = new FormLayout(
						"fill:default:grow",
						"fill:default:grow, 7dlu, pref");
				
				DefaultFormBuilder builder = new DefaultFormBuilder(layout);
				//builder.setDefaultDialogBorder();
				
				builder.append(tabbedPane);
				builder.nextRow();
				builder.append(buttons);
				
				frame.add(builder.getPanel());
				
				frame.pack();
				frame.setVisible(true);
				
			}
		});
		
	}
	
	private static JPanel dialogSurround(JComponent c) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(c);
		panel.setBorder(Borders.DIALOG_BORDER);
		return panel;
	}

	private static ObservableList<LotsOfProps> makeBeanList() {
		final ObservableList<LotsOfProps> l = new ObservableListDefault<LotsOfProps>();
		for (int i = 0; i < 20; i++) {
			l.add(source.get());
		}
		return l;
	}
	
}
