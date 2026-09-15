package com.nhernandez.servicio_a.controller;

import com.nhernandez.servicio_a.client.ObtenerMensaje;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludoController {

    private final ObtenerMensaje servicioBClient;

    public SaludoController(ObtenerMensaje servicioBClient) {
        this.servicioBClient = servicioBClient;
    }

    @GetMapping
    public ResponseEntity<String> saludo() {
        return ResponseEntity.ok("Servicio A dice: Hola Mundo ");
    }

    @GetMapping("/servicio-b")
    public ResponseEntity<String> consumirB() {
        return ResponseEntity.ok("Servicio A dice: " + servicioBClient.obtenerMensaje());
    }
}
