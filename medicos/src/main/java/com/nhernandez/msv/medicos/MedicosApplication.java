package com.nhernandez.msv.medicos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nhernandez.msv.medicos", "com.nhernandez.commons"})
public class MedicosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicosApplication.class, args);
	}

}
