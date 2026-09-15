package com.nhernandez.pacientes;

import com.nhernandez.commons.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.nhernandez.pacientes", "com.nhernandez.commons.exceptions"})
@Import(GlobalExceptionHandler.class)
public class PacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesApplication.class, args);
	}

}
