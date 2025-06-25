package brzeph.spring.java_motordinamico_demo.engine.core.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import brzeph.spring.java_motordinamico_demo.engine.core.annotation.OperationContext;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.ValidationRule;
import brzeph.spring.java_motordinamico_demo.engine.core.annotation.validations.Required;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class AnnotationBasedMapper {

    public <T> T mapForContext(Map<String, Object> jsonMap, Class<T> targetClass, OperationContext context) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();

            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(context.getAnnotationClass()) && jsonMap.containsKey(field.getName())) {

                    Object rawValue = jsonMap.get(field.getName());

                    Object value = convertValue(rawValue, field.getType());

                    // Nested object recursion
                    if (isCustomClass(field.getType()) && value instanceof Map) {
                        Object nested = mapForContext((Map<String, Object>) value, field.getType(), context);
                        field.set(target, nested);
                    }
                    // Collection recursion
                    else if (Collection.class.isAssignableFrom(field.getType()) && value instanceof List) {
                        ParameterizedType listType = (ParameterizedType) field.getGenericType();
                        Class<?> itemType = (Class<?>) listType.getActualTypeArguments()[0];

                        List<Object> targetList = new ArrayList<>();
                        for (Object item : (List<?>) value) {
                            if (isCustomClass(itemType) && item instanceof Map) {
                                Object nested = mapForContext((Map<String, Object>) item, itemType, context);
                                targetList.add(nested);
                            } else {
                                targetList.add(convertValue(item, itemType));
                            }
                        }
                        field.set(target, targetList);
                    }
                    else {
                        field.set(target, value);
                    }
                }
            }

            validateBody(target, context);
            return target;

        } catch (Exception e) {
            throw new RuntimeException("Erro no mapeamento de contexto", e);
        }
    }

    public void validateBody(Object requestBody, OperationContext context) {
        if (requestBody == null) return;
        for (Field field : requestBody.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(requestBody);
                if (field.isAnnotationPresent(context.getAnnotationClass())) {
                    for (Annotation annotation : field.getDeclaredAnnotations()) {
                        ValidationRule.fromAnnotation(annotation)
                                .ifPresent(rule -> rule.validate(field, value));
                    }
                }

                if (value != null) {
                    if (isCustomClass(field.getType())) {
                        validateBody(value, context);
                    } else if (value instanceof Collection<?>) {
                        for (Object item : (Collection<?>) value) {
                            if (item != null && isCustomClass(item.getClass())) {
                                validateBody(item, context);
                            }
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erro ao validar campo: " + field.getName(), e);
            }
        }
    }

    private Object convertValue(Object rawValue, Class<?> targetType) {
        if (rawValue == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.convertValue(rawValue, targetType);
    }

    private boolean isCustomClass(Class<?> clazz) {
        return clazz.getPackage() != null && !clazz.getPackage().getName().startsWith("java");
    }
}