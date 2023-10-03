package dev.inventex.typedi;

import lombok.SneakyThrows;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The container is the heart of the library. It is responsible for creating and managing dependencies.
 * The container is a singleton, so you can access it from anywhere in your application.
 */
public class ContainerInstance {
    /**
     * The dependencies map contains all the non-global dependencies that have been created.
     */
    private final Map<Class<?>, Object> dependencies = new ConcurrentHashMap<>();

    /**
     * The values map contains all the global values that have been stored.
     */
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    /**
     * Get a dependency from the container. If the dependency is not global, it will be created and
     * injected with its dependencies. If the dependency is global, it will be created and stored
     * in the container for future use.
     * @param clazz the class of the dependency
     * @return the created or retrieved dependency
     * @param <T> the type of the dependency
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        // check if the class is not annotated with @Service
        Service service = clazz.getDeclaredAnnotation(Service.class);
        if (service == null)
            throw new UnknownDependencyException(clazz.getName() + " is not a service");

        // check if the service has a factory
        Class<? extends Factory<?>> factoryType = service.factory();
        if (!factoryType.equals(NullFactory.class)) {
            // create an instance of the factory
            Constructor<? extends Factory<?>> constructor = factoryType.getDeclaredConstructor();
            constructor.setAccessible(true);
            // use the factory to create the dependency instance
            Factory<T> factory = (Factory<T>) constructor.newInstance();
            return get(clazz, service.global(), factory);
        }

        // service has no factory, create the dependency manually
        return get(clazz, service.global(), null);
    }

    /**
     * Get a dependency from the container. If the dependency is not global, it will be created and
     * injected with its dependencies. If the dependency is global, it will be created and stored
     * in the container for future use.
     * @param clazz the class of the dependency
     * @param global whether the dependency is global
     * @param factory the factory to use to create the dependency
     * @return the created or retrieved dependency
     * @param <T> the type of the dependency
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, boolean global, Factory<T> factory) {
        // create the dependency for every call if it is not global
        if (!global)
            return createInstance(clazz, factory);

        // check if the dependency has not been created yet
        if (!has(clazz)) {
            T instance = createInstance(clazz, factory);
            set(clazz, instance);
            return instance;
        }

        // retrieve the dependency from the container
        return (T) dependencies.get(clazz);
    }

    /**
     * Create an instance of the specified class and inject its dependencies.
     * @param clazz the class to create an instance of
     * @param factory the factory to use to create the instance
     * @return the created instance
     * @param <T> the type of the instance
     */
    @SneakyThrows
    private <T> T createInstance(Class<T> clazz, Factory<T> factory) {
        // let the factory create the instance if it is not null
        if (factory != null && !factory.getClass().equals(NullFactory.class))
            return factory.create();

        // create the instance with its dependencies
        T instance = createInstanceWithDependencies(clazz);

        // inject the fields of the instance
        injectFields(clazz, instance);

        return instance;
    }

    /**
     * Create an instance of the specified class and inject its dependencies.
     * @param clazz the class to create an instance of
     * @return the created instance
     * @param <T> the type of the instance
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T> T createInstanceWithDependencies(Class<T> clazz) {
        // get the first constructor of the class
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        // create the arguments of the constructor call
        Class<?>[] types = constructor.getParameterTypes();
        int parameterCount = constructor.getParameterCount();
        Object[] args = new Object[parameterCount];

        // loop through the constructor parameters
        for (int i = 0; i < parameterCount; i++) {
            // check if the parameter is not annotated with @Service
            Class<?> paramType = types[i];
            if (!paramType.isAnnotationPresent(Service.class))
                continue;

            // get the dependency from the container
            args[i] = get(paramType);
        }

        // create the instance with the resolved service arguments
        return (T) constructor.newInstance(args);
    }

    /**
     * Inject the fields of the specified instance.
     * @param clazz the class of the instance
     * @param instance the instance to inject the fields of
     * @param <T> the type of the instance
     */
    @SneakyThrows
    private <T> void injectFields(Class<T> clazz, T instance) {
        // loop through the fields of the class
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            // retrieve the type metadata of the field
            Class<?> fieldType = field.getType();
            Annotation[] fieldAnnotations = field.getAnnotations();

            // skip the field if it has no annotations
            if (fieldAnnotations.length == 0)
                continue;

            // skip the field if it is not annotated with @Inject
            Annotation annotation = fieldAnnotations[0];
            if (!annotation.annotationType().equals(Inject.class))
                continue;

            // inject the field with the dependency
            field.set(instance, get(fieldType));
        }
    }

    /**
     * Create an instance of the specified class using dependency injection.
     * @param clazz the class to create an instance of
     * @return the created instance
     * @param <T> the type of the instance
     */
    @SneakyThrows
    private <T> T createInstance(Class<T> clazz) {
       return createInstance(clazz, null);
    }

    /**
     * Get a global value from the container.
     * @param token the token of the value
     * @return the retrieved value
     * @param <T> the type of the value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String token) {
        // check if the value has not been stored yet
        if (!has(token))
            throw new UnknownDependencyException("Unknown dependency: " + token);
        // retrieve the value from the container
        return (T) values.get(token);
    }

    /**
     * Set a dependency in the container.
     * @param clazz the class of the dependency
     * @param dependency the dependency to set
     * @param <T> the type of the dependency
     */
    public <T> void set(Class<T> clazz, T dependency) {
        dependencies.put(clazz, dependency);
    }

    /**
     * Set a global value in the container.
     * @param token the token of the value
     * @param value the value to set
     */
    public void set(String token, Object value) {
        values.put(token, value);
    }

    /**
     * Check if the container has a dependency.
     * @param clazz the class of the dependency
     * @return whether the container has the dependency
     */
    public boolean has(Class<?> clazz) {
        return dependencies.containsKey(clazz);
    }

    /**
     * Check if the container has a global value.
     * @param token the token of the value
     * @return whether the container has the value
     */
    public boolean has(String token) {
        return values.containsKey(token);
    }

    /**
     * Remove a dependency from the container.
     * @param clazz the class of the dependency
     * @param clazz the class of the dependency
     */
    public void remove(Class<?> clazz) {
        dependencies.remove(clazz);
    }

    /**
     * Remove a global value from the container.
     * @param token the token of the value
     */
    public void remove(String token) {
        values.remove(token);
    }

    /**
     * Reset the container by removing all the dependencies and values.
     */
    public void reset() {
        dependencies.clear();
        values.clear();
    }
}
