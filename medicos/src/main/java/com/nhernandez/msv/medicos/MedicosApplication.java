package com.nhernandez.msv.medicos;

import com.nhernandez.commons.exceptions.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.nhernandez.msv.medicos", "com.nhernandez.commons.exceptions"})
@Import(GlobalExceptionHandler.class)
public class MedicosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicosApplication.class, args);
	}

}
