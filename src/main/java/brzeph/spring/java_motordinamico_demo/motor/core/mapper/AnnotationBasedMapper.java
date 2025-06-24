package brzeph.spring.java_motordinamico_demo.motor.core.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import brzeph.spring.java_motordinamico_demo.motor.core.annotation.OperationContext;
import brzeph.spring.java_motordinamico_demo.motor.core.annotation.validations.Required;
import org.springframework.stereotype.Component;

@Component
public class AnnotationBasedMapper {
    /**
     * Serviço responsável por delegar o mapeamento de um objeto conforme o contexto de operação.
     * <p>
     * Este componente centraliza a chamada ao motor ReflectionMapperEngine, determinando a
     * anotação correspondente a ser aplicada com base no contexto (ex.: Create → @Create).
     */
    public <T> T mapForContext(Object source, Class<T> targetClass, OperationContext context) {
        if (source == null) return null;
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(context.getAnnotationClass())) {
                    Field sourceField = source.getClass().getDeclaredField(field.getName());
                    sourceField.setAccessible(true);
                    Object value = sourceField.get(source);

                    // Nested object recursion
                    if (isCustomClass(field.getType()) && value != null) {
                        Object nested = mapForContext(value, field.getType(), context);
                        field.set(target, nested);
                    }
                    // List recursion
                    else if (Collection.class.isAssignableFrom(field.getType()) && value != null) {
                        Collection<?> sourceList = (Collection<?>) value;
                        List<Object> targetList = new ArrayList<>();
                        for (Object item : sourceList) {
                            if (item != null) {
                                Object nested = mapForContext(item, item.getClass(), context);
                                targetList.add(nested);
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
        } catch (IllegalAccessException | NoSuchFieldException | InvocationTargetException | InstantiationException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateBody(Object obj, OperationContext context) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(context.getAnnotationClass()) && field.isAnnotationPresent(Required.class)) {
                try {
                    if (field.get(obj) == null) {
                        // ESTÁ FALHANDO AQUI
                        throw new RuntimeException("Field required: " + field.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean isCustomClass(Class<?> clazz) {
        return !(clazz.isPrimitive() || clazz.getName().startsWith("java."));
    }
}
