package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.EditablePathProp;
import org.jpropeller.transformer.BeanPathToEditable;
import org.jpropeller.transformer.BeanPathVia;

/**
 * Simple demo class
 */
public class A extends BeanDefault {

	/**
	 * Transform from {@link A} to its {@link #b()} property
	 */
	public static BeanPathVia<A, B> aToB = new BeanPathVia<A, B>(){
		@Override
		public Prop<B> transform(A s) {
			return s.b();
		}
	};

	/**
	 * Transform from {@link B} to its {@link B#c()} property
	 */
	BeanPathVia<B, C> bToC = new BeanPathVia<B, C>(){
		@Override
		public Prop<C> transform(B s) {
			return s.c();
		}
	};

	/**
	 * Transform from {@link C} to its {@link C#d()} property
	 */
	BeanPathToEditable<C, D> cToD = new BeanPathToEditable<C, D>() {
		@Override
		public EditableProp<D> transform(C s) {
			return s.d();
		}
	};

	private EditableProp<B> b = editable(B.class, "b", null);

	/**
	 * B property
	 * @return b
	 */
	public EditableProp<B> b() {return b;} 
	
	private EditableProp<D> d;

	/**
	 * D property
	 * @return d
	 */
	public EditableProp<D> d() {return d;}

	/**
	 * Make a new instance of A
	 */
	public A() {
		BeanPath<A, EditableProp<D>, D> pathForD = BeanPathBuilder.createVia(aToB).via(bToC).to(cToD);
		d = addProp(new EditablePathProp<A, D>(PropName.editable("d", D.class), this, pathForD));
	} 
	
}
