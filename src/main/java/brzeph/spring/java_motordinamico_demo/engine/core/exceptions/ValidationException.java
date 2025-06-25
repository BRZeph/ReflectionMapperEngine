package brzeph.spring.java_motordinamico_demo.engine.core.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
