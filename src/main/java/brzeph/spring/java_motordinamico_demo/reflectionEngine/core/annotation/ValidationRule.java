package brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation;

import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.validations.NotBlank;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.validations.NotNull;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.validations.Required;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.exceptions.ValidationEngineException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;

public enum ValidationRule {

    REQUIRED(Required.class, (field, value) -> {
        if (value == null) {
            throw new ValidationEngineException("Campo obrigatório não informado: " + field.getName());
        }
    }),

    NOT_NULL(NotNull.class, (field, value) -> {
        if (value == null) {
            throw new ValidationEngineException("Campo não pode ser nulo: " + field.getName());
        }
    }),

    NOT_BLANK(NotBlank.class, (field, value) -> {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            throw new ValidationEngineException("Campo não pode estar em branco: " + field.getName());
        }
    });

    private final Class<? extends Annotation> annotationClass;
    private final BiConsumer<Field, Object> validator;

    ValidationRule(Class<? extends Annotation> annotationClass, BiConsumer<Field, Object> validator) {
        this.annotationClass = annotationClass;
        this.validator = validator;
    }

    public static Optional<ValidationRule> fromAnnotation(Annotation annotation) {
        return Arrays.stream(values())
                .filter(v -> v.annotationClass.equals(annotation.annotationType()))
                .findFirst();
    }

    public void validate(Field field, Object value) {
        validator.accept(field, value);
    }
}