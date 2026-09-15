package com.nhernandez.msv.medicos.service;

import com.nhernandez.commons.dto.medicos.MedicoRequest;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.services.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
