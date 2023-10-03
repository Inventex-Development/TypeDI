package dev.inventex.typedi;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The container is the heart of the library. It is responsible for creating and managing dependencies.
 * The container is a singleton, so you can access it from anywhere in your application.
 */
@UtilityClass
public class Container {
    /**
     * The containers map contains all the containers that have been created.
     */
    private final Map<String, ContainerInstance> containers = new ConcurrentHashMap<>();

    /**
     * The default container is the container that is used when no container is specified.
     */
    private final ContainerInstance defaultContainer = new ContainerInstance();

    /**
     * Get a container by name. If the container does not exist, it will be created.
     * @param name the name of the container
     * @return the created or retrieved container
     */
    public ContainerInstance of(String name) {
        // resolve the container by name from the cache
        ContainerInstance container = containers.get(name);
        // create the container if it does not exist
        if (container == null) {
            container = new ContainerInstance();
            containers.put(name, container);
        }
        return container;
    }

    /**
     * Get a dependency from the default container. If the dependency is not global, it will be created and
     * injected with its dependencies. If the dependency is global, it will be created and stored
     * in the container for future use.
     * @param clazz the class of the dependency
     * @return the created or retrieved dependency
     * @param <T> the type of the dependency
     */
    @SneakyThrows
    public <T> T get(Class<T> clazz) {
        return defaultContainer.get(clazz);
    }

    /**
     * Get a dependency from the default container. If the dependency is not global, it will be created and
     * injected with its dependencies. If the dependency is global, it will be created and stored
     * in the container for future use.
     * @param clazz the class of the dependency
     * @param global whether the dependency is global
     * @param factory the factory to use to create the dependency
     * @return the created or retrieved dependency
     * @param <T> the type of the dependency
     */
    public <T> T get(Class<T> clazz, boolean global, Factory<T> factory) {
        return defaultContainer.get(clazz, global, factory);
    }

    /**
     * Get a global value from the default container.
     * @param token the token of the value
     * @return the retrieved value
     * @param <T> the type of the value
     */
    public <T> T get(String token) {
        return defaultContainer.get(token);
    }

    /**
     * Set a dependency in the default container.
     * @param clazz the class of the dependency
     * @param dependency the dependency to set
     * @param <T> the type of the dependency
     */
    public <T> void set(Class<T> clazz, T dependency) {
        defaultContainer.set(clazz, dependency);
    }

    /**
     * Set a global value in the default container.
     * @param token the token of the value
     * @param value the value to set
     */
    public void set(String token, Object value) {
        defaultContainer.set(token, value);
    }

    /**
     * Check if the default container has a dependency.
     * @param clazz the class of the dependency
     * @return whether the container has the dependency
     */
    public boolean has(Class<?> clazz) {
        return defaultContainer.has(clazz);
    }

    /**
     * Check if the default container has a global value.
     * @param token the token of the value
     * @return whether the container has the value
     */
    public boolean has(String token) {
        return defaultContainer.has(token);
    }

    /**
     * Remove a dependency from the default container.
     * @param clazz the class of the dependency
     */
    public void remove(Class<?> clazz) {
        defaultContainer.remove(clazz);
    }

    /**
     * Remove a global value from the default container.
     * @param token the token of the value
     */
    public void remove(String token) {
        defaultContainer.remove(token);
    }

    /**
     * Reset the default container by removing all the dependencies and values.
     */
    public void reset() {
        defaultContainer.reset();
    }
}
