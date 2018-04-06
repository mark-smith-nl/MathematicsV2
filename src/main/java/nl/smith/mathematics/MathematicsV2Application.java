package nl.smith.mathematics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nl.smith.mathematics.domain.ArithmeticExpression;
import nl.smith.mathematics.services.ArithmeticExpressionService;

@SpringBootApplication
public class MathematicsV2Application implements CommandLineRunner {

	@Autowired
	private ArithmeticExpressionService arithmeticExpressionService;

	public static void main(String[] args) {
		SpringApplication.run(MathematicsV2Application.class, args);
	}

	@Override
	public void run(String... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException {
		ArithmeticExpression buildArithmeticExpression = arithmeticExpressionService.buildArithmeticExpression("2+3*(5+6)+7*(8-9)");
		System.out.println(buildArithmeticExpression);
	}
}
