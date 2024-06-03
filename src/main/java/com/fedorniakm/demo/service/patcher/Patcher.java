package com.fedorniakm.demo.service.patcher;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * General Patcher to partially update (patch) DTO.
 * @param <T> Target type to be patched.
 * @param <P> Patch type to be applied to the target.
 */
@FunctionalInterface
public interface Patcher<T, P> {

    void patch(T target, P patch);

    default <V> void patchNonNull(V value, Consumer<V> patch) {
        if (Objects.nonNull(value)) {
            patch.accept(value);
        }
    }

    default <V> void patchNotEmpty(V value, Consumer<V> patch) {
        if (Objects.nonNull(value)) {
            patch.accept(value);
        }
    }

}
