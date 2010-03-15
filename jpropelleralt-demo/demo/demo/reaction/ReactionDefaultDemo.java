package demo.reaction;

import java.util.Map;

import org.jpropelleralt.box.Box;
import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.change.Change;
import org.jpropelleralt.plumbing.NoInstanceAvailableException;
import org.jpropelleralt.plumbing.Source;
import org.jpropelleralt.reaction.impl.ReactionDefault;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.universe.impl.UniverseDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.View;

/**
 * Demonstrate {@link RefDefault}
 */
public class ReactionDefaultDemo {

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

		new ReactionDefault<Double>(
				new Source<Double>() {
					@Override
					public Double get() throws NoInstanceAvailableException {
						return a.get() + b.get();
					}
				}, 
		c, a, b);

		System.out.println("Setting a to 10");
		a.set(10d);
		System.out.println("c = " + c.get());

		System.out.println("Setting b to 100");
		b.set(100d);
		System.out.println("c = " + c.get());

	}

}
