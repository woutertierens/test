package demo.jview.node;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropelleralt.jview.node.impl.NodeView;
import org.jpropelleralt.jview.ref.impl.BooleanView;
import org.jpropelleralt.jview.ref.impl.GeneralView;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.Views;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import demo.examples.Person;

/**
 * Demonstrate {@link NodeView}
 */
public class NodeViewDemo {

	/**
	 * Demonstrate
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp2();
			}
		});
	}
	
	private static void runApp2() {
		LoggerUtils.enableConsoleLogging();
		Views.initialiseLookAndFeel();
		
		JFrame frame = new JFrame("Node View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final Person alice = new Person("Alice", "First Person", 20, null);
		final Person bob = new Person("Bob", "Second Person", 21, null);
		alice.bff().set(bob);
		bob.bff().set(alice);
		
		final Ref<Person> a = RefDefault.create(alice);
		final Ref<Person> b = RefDefault.create(bob);
		final Ref<Boolean> locked = RefDefault.create(false);
		
		BooleanView lockedView = BooleanView.createCheck(locked, "locked");
		
		NodeView aView = NodeView.create(a, locked);
		NodeView bView = NodeView.create(b, locked);
		
		GeneralView aGView = GeneralView.create(a);
		GeneralView bGView = GeneralView.create(b);

		final Ref<Person> selected = RefDefault.create(alice);
		NodeView selectedView = NodeView.create(selected, locked, "name", "description");
		GeneralView selectedGView = GeneralView.create(selected);

		JButton selectAlice = new JButton(new AbstractAction("Select Alice") {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected.set(alice);
			}
		});

		JButton selectBob = new JButton(new AbstractAction("Select Bob") {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected.set(bob);
			}
		});

		FormLayout layout = new FormLayout(
				"fill:pref:grow",
			//   gen. a      node a      gen. b      node b      gen. sel.   node sel.   select a    select b    locked
				"pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref"
		);
		
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		CellConstraints cc = new CellConstraints();
		
		int y = 1;
		builder.add(aGView.getComponent(),			cc.xy(1, y));
		y+=2;
		builder.add(aView.getComponent(),			cc.xy(1, y));
		y+=2;
		builder.add(bGView.getComponent(),			cc.xy(1, y));
		y+=2;
		builder.add(bView.getComponent(),			cc.xy(1, y));
		y+=2;
		builder.add(selectedGView.getComponent(),	cc.xy(1, y));
		y+=2;
		builder.add(selectedView.getComponent(),	cc.xy(1, y));
		y+=2;
		builder.add(selectAlice,					cc.xy(1, y));
		y+=2;
		builder.add(selectBob,						cc.xy(1, y));
		y+=2;
		builder.add(lockedView.getComponent(),		cc.xy(1, y));

		frame.getContentPane().add(builder.getPanel());
		frame.pack();
		frame.setVisible(true);
		
		System.out.println("Possibly about to lose scope.");
	}
	
}
