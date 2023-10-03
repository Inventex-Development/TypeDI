package dev.inventex.typedi;

/**
 * Represents a factory implementation that is a placeholder for not specifying a factory type.
 */
public class NullFactory implements Factory<Object> {
    /**
     * Do not actually create an instance. This is just a placeholder.
     * @return {@code null}.
     */
    @Override
    public Object create() {
        return null;
    }
}
