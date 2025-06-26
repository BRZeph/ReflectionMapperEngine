package brzeph.spring.java_motordinamico_demo.engine.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler dedicado ao motor de mapeamento/reflexão.
 * Trata exceções lançadas pelo ReflectionMapperEngine de forma padronizada e separada
 * do resto do sistema.
 */
@ControllerAdvice
public class EngineGlobalExceptionHandler {

    /**
     * Trata exceções de merge/reflexão de objetos.
     *
     * @param ex a exceção de merge lançada
     * @return resposta HTTP 400 com a mensagem de erro
     */
    @ExceptionHandler(MergeEngineException.class)
    public ResponseEntity<String> handleMergeEngineException(MergeEngineException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Erro de mapeamento/merge da engine: " + ex.getMessage());
    }

    /**
     * Trata falhas de validação lançadas pela engine.
     *
     * @param ex a exceção de validação lançada
     * @return resposta HTTP 422 com a mensagem de erro
     */
    @ExceptionHandler(ValidationEngineException.class)
    public ResponseEntity<String> handleValidationException(ValidationEngineException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("Erro de validação: " + ex.getMessage());
    }

    /**
     * Trata falhas genéricas lançadas pela engine
     *
     * @param ex a exceção de validação lançada
     * @return resposta HTTP 422 com a mensagem de erro
     */
    @ExceptionHandler(GenericEngineError.class)
    public ResponseEntity<String> handleValidationException(GenericEngineError ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + ex.getMessage());
    }
    @ExceptionHandler(ContextMappingEngineError.class)
    public ResponseEntity<String> handleValidationException(ContextMappingEngineError ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + ex.getMessage());
    }
    @ExceptionHandler(HttpMessageConversionEngineException.class)
    public ResponseEntity<String> handleValidationException(HttpMessageConversionEngineException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + ex.getMessage());
    }
    @ExceptionHandler(IllegalArgumentEngineException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentEngineException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + ex.getMessage());
    }
    @ExceptionHandler(IllegalStateEngineException.class)
    public ResponseEntity<String> handleValidationException(IllegalStateEngineException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + ex.getMessage());
    }
}

