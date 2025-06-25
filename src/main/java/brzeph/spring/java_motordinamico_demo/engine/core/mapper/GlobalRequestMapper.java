package brzeph.spring.java_motordinamico_demo.engine.core.mapper;

import brzeph.spring.java_motordinamico_demo.engine.core.annotation.OperationContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;

@Component
@ControllerAdvice
public class GlobalRequestMapper extends RequestBodyAdviceAdapter {

    @Autowired
    private AnnotationBasedMapper mapper;

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override @NonNull
    public Object afterBodyRead(@NonNull Object body,
                                @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter,
                                @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
// Lógica comentada, este method é aplicado pós Jackson, responsabilidade a baixo passada para
// CustomContextualMessageConverter.read().
//         /*
//        Todos os parâmetros do método são não nulos por definição do spring. Para mais detalhes, ver anotações.
//         */
//        try {
//            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            if (attrs == null) {
//                throw new IllegalStateException("No request context available");
//            }
//            HttpServletRequest request = attrs.getRequest();
//            String method = request.getMethod();
//            OperationContext context = OperationContext.valueOf(method);
//
//            return mapper.mapForContext(body, body.getClass(), context);
//        }
//        catch (IllegalArgumentException e){ // Erro na definição de contexto.
//            System.out.println("Unregistered method type: " + e.getMessage());
//            return body;
//        }
        return body;
    }
}

