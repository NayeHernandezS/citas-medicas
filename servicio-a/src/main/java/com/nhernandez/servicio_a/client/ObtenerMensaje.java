package com.nhernandez.servicio_a.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "servicio-b")
public interface ObtenerMensaje {

    @GetMapping("/mensaje")
    String obtenerMensaje();
}
