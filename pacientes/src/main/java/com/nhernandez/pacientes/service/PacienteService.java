package com.nhernandez.pacientes.service;

import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.commons.services.CrudService;
import com.nhernandez.pacientes.dto.PacienteResponse;

import java.util.List;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {

    List<PacienteResponse> listarSinValidarEstado();

    PacienteResponse obtenerSinValidarEstado(Long id);
}
