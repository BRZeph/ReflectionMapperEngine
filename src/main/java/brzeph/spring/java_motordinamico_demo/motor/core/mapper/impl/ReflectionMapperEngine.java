package brzeph.spring.java_motordinamico_demo.motor.core.mapper.impl;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ReflectionMapperEngine {

    /**
     * Executa o mapeamento reflexivo de um objeto baseado em uma anotação, podendo retornar
     * um novo objeto ou um Map.
     *
     * @param source Objeto de origem a ser mapeado.
     * @param clazz Classe do objeto de origem.
     * @param annotationClass Anotação a ser considerada para filtragem (ex.: Read.class, Create.class, Update.class).
     * @param outputAsMap Se true, retorna um Map (modo leitura, ideal para resposta JSON); se false, retorna um novo objeto instanciado.
     * @return Objeto mapeado (ou Map) com os campos filtrados.
     */
    public static Object map(Object source, Class<?> clazz, Class<? extends Annotation> annotationClass, boolean outputAsMap) {
        if (source == null) return null;
        if (outputAsMap) return mapToMap(source, clazz, annotationClass);
        return mapToObject(source, clazz, annotationClass);
    }

    public static <T> T mergeWithAnnotation(T base, T override, Class<? extends Annotation> annotationClass) {
        if (base == null || override == null) {
            throw new IllegalArgumentException("Objetos não podem ser nulos");
        }

        try {
            Class<?> clazz = base.getClass();
            @SuppressWarnings("unchecked")
            T merged = (T) clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    Object baseValue = field.get(base);
                    Object overrideValue = field.get(override);

                    Object finalValue;

                    if (overrideValue != null) {
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            finalValue = overrideValue; // sempre sobrescreve lista inteira
                        } else if (isPojo(field.getType())) {
                            if (baseValue == null) {
                                finalValue = overrideValue;
                            } else {
                                finalValue = mergeWithAnnotation(baseValue, overrideValue, annotationClass);
                            }
                        } else {
                            finalValue = overrideValue;
                        }
                    } else {
                        finalValue = baseValue;
                    }

                    field.set(merged, finalValue);
                }
            }
            return merged;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer merge com annotation: " + annotationClass.getSimpleName(), e);
        }
    }

    private static Object mapToObject(Object source, Class<?> clazz, Class<? extends Annotation> annotationClass) {
        try {
            Object target = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    Object value = field.get(source);

                    if (value != null) {
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            Collection<?> originalCollection = (Collection<?>) value;
                            Collection<Object> targetCollection = createEmptyCollection(field.getType());
                            Class<?> genericType = getGenericType(field);

                            if (genericType != null && isPojo(genericType)) {
                                for (Object item : originalCollection) {
                                    Object mappedItem = mapToObject(item, genericType, annotationClass);
                                    targetCollection.add(mappedItem);
                                }
                                field.set(target, targetCollection);
                            } else {
                                field.set(target, value);
                            }
                        } else if (isPojo(field.getType())) {
                            Object nested = mapToObject(value, field.getType(), annotationClass);
                            field.set(target, nested);
                        } else {
                            field.set(target, value);
                        }
                    }
                }
            }
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear objeto: " + clazz.getSimpleName(), e);
        }
    }

    private static Map<String, Object> mapToMap(Object source, Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    Object value = field.get(source);

                    if (Collection.class.isAssignableFrom(field.getType()) && value != null) {
                        Collection<?> originalCollection = (Collection<?>) value;
                        List<Object> mappedCollection = new ArrayList<>();
                        Class<?> genericType = getGenericType(field);

                        if (genericType != null && isPojo(genericType)) {
                            for (Object item : originalCollection) {
                                mappedCollection.add(mapToMap(item, genericType, annotationClass));
                            }
                        } else {
                            mappedCollection.addAll(originalCollection);
                        }
                        result.put(field.getName(), mappedCollection);
                    } else if (isPojo(field.getType()) && value != null) {
                        result.put(field.getName(), mapToMap(value, field.getType(), annotationClass));
                    } else {
                        result.put(field.getName(), value); // Inclui mesmo se null
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear objeto: " + clazz.getSimpleName(), e);
        }
        return result;
    }

    private static boolean isPojo(Class<?> type) {
        return !type.isPrimitive()
                && !type.isEnum()
                && !type.getPackageName().startsWith("java.")
                && !Collection.class.isAssignableFrom(type);
    }

    private static Collection<Object> createEmptyCollection(Class<?> collectionType) {
        if (List.class.isAssignableFrom(collectionType)) {
            return new ArrayList<>();
        }
        throw new UnsupportedOperationException("Collection não suportada: " + collectionType);
    }

    private static Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            Type actualType = parameterizedType.getActualTypeArguments()[0];
            if (actualType instanceof Class<?> clazz) {
                return clazz;
            }
        }
        return null;
    }
}


