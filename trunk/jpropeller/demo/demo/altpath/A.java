package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.BeanPathTo;
import org.jpropeller.transformer.BeanPathVia;

/**
 * Simple demo class
 */
public class A extends BeanDefault {

	/**
	 * The {@link PropName} of the {@link B} property
	 */
	public final static PropName<B> B_NAME = PropName.create(B.class, "b"); 
	
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
	BeanPathTo<C, D> cToD = new BeanPathTo<C, D>() {
		@Override
		public Prop<D> transform(C s) {
			return s.d();
		}
	};

	private Prop<B> b = editable(B.class, "b", null);
	/**
	 * B property
	 * @return b
	 */
	public Prop<B> b() {return b;} 
	
	
	private Prop<D> dByTransforms = editableFrom("dByTransforms", D.class, this).via(aToB).via(bToC).to(cToD);
	
	/**
	 * {@link D} property, via transforms
	 * @return dByTransforms
	 */
	public Prop<D> dByTransforms() {return dByTransforms;}

	private Prop<D> dByNames = null;//editableFrom("dByNames", D.class, this).via(B_NAME).via(B.C_NAME).to(C.D_NAME);
	/**
	 * {@link D} property, via names
	 * @return dByNames
	 */
	public Prop<D> dByNames() {return dByNames;}

	/**
	 * Make a new instance of {@link A}
	 */
	public A() {
		
		//Build path and pathprop by names
		//BeanPath<? super A, EditableProp<D>, D> pathForDByNames = BeanPathBuilder.<A>create().via(B_NAME).via(B.C_NAME).to(C.D_NAME);
		//dByNames = addProp(new EditablePathProp<A, D>(PropName.editable("dByNames", D.class), this, pathForDByNames));

	} 
	
}
