package brzeph.spring.java_motordinamico_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaMotorDinamicoDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaMotorDinamicoDemoApplication.class, args);
	}
	/*
	TODO: tornar AnnotationBasedMapper.validateBody dinâmico dividindo OperationContext em OperationContext e Validations.
	TODO: fazer com que ao falhar uma validação, dar dump e retornar para o front (usando ExceptionHandler e tudo mais).
	 */
}
