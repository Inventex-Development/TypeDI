package org.inventex.typedi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an annotation that indicates, that a certain class is a service.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
    /**
     * Indicate, whether the service should be a singleton or should be instantiated for each injection.
     * @return {@code true} if the service should be a singleton, {@code false} otherwise.
     */
    boolean global() default false;

    /**
     * Get the factory that creates instances of the service.
     * @return service creation factory class
     */
    Class<? extends Factory<?>> factory() default NullFactory.class;
}
