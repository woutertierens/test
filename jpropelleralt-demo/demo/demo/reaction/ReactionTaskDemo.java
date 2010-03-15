package demo.reaction;

import java.util.Map;

import org.jpropelleralt.box.Box;
import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.change.Change;
import org.jpropelleralt.reaction.impl.ReactionTask;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.View;

/**
 * Demonstrate {@link RefDefault}
 */
public class ReactionTaskDemo {

	/**
	 * Demonstrate {@link RefDefault}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");

		LoggerUtils.enableConsoleLogging(RefDefault.class, UniverseDefault.class);

		final Ref<Double> a = Boxes.create(0.5d);
		final Ref<Double> b = Boxes.create(0.15d);
		final Ref<Double> c = Boxes.create(-1d);

		c.features().addView(new View() {
			@Override
			public void changes(Map<Box, Change> changes) {
				System.out.println("CHANGE: c = " + c.get());
			}
		});

		//See ReactionDefaultDemo for the right way to do this - should only use
		//ReactionTask when we don't actually know the exact target in advance.
		new ReactionTask(
				new Runnable() {
					@Override
					public void run() {
						//FIXME should this call set(value, true) - what do we do when asking the "user" to apply a Reaction?
						//Would be SO much nicer if the Universe could just work out whether changes should be propagated.
						c.set(a.get() + b.get());
					}
				},
		a, b);

		System.out.println("Setting a to 10");
		a.set(10d);
		System.out.println("c = " + c.get());

		System.out.println("Setting b to 100");
		b.set(100d);
		System.out.println("c = " + c.get());

	}

}
