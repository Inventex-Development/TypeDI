package org.inventex.typedi;

public class NullFactory implements Factory<Object> {
    @Override
    public Object create() {
        return null;
    }
}
