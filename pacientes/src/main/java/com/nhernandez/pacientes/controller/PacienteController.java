package com.nhernandez.pacientes.controller;

import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.pacientes.dto.PacienteResponse;
import com.nhernandez.pacientes.service.paciente.PacienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Tag(name = "API pacientes", description = "Metodos para la gestion de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> listar() {
        return ResponseEntity.ok(pacienteService.listar());
    }

    @GetMapping("/sinValidarEstado")
    public ResponseEntity<List<com.nhernandez.pacientes.dto.PacienteResponse>> listarSinValidarEstado() {
        return ResponseEntity.ok(pacienteService.listarSinValidarEstado());
    }

    @GetMapping("/sinValidarEstado/{id}")
    public ResponseEntity<com.nhernandez.pacientes.dto.PacienteResponse> obtenerSinValidarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerSinValidarEstado(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.nhernandez.pacientes.dto.PacienteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<com.nhernandez.pacientes.dto.PacienteResponse> registrar(@Valid @RequestBody PacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.nhernandez.pacientes.dto.PacienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequest request
    ) {
        return ResponseEntity.ok(pacienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<com.nhernandez.pacientes.dto.PacienteResponse> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.eliminar(id));
    }
}
