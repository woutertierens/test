package org.jpropeller.view;

import org.jpropeller.view.update.UpdatableView;

/**
 * A {@link SingleValueView} that is also an {@link UpdatableView}
 *
 * @param <M>		The type of value in the model
 */
public interface UpdatableSingleValueView<M> extends UpdatableView, SingleValueView<M> {

}
