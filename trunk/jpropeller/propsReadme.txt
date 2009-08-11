A Bean is very much like a JavaBean

It doesn't have setters and getters - it has Props. The Props have setters and getters. They also have a type, so a Prop with a String value is a Prop<String>

Let's say we have a simple Bean, called "bean", with a String property "name" and an Integer property "age"
The Props of a Bean can be made available via public final fields:
	bean.name.get()
Or by methods:
	bean.name().get()
Methods are preferred, since then they can be specified in interfaces:
public interface NamedBean {
	public Prop<String> name();
}
However public final fields also work fine if you do not want interfaces.

The only additional requirement of a Bean is that it makes its Props available via a PropSet.
A PropSet provides access to the Props of a bean in a map-like way:
	bean.getPropSet().getProp(new PropName<Integer>("age"));
Why do we pass a PropName<Integer> instead of just the String "age"? This is so that we know we will get a Prop<Integer>.
A PropSet also allows us to listen for changes to any Prop in the PropSet. Listeners will receive a PropEvent when a Prop changes.

Props and PropSets and Beans are linked together closely. A Prop belongs to one PropSet, and cannot change the PropSet it belongs to. A PropSet belongs to one Bean, and cannot change the Bean it belongs to. Props know which PropSet they belong to, and PropSets know which Bean they belong to.

The rule that Props cannot change PropSet and PropSets cannot change Bean is enforced - once a Prop is added to one PropSet, attempting to add it to another PropSet will fail at runtime. Similarly for PropSets and Beans.