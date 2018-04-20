package nl.smith.mathematics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nl.smith.mathematics.services.ArithmeticExpressionService;

@SpringBootApplication
public class MathematicsV2Application implements CommandLineRunner {

	private final Logger LOGGER = LoggerFactory.getLogger(MathematicsV2Application.class);

	@Autowired
	private ArithmeticExpressionService arithmeticExpressionService;

	public static void main(String[] args) {
		SpringApplication.run(MathematicsV2Application.class, args);
	}

	@Override
	public void run(String... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException {
	}
}
