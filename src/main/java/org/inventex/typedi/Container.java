package org.inventex.typedi;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container {
    private static Map<Class<?>, Object> dependencies = new ConcurrentHashMap<>();

    private static Map<String, Object> values = new ConcurrentHashMap<>();


    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        Service service = clazz.getDeclaredAnnotation(Service.class);
        if (service == null)
            throw new UnknownDependencyException(clazz.getName() + " is not a service");

        Constructor<? extends Factory<?>> constructor = service.factory().getDeclaredConstructor();
        constructor.setAccessible(true);
        Factory<T> factory = (Factory<T>) constructor.newInstance();

        return get(clazz, service.global(), factory);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, boolean global, Factory<T> factory) {
        if (!global)
            return createInstance(clazz, factory);

        if (!has(clazz)) {
            T instance = createInstance(clazz, factory);
            set(clazz, instance);
            return instance;
        }

        return (T) dependencies.get(clazz);
    }

    @SneakyThrows
    private static <T> T createInstance(Class<T> clazz, Factory<T> factory) {
        if (factory != null && !factory.getClass().equals(NullFactory.class))
            return factory.create();

        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        Class<?>[] types = constructor.getParameterTypes();

        int parameterCount = constructor.getParameterCount();
        Object[] args = new Object[parameterCount];

        for (int i = 0; i < parameterCount; i++) {
            Class<?> paramType = types[i];

            if (!paramType.isAnnotationPresent(Service.class))
                continue;

            args[i] = get(paramType);
        }

        @SuppressWarnings("unchecked")
        T instance = (T) constructor.newInstance(args);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            Class<?> fieldType = field.getType();
            Annotation[] fieldAnnotations = field.getAnnotations();

            if (fieldAnnotations.length == 0)
                continue;

            Annotation annotation = fieldAnnotations[0];
            if (!annotation.annotationType().equals(Inject.class))
                continue;

            field.set(instance, get(fieldType));
        }

        return instance;
    }

    @SneakyThrows
    private static <T> T createInstance(Class<T> clazz) {
       return createInstance(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String token) {
        if (!has(token))
            throw new UnknownDependencyException("Unknown dependency: " + token);
        return (T) values.get(token);
    }

    public static <T> void set(Class<T> clazz, T dependency) {
        dependencies.put(clazz, dependency);
    }

    public static void set(String token, Object value) {
        values.put(token, value);
    }

    public static boolean has(Class<?> clazz) {
        return dependencies.containsKey(clazz);
    }

    public static boolean has(String token) {
        return values.containsKey(token);
    }
}
