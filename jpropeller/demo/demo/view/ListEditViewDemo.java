package demo.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
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
import com.jgoodies.forms.layout.FormLayout;

/**
 * Simple demonstration of {@link ListEditView}
 */
public class ListEditViewDemo {

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
				
				final UndoSystem undoSystem = new UndoSystemDefault(l, new UndoDelegateSourceDefault());
				
				GeneralUtils.enableNimbus();
				
				JFrame frame = new JFrame("BeanListTableModel Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				//final ListSelectionEditableValueReference<LotsOfProps> reference = new ListSelectionEditableValueReferenceDefault<LotsOfProps>(l, LotsOfProps.class);
				//ListEditView<LotsOfProps> view = new ListEditView<LotsOfProps>(reference, LotsOfProps.class, new BeanRowView(example), source);

				ListEditView<LotsOfProps> view = ListEditView.create(l, LotsOfProps.class, new BeanRowView(example), source, true);
				final ListAndSelectionAndValueReference<LotsOfProps> reference = view.getReference();
				
				JPanel buttons = new JPanel();

				JButton undo = new JButton(UndoAction.create(undoSystem));
				buttons.add(undo);

				JButton redo = new JButton(RedoAction.create(undoSystem));
				buttons.add(redo);

				JButton incrementSelection = new JButton("inc. selection");
				incrementSelection.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						reference.selection().set(reference.selection().get()+1);
					}
				});
				buttons.add(incrementSelection);

				JButton clearSelection = new JButton("clear selection");
				clearSelection.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						reference.selection().set(-1);
					}
				});
				buttons.add(clearSelection);
				
				JButton addStart = new JButton("Add Start");
				addStart.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps p = new LotsOfProps();
						p.stringProp().set("New random p " + Math.random());
						reference.value().get().add(0, p);
					}
				});
				buttons.add(addStart);

				JButton addEnd = new JButton("Add End");
				addEnd.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps p = new LotsOfProps();
						p.stringProp().set("New random p " + Math.random());
						reference.value().get().add(p);
					}
				});
				buttons.add(addEnd);
				
				JButton changeOriginalList = new JButton("Change original list");
				changeOriginalList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						l.get(0).doubleProp().set(Math.random());
					}
				});
				buttons.add(changeOriginalList);
				
				JButton changeCurrentList = new JButton("Change current list");
				changeCurrentList.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						reference.value().get().get(0).doubleProp().set(Math.random());
					}
				});
				buttons.add(changeCurrentList);
				
				FormLayout layout = new FormLayout(
						"fill:default:grow",
						"fill:default:grow, 7dlu, pref");
				
				DefaultFormBuilder builder = new DefaultFormBuilder(layout);
				builder.setDefaultDialogBorder();
				
				builder.append(view.getComponent());
				builder.nextRow();
				builder.append(buttons);
				
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
