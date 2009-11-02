package demo.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.reference.impl.PathReference;
import org.jpropeller.reference.impl.PathReferenceBuilder;
import org.jpropeller.transformer.BeanPathTo;
import org.jpropeller.undo.UndoSystem;
import org.jpropeller.undo.delegates.impl.UndoDelegateSourceDefault;
import org.jpropeller.undo.impl.UndoSystemDefault;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Source;
import org.jpropeller.view.bean.impl.BeanEditor;
import org.jpropeller.view.list.impl.ListAddAction;
import org.jpropeller.view.list.impl.ListDeleteAction;
import org.jpropeller.view.list.impl.ListMoveAction;
import org.jpropeller.view.table.impl.BeanRowView;
import org.jpropeller.view.table.impl.SingleSelectionListTableView;

import test.example.LotsOfProps;

/**
 * Simple demonstration of {@link SingleSelectionListTableView}
 */
public class SingleSelectionListTableViewDemo {

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
				
				final ListAndSelectionAndValueReference<LotsOfProps> reference = new ListAndSelectionAndValueReferenceDefault<LotsOfProps>(LotsOfProps.class, l);
				
				//Make table model based on reference and row view displaying properties as columns
				SingleSelectionListTableView<LotsOfProps> view = new SingleSelectionListTableView<LotsOfProps>(reference, new BeanRowView<Bean>(example));
				
				//Default table using model
				JTable table = view.getComponent();
				JScrollPane scroll = new JScrollPane(table);
				
				JPanel buttons = new JPanel();

				ListMoveAction<LotsOfProps> moveUpAction = ListMoveAction.createUpAction(reference);
				ListMoveAction<LotsOfProps> moveDownAction = ListMoveAction.createDownAction(reference);
				ListDeleteAction<LotsOfProps> deleteAction = ListDeleteAction.create(reference);
				
				ListAddAction<LotsOfProps> addAction = ListAddAction.create(reference, source);
				
				JButton moveUp = new JButton(moveUpAction);
				JButton moveDown = new JButton(moveDownAction);
				JButton delete = new JButton(deleteAction);
				JButton add = new JButton(addAction);
				buttons.add(moveUp);
				buttons.add(moveDown);
				buttons.add(delete);
				buttons.add(add);

				JButton undo = new JButton("undo");
				undo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						undoSystem.undo();
					}
				});
				buttons.add(undo);

				JButton redo = new JButton("redo");
				redo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						undoSystem.redo();
					}
				});
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

				
				//Make a transform from the list reference to the selected value
				BeanPathTo<ListAndSelectionAndValueReference<LotsOfProps>, LotsOfProps> refToSelected = 
					ListAndSelectionAndValueReferenceDefault.transformerToSelectedValue();
				
				//Editor for current selection
				final PathReference<LotsOfProps> selectedReference = PathReferenceBuilder.from(LotsOfProps.class, reference).to(refToSelected);
				final BeanEditor<LotsOfProps> selectedEditor = BeanEditor.create(selectedReference);
				JScrollPane selectedEditorScroll = new JScrollPane(selectedEditor.getComponent());
				selectedEditorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

				frame.add(scroll, BorderLayout.CENTER);
				frame.add(buttons, BorderLayout.SOUTH);
				frame.add(selectedEditorScroll, BorderLayout.EAST);
				
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
