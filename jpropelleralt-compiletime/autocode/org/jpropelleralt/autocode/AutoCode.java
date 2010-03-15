package org.jpropelleralt.autocode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jpropelleralt.utils.impl.FileUtils;

/**
 * Utils for automatic code generation
 */
public class AutoCode {

	/**
	 * Primitive wrapper and other simple immutable types.
	 */
	public final static String[] CRUDE_TYPES = new String[]{
		"Boolean",
		"Byte",
		"Short",
		"Integer",
		"Long",
		"Float",
		"Double",
		"String",
		"Color",
	};
	
	/**
	 * Tag to be present in template code wherever a crude type is needed
	 */
	public final static String CRUDE_TYPE_TAG = "CRUDE_TYPE";

	/**
	 * Number wrapper types, excluding {@link Byte} and {@link Short}
	 * since these are little-used, especially in a high-level data system.
	 */
	public final static String[] NUMBER_TYPES = new String[]{
		"Integer",
		"Long",
		"Float",
		"Double",
	};
	
	/**
	 * Tag to be present in template code wherever a number type is needed
	 */
	public final static String NUMBER_TYPE_TAG = "NUMBER_TYPE";

	/**
	 * Call {@link #makeTypes(String, String[], String)} with a template loaded from
	 * a resource, using {@link #CRUDE_TYPES} and {@value #CRUDE_TYPE_TAG}
	 * @param clazz		The resource class
	 * @return			The auto code.
	 */
	public final static String makeCrudeTypes(Class<?> clazz) {
		return AutoCode.makeTypes(clazz, CRUDE_TYPES, CRUDE_TYPE_TAG);
	}

	/**
	 * Call {@link #makeTypes(String, String[], String)} with a template loaded from
	 * a resource, using {@link #NUMBER_TYPES} and {@value #NUMBER_TYPE_TAG}
	 * @param clazz		The resource class
	 * @return			The auto code.
	 */
	public final static String makeNumberTypes(Class<?> clazz) {
		return AutoCode.makeTypes(clazz, NUMBER_TYPES, NUMBER_TYPE_TAG);
	}
	
	/**
	 * Call {@link #makeTypes(String, String[], String)} with a template loaded from
	 * a resource.
	 * @param clazz		The resource class
	 * @param types		The types to generate for
	 * @param tag		The tag to replace
	 * @return			The auto code.
	 */
	public final static String makeTypes(Class<?> clazz, String[] types, String tag) {
		String fullName = clazz.getName();
		String className = fullName.substring(fullName.lastIndexOf(".")+1, fullName.length());
		InputStream stream = clazz.getResourceAsStream(className + ".txt");
		Reader reader = new BufferedReader(new InputStreamReader(stream));
		String template;
		try {
			template = FileUtils.readerToString(reader);
		} catch (IOException e) {
			throw new RuntimeException("Missing resource for autocode");
		}
		return AutoCode.makeTypes(template, types, tag);
	}
	
	/**
	 * Make versions of provided template for each of the specified types,
	 * and append them together, with comments for the code in each type.
	 * @param template		The template, must have the tag wherever
	 * 						a type should be inserted.
	 * @param types			The types to generate for
	 * @param tag			The tag to replace
	 * @return				The auto code.
	 */
	public final static String makeTypes(String template, String[] types, String tag) {
		StringBuilder builder = new StringBuilder();
		for (String type : types) {
			builder.append("// AutoCode for " + type + "\n\n");
			builder.append(template.replaceAll(tag, type));
			builder.append("\n\n// End of AutoCode for " + type + "\n\n\n");
		}
		return builder.toString();
	}
}
