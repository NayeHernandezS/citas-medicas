package com.nhernandez.pacientes.service.paciente;

import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.pacientes.dto.PacienteResponse;

import java.util.List;

public interface PacienteService {

    List<PacienteResponse> listar();

    List<PacienteResponse> listarSinValidarEstado();

    PacienteResponse obtener(Long id);

    PacienteResponse obtenerSinValidarEstado(Long id);

    PacienteResponse registrar(PacienteRequest request);

    PacienteResponse actualizar(Long id, PacienteRequest request);

    PacienteResponse eliminar(Long id);
}
