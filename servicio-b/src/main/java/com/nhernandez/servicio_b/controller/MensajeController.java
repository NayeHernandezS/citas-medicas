package com.nhernandez.servicio_b.controller;

import com.nhernandez.servicio_b.clientes.ServicioAClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MensajeController {

    private final ServicioAClient servicioAClient;

    public MensajeController(ServicioAClient servicioAClient) {
        this.servicioAClient = servicioAClient;
    }

    @GetMapping
    public ResponseEntity<String> mensaje() {
        return ResponseEntity.ok("Hola desde servicio B");
    }

    @GetMapping("/servicio-a")
    public ResponseEntity<String> consumirA() {
        return ResponseEntity.ok("Servicio B dice: " + servicioAClient.obtenerSaludo());
    }
}
