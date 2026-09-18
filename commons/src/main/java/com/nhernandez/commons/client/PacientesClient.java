package com.nhernandez.commons.client;

import com.nhernandez.commons.dto.pacientes.PacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pacientes")
public interface PacientesClient {

    @GetMapping("/{id}")
    PacienteResponse obtenerPacienteActivoPorId(@PathVariable("id") Long id);

    @GetMapping("/sinValidarEstado/{id}")
    PacienteResponse obtenerPacienteSinEstadoPorId(@PathVariable("id") Long id);
}
