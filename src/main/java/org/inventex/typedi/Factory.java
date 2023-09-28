package org.inventex.typedi;

/**
 * Represents a factory that creates instances of type {@code T}.
 * @param <T> The type of the instances to create.
 */
public interface Factory<T> {
    /**
     * Create a new instance of type {@code T}.
     * @return A new instance of type {@code T}.
     */
    T create();
}
