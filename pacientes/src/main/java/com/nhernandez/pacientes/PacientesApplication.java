package com.nhernandez.pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nhernandez.pacientes", "com.nhernandez.commons"})
public class PacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesApplication.class, args);
	}

}
