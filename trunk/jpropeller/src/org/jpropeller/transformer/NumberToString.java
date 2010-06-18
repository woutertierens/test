package org.jpropeller.transformer;

import java.text.NumberFormat;

/**
 * An {@link ImmutableTransformer} from {@link Number} to {@link String}
 * Essentially an immutable, one-way {@link NumberFormat}
 */
public interface NumberToString extends ImmutableTransformer<Number, String> {
}
