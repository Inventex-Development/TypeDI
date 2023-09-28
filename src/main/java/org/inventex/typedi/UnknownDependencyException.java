package org.inventex.typedi;

public class UnknownDependencyException extends RuntimeException {
    public UnknownDependencyException(String message) {
        super(message);
    }
}
