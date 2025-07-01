package brzeph.spring.java_motordinamico_demo.reflectionEngine.core.mapper.utils;

import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.annotation.apiCallType.Read;
import brzeph.spring.java_motordinamico_demo.reflectionEngine.core.mapper.Mapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

@ControllerAdvice
public class GlobalResponseMapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        /*
        Define que todas chamadas anotadas com @RestController serão intercetadas pelo beforeBodyWrite.
         */
        return returnType.getContainingClass().isAnnotationPresent(RestController.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) return null;
        return Mapper.mapToMapForX(body, Read.class);
    }
}
