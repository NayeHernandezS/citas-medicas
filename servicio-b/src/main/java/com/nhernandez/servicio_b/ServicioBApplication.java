package com.nhernandez.servicio_b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServicioBApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioBApplication.class, args);
	}

}
