package org.jpropeller.util;

/**
 * Utils to generate repeated code for primitive types, etc.
 */
public class BuildUtils {

	/**
	 * Run the utils
	 * @param args		Ignored
	 */
	public static void main(String[] args) {
		generateImmutablePropMethods();
	}
	
	/**
	 * Generate the immutable prop methods
	 */
	public final static void generateImmutablePropMethods() {
		
		String[] extendedBeanFeaturesMethods = new String[] {
			"/**\n"+
			" * Make a new read-only TYPE {@link Prop} and add to\n" +
			" * this {@link Bean}\n"+
			" * @param name\t\tThe name of the {@link Prop}\n"+
			" * @param value\t\tThe value of the {@link Prop}\n"+
			" * @return\t\t\tThe new {@link Prop} itself (already added to the {@link BeanFeatures})\n"+
			" */\n" +
			"public Prop<TYPE> create(String name, TYPE value);\n",
			
			"/**\n"+
			" * Make a new TYPE {@link Prop} and add to this {@link Bean}\n"+
			" * @param name\t\tThe name of the {@link Prop}\n"+
			" * @param value\t\tThe value of the {@link Prop}\n"+
			" * @param processor\tThe processor used when new values are set\n"+ 
			" * @return\t\t\tThe new {@link Prop} itself (already added to the {@link BeanFeatures})\n"+
			" */\n" +
			"public Prop<TYPE> create(String name, TYPE value, ValueProcessor<TYPE> processor);\n",
			
			"/**\n"+
			" * Make a new editable TYPE {@link Prop}, accepting all values,\n" +
			" * and add to this {@link Bean}\n"+
			" * @param name\t\tThe name of the {@link Prop}\n"+
			" * @param value\t\tThe value of the {@link Prop}\n"+
			" * @return\t\t\tThe new {@link Prop} itself (already added to the {@link BeanFeatures})\n"+
			" */\n" +
			"public Prop<TYPE> editable(String name, TYPE value);\n",
		};

		String[] extendedBeanFeaturesDefaultMethods = new String[] {
				"@Override\n"+
				"public Prop<TYPE> create(String name, TYPE value) {\n" + 
				"\treturn add(PropImmutable.create(name, value));\n" + 
				"}\n",
				
				"@Override\n"+
				"public Prop<TYPE> create(String name, TYPE value, ValueProcessor<TYPE> processor) {\n" + 
				"\treturn add(PropImmutable.create(name, value, processor));\n" + 
				"}\n",
				
				"@Override\n"+
				"public Prop<TYPE> editable(String name, TYPE value) {\n" + 
				"\treturn add(PropImmutable.editable(name, value));\n" + 
				"}\n",
			};

		
		String[] propImmutableMethods = new String[] {
				"/**\n"+
				" * Make a new read-only TYPE {@link Prop}\n"+
				" * @param name\t\tThe name of the {@link Prop}\n"+
				" * @param value\t\tThe value of the {@link Prop}\n"+
				" * @return\t\t\tThe new {@link Prop}\n"+
				" */\n" +
				"public final static PropImmutable<TYPE> create(String name, TYPE value){\n" +
				"\treturn new PropImmutable<TYPE>(PropName.create(name, TYPE.class), value, ReadOnlyProcessor.<TYPE>get());\n" +
				"}\n",
				
				"/**\n"+
				" * Make a new TYPE {@link Prop}\n"+
				" * @param name\t\tThe name of the {@link Prop}\n"+
				" * @param value\t\tThe value of the {@link Prop}\n"+
				" * @param processor\tThe processor used when new values are set\n"+ 
				" * @return\t\t\tThe new {@link Prop}\n"+
				" */\n" +
				"public final static PropImmutable<TYPE> create(String name, TYPE value, ValueProcessor<TYPE> processor){\n" +
				"\treturn new PropImmutable<TYPE>(PropName.create(name, TYPE.class), value, processor);\n" +
				"}\n",
				
				"/**\n"+
				" * Make a new editable TYPE {@link Prop}, accepting all values\n" +
				" * @param name\t\tThe name of the {@link Prop}\n"+
				" * @param value\t\tThe value of the {@link Prop}\n"+
				" * @return\t\t\tThe new {@link Prop}\n"+
				" */\n" +
				"public final static PropImmutable<TYPE> editable(String name, TYPE value){\n" +
				"\treturn new PropImmutable<TYPE>(PropName.create(name, TYPE.class), value, AcceptProcessor.<TYPE>get());\n" +
				"}\n",
			};

		

		
		
		String[] types = new String[] {
										"String",
										"Boolean",
										"Byte",
										"Short",
										"Integer",
										"Long",
										"Float",
										"Double",
										"DateTime",
										"Color",
										"ImmutableIcon",
		                            };
		
		
		System.out.println("//ExtendedBeanFeatures#############################");
		System.out.println();
		for (String type : types) {
			for (String method : extendedBeanFeaturesMethods) {
				System.out.println(method.replaceAll("TYPE", type));
				System.out.println();
			}
		}
		
		
		System.out.println("//ExtendedBeanFeaturesDefault#############################");
		System.out.println();
		for (String type : types) {
			for (String method : extendedBeanFeaturesDefaultMethods) {
				System.out.print(method.replaceAll("TYPE", type));
			}
		}
		

		System.out.println("//PropImmutable#############################");
		System.out.println();
		for (String type : types) {
			for (String method : propImmutableMethods) {
				System.out.print(method.replaceAll("TYPE", type));
			}
		}

	}
	
}
