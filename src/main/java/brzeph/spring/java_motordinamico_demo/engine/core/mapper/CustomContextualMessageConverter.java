package brzeph.spring.java_motordinamico_demo.engine.core.mapper;

import brzeph.spring.java_motordinamico_demo.engine.core.annotation.OperationContext;
import brzeph.spring.java_motordinamico_demo.engine.core.exceptions.HttpMessageConversionEngineException;
import brzeph.spring.java_motordinamico_demo.engine.core.exceptions.IllegalStateEngineException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomContextualMessageConverter implements HttpMessageConverter<Object> {

    @Autowired
    private AnnotationBasedMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return MediaType.APPLICATION_JSON.includes(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false; // Não estamos escrevendo aqui
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(MediaType.APPLICATION_JSON);
    }

    @Override
    public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
        String json = new BufferedReader(new InputStreamReader(inputMessage.getBody()))
                .lines().collect(Collectors.joining("\n"));

        Map<String, Object> rawMap = objectMapper.readValue(json, new TypeReference<>() {});

        // Inferir o method HTTP
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) throw new IllegalStateEngineException("No request context");

        String method = attrs.getRequest().getMethod();
        OperationContext context = OperationContext.valueOf(method);

        return mapper.mapForContext(rawMap, clazz, context);
    }

    @Override
    public void write(Object o, MediaType mediaType, HttpOutputMessage outputMessage) throws IOException {
        throw new HttpMessageConversionEngineException("Somente leitura suportada.");
    }
}
