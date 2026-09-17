package com.nhernandez.msv.citas.service;

import com.nhernandez.commons.services.CrudService;
import com.nhernandez.commons.dto.citas.CitaRequest;
import com.nhernandez.commons.dto.citas.CitaResponse;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {

    CitaResponse obtenerPorIdSinEstado(Long id);

    void actualizarEstadoCita(Long idCita, Long idEstado);
}
