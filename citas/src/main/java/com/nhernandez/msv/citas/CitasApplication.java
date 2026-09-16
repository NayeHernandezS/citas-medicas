package com.nhernandez.msv.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.nhernandez.msv.citas", "com.nhernandez.commons"})
public class CitasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasApplication.class, args);
	}

}
