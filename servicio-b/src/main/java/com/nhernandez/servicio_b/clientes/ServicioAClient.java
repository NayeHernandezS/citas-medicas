package com.nhernandez.servicio_b.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "servicio-a")
public interface ServicioAClient {

    @GetMapping("/saludo")
    String obtenerSaludo();
}
