package brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation;

import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Create;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Update;

import java.lang.annotation.Annotation;

public enum OperationContext {
    GET(Read.class),    // Context annotation.
    POST(Create.class), // Context annotation.
    PUT(Update.class);  // Context annotation.
    /*
    Se o parâmetro não tem annotation de contexto (@Create, @Read ou @Update por enquanto) ->
    ele não terá o no objeto ->
    não faz sentido fazer validações em cima dele, pois ele não existe.

    Logo, não é preciso se preocupar com um parâmetro ter validação X para contextos Y e Z enquanto
    não tem validação X para contexto A.

    Parâmetros serão validados apenas para entrada de dados (@Create e @Post), então métodos com
    campos que precisam ser validados (ex: garantir que Id está preenchido) serão validados apenas na entrada
    de dados. Parâmetros sem @Create não serão validados para entradas do tipo Post (@Create).
     */

    private final Class<? extends Annotation> annotationClass;

    OperationContext(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public String toString() {
        return "OperationContext{" +
                "annotationClass=" + annotationClass +
                '}';
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }
}