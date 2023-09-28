package org.inventex.typedi;

/**
 * Represents an exception that is thrown when a dependency is not found or is invalid
 */
public class UnknownDependencyException extends RuntimeException {
    /**
     * Initializes a new instance of the {@link UnknownDependencyException} class.
     * @param message the message that describes the error.
     */
    public UnknownDependencyException(String message) {
        super(message);
    }
}
