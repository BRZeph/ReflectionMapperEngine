package brzeph.spring.java_motordinamico_demo.engine.core.mapper.impl;


import brzeph.spring.java_motordinamico_demo.engine.core.exceptions.ContextMappingEngineError;
import brzeph.spring.java_motordinamico_demo.engine.core.exceptions.IllegalArgumentEngineException;
import brzeph.spring.java_motordinamico_demo.engine.core.exceptions.IllegalStateEngineException;
import brzeph.spring.java_motordinamico_demo.engine.core.exceptions.MergeEngineException;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.identity.Id;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.identity.MergeId;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Realiza o merge entre dois objetos do mesmo tipo, campo a campo.
     * <p>
     * O merge é realizado seguindo as seguintes regras:
     * <ul>
     *     <li>Se {@code annotationClass} for {@code null}, todos os campos são processados, independentemente de estarem anotados.</li>
     *     <li>Se {@code annotationClass} não for {@code null}, somente os campos que possuem a annotation especificada serão processados.</li>
     *     <li>Para campos não processados, mantém-se o valor do objeto base.</li>
     *     <li>Para campos simples (primitivos, wrappers, strings, datas etc):</li>
     *     <ul>
     *         <li>Se o valor no objeto override for {@code null}, mantém o valor do base.</li>
     *         <li>Se o valor no objeto override for não nulo, substitui o valor do base.</li>
     *     </ul>
     *     <li>Para campos do tipo {@code Collection}:</li>
     *     <ul>
     *         <li>Se o valor no override não for nulo, sobrescreve a lista inteira.</li>
     *         <li>Não há merge incremental de listas neste método.</li>
     *     </ul>
     *     <li>Para campos de tipos compostos (POJOs customizados):</li>
     *     <ul>
     *         <li>O merge é aplicado recursivamente, aplicando a mesma lógica de annotation em todos os níveis.</li>
     *     </ul>
     * </ul>
     *
     * @param base            Objeto base a ser usado como referência inicial.
     * @param override        Objeto contendo os valores novos que podem sobrescrever o base.
     * @param annotationClass Annotation a ser considerada no merge. Se {@code null}, todos os campos são processados.
     * @param <T>             Tipo genérico dos objetos a serem mesclados.
     * @return Novo objeto resultante do merge.
     * @throws IllegalArgumentEngineException Se qualquer um dos objetos fornecidos for {@code null}.
     * @throws RuntimeException         Se ocorrer qualquer erro de reflexão durante o merge.
     */
    public static <T> T mergeWithAnnotation(T base, T override, Class<? extends Annotation> annotationClass) {
        if (base == null || override == null) {
            throw new IllegalArgumentEngineException("Objetos não podem ser nulos");
        }

        if (!base.getClass().equals(override.getClass())) {
            throw new MergeEngineException("Classes incompatíveis para merge: "
                    + base.getClass().getSimpleName() + " ≠ " + override.getClass().getSimpleName());
        }

        try {
            Class<?> clazz = base.getClass();
            @SuppressWarnings("unchecked")
            T merged = (T) clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object baseValue = field.get(base);
                Object overrideValue = field.get(override);
                Object finalValue;

                boolean hasAnnotation = (annotationClass == null) || field.isAnnotationPresent(annotationClass);

                if (hasAnnotation) {
                    if (overrideValue != null) {
                        if (Collection.class.isAssignableFrom(field.getType())) {
                            finalValue = mergeCollections(baseValue, overrideValue, annotationClass, field);
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
                } else {
                    finalValue = baseValue;
                }

                field.set(merged, finalValue);
            }
            return merged;

        } catch (Exception e) {
            throw new MergeEngineException("Erro ao fazer merge com annotation: "
                    + (annotationClass != null ? annotationClass.getSimpleName() : "ALL FIELDS") + e.getMessage());
        }
    }

    /**
     * Mescla dois objetos complementares numa terceira instância de classe alvo, com validação total.
     * <p>
     * Este método assume que todos os campos da classe de destino (resultClass) existem em pelo menos
     * um dos dois objetos de origem (obj1 ou obj2), e que todos os campos de origem estão representados
     * no objeto de destino.
     * </p>
     * <p>
     * A correspondência é feita por nome exato dos campos (case-sensitive). Campos duplicados (existentes
     * em ambas as origens) são preferencialmente extraídos de {@code obj1}.
     * </p>
     *
     * @param obj1        Primeiro objeto de origem (ex: entidade principal).
     * @param obj2        Segundo objeto de origem (ex: entidade complementar).
     * @param resultClass Classe da instância de resultado que deve conter a união dos campos.
     * @param <A>         Tipo do primeiro objeto.
     * @param <B>         Tipo do segundo objeto.
     * @param <R>         Tipo do objeto de resultado.
     * @return Uma instância de {@code resultClass} com todos os campos preenchidos a partir de {@code obj1} e {@code obj2}.
     *
     * @throws MergeEngineException Se:
     *                              <ul>
     *                                <li>Algum campo da {@code resultClass} não existir em {@code obj1} nem {@code obj2}.</li>
     *                                <li>Algum campo de {@code obj1} ou {@code obj2} não existir em {@code resultClass}.</li>
     *                                <li>Algum erro de reflexão ocorrer durante a cópia.</li>
     *                              </ul>
     */
    public static <A, B, R> R mergeIntoThirdObject(A obj1, B obj2, Class<R> resultClass) {
        if (obj1 == null || obj2 == null || resultClass == null) {
            throw new MergeEngineException("Objetos e classe de resultado não podem ser nulos");
        }

        try {
            R result = resultClass.getDeclaredConstructor().newInstance();

            // Campos dos objetos de origem
            Set<String> obj1Fields = Arrays.stream(obj1.getClass().getDeclaredFields())
                    .peek(f -> f.setAccessible(true))
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            Set<String> obj2Fields = Arrays.stream(obj2.getClass().getDeclaredFields())
                    .peek(f -> f.setAccessible(true))
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            // Preenche os campos do objeto resultado
            for (Field targetField : resultClass.getDeclaredFields()) {
                targetField.setAccessible(true);
                String name = targetField.getName();
                Field sourceField = null;
                Object sourceValue = null;

                if (obj1Fields.contains(name)) {
                    sourceField = obj1.getClass().getDeclaredField(name);
                    sourceField.setAccessible(true);
                    sourceValue = sourceField.get(obj1);
                } else if (obj2Fields.contains(name)) {
                    sourceField = obj2.getClass().getDeclaredField(name);
                    sourceField.setAccessible(true);
                    sourceValue = sourceField.get(obj2);
                } else {
                    throw new MergeEngineException("Campo '" + name + "' do resultado não existe em nenhum dos objetos de origem");
                }

                targetField.set(result, sourceValue);
            }

            // Validação de cobertura completa
            Set<String> resultFields = Arrays.stream(resultClass.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            for (String field : obj1Fields) {
                if (!resultFields.contains(field)) {
                    throw new MergeEngineException("Campo '" + field + "' de obj1 não está presente na classe de resultado");
                }
            }
            for (String field : obj2Fields) {
                if (!resultFields.contains(field)) {
                    throw new MergeEngineException("Campo '" + field + "' de obj2 não está presente na classe de resultado");
                }
            }

            return result;
        } catch (Exception e) {
            throw new MergeEngineException("Erro ao mesclar objetos complementares em resultado: " + e.getMessage());
        }
    }

    private static Object mergeCollections(Object baseValue, Object overrideValue,
                                           Class<? extends Annotation> annotationClass, Field field)
            throws Exception {
        if (baseValue == null || overrideValue == null) {
            return (overrideValue != null) ? overrideValue : baseValue;
        }

        Collection<?> baseCollection = (Collection<?>) baseValue;
        Collection<?> overrideCollection = (Collection<?>) overrideValue;

        Class<?> itemType = getCollectionItemType(field);

        if (!isPojo(itemType)) {
            return overrideCollection;
        }

        Field idField = getMergeIdField(itemType);
        if (idField == null) {
            throw new IllegalStateEngineException("Nenhum campo com @MergeId ou @Id encontrado em " + itemType.getSimpleName());
        }
        idField.setAccessible(true);

        Map<Object, Object> baseMap = new HashMap<>();
        for (Object baseItem : baseCollection) {
            Object id = idField.get(baseItem);
            if (id == null) {
                throw new IllegalStateEngineException("Item do base collection com id nulo: " + field);
            }
            baseMap.put(id, baseItem);
        }

        List<Object> mergedList = new ArrayList<>();

        for (Object overrideItem : overrideCollection) {
            Object id = idField.get(overrideItem);
            if (id == null) {
                throw new IllegalStateEngineException("Item do override collection com id nulo: " + field);
            }
            Object baseItem = baseMap.get(id);
            if (baseItem != null) {
                Object mergedItem = mergeWithAnnotation(baseItem, overrideItem, annotationClass);
                mergedList.add(mergedItem);
                baseMap.remove(id);
            } else {
                mergedList.add(overrideItem);
            }
        }

        mergedList.addAll(baseMap.values());

        return mergedList;
    }

    private static Field getMergeIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(MergeId.class)) {
                return field;
            }
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
    }

    // Recupera o tipo de item da Collection
    private static Class<?> getCollectionItemType(Field field) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        return (Class<?>) listType.getActualTypeArguments()[0];
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
            throw new ContextMappingEngineError("Erro ao mapear objeto: " + clazz.getSimpleName() + e.getMessage());
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
            throw new ContextMappingEngineError("Erro ao mapear objeto: " + clazz.getSimpleName() + e.getMessage());
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
        throw new IllegalArgumentEngineException("Collection não suportada: " + collectionType);
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


