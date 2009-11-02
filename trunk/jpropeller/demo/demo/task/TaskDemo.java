package demo.task;

import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.task.Task;
import org.jpropeller.task.impl.TaskExecutor;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Source;
import org.jpropeller.view.list.impl.ListEditView;
import org.jpropeller.view.table.impl.BeanRowView;

import test.example.LotsOfProps;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Demonstration of {@link Task}s, by removing elements from a list
 * when they match certain criteria
 */
public class TaskDemo {

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

		//Make a task to remove things from l, and set it up to execute
		Task task = new RemovalTask(l);
		new TaskExecutor(task);		//Note we would normally hold on to this to dispose of it as necessary
		
		final LotsOfProps example = new LotsOfProps();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				GeneralUtils.enableNimbus();
				GeneralUtils.enableConsoleLogging();
				
				JFrame frame = new JFrame("Task Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				ListEditView<LotsOfProps> view = ListEditView.create(l, LotsOfProps.class, new BeanRowView<Bean>(example), source, true);
				
				FormLayout layout = new FormLayout(
						"fill:default:grow",
						"fill:default:grow");
				
				DefaultFormBuilder builder = new DefaultFormBuilder(layout);
				//builder.setDefaultDialogBorder();
				
				builder.append(view.getComponent());
				
				frame.add(builder.getPanel());
				
				frame.pack();
				frame.setVisible(true);
				
			}
		});
		
	}

	/**
	 * Task that removes elements from a {@link CList} of {@link LotsOfProps}
	 * if their string prop starts with "d", or if the integer prop is greater than
	 * the long prop
	 */
	private static class RemovalTask implements Task {
		
		private final Set<Changeable> sources;
		private final CList<LotsOfProps> l;

		private RemovalTask(CList<LotsOfProps> l) {
			this.l = l;
			IdentityHashSet<Changeable> sourcesM = new IdentityHashSet<Changeable>();
			sourcesM.add(l);
			sources = Collections.unmodifiableSet(sourcesM);
		}
		
		@Override
		public void respond(AtomicBoolean shouldCancel) {
			Props.getPropSystem().getChangeSystem().acquire();
			try {
				Iterator<LotsOfProps> it = l.iterator();
				while (it.hasNext()) {
					LotsOfProps p = it.next();
					if (p.stringProp().get().startsWith("d") || p.intProp().get().longValue() > p.longProp().get().longValue()) {
						it.remove();
					}
				}
			} finally {
				Props.getPropSystem().getChangeSystem().release();
			}
		}
	
		@Override
		public Set<? extends Changeable> getSources() {
			return sources;
		}
		
	}
	
	private static CList<LotsOfProps> makeBeanList() {
		final CList<LotsOfProps> l = new CListDefault<LotsOfProps>();
		for (int i = 0; i < 20; i++) {
			l.add(source.get());
		}
		return l;
	}
}
