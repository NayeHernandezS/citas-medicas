package com.nhernandez.pacientes.controller;

import com.nhernandez.commons.controller.CrudController;
import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.pacientes.dto.PacienteResponse;
import com.nhernandez.pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Tag(name = "API pacientes", description = "Metodos para la gestion de pacientes")
public class PacienteController extends CrudController<PacienteRequest, PacienteResponse, PacienteService> {

    public PacienteController(PacienteService service) {
        super(service);
    }

    /** Lista todos, incluido ELIMINADO. */
    @GetMapping("/sinValidarEstado")
    public ResponseEntity<List<PacienteResponse>> listarSinValidarEstado() {
        return ResponseEntity.ok(service.listarSinValidarEstado());
    }

    /** Obtiene por id aunque el registro esté ELIMINADO. */
    @GetMapping("/sinValidarEstado/{id}")
    public ResponseEntity<PacienteResponse> obtenerSinValidarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerSinValidarEstado(id));
    }
}
