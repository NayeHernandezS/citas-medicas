package com.nhernandez.msv.medicos.mapper;

import com.nhernandez.commons.dto.medicos.MedicoRequest;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.enums.DisponibilidadMedico;
import com.nhernandez.commons.enums.EspecialidadMedico;
import com.nhernandez.commons.enums.EstadoPaciente;
import com.nhernandez.commons.mappers.CommonMapper;
import com.nhernandez.msv.medicos.entity.Medico;
import org.springframework.stereotype.Component;

@Component
public class MedicoMapper implements CommonMapper<MedicoRequest, MedicoResponse, Medico> {

    @Override
    public Medico requestEntidad(MedicoRequest request) {
        if (request == null) {
            return null;
        }

        EspecialidadMedico especialidad = EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad());

        return Medico.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .email(request.email().trim())
                .telefono(request.telefono().trim())
                .cedulaProfesional(request.cedulaProfesional().trim())
                .especialidadMedico(especialidad)
                .disponibilidadMedico(DisponibilidadMedico.DISPONIBLE)
                .estadoRegistro(EstadoPaciente.ACTIVO)
                .build();
    }

    @Override
    public MedicoResponse entidadResponse(Medico entidad) {
        if (entidad == null) {
            return null;
        }

        EspecialidadMedico especialidad = entidad.getEspecialidadMedico();
        DisponibilidadMedico disponibilidad = entidad.getDisponibilidadMedico();

        return new MedicoResponse(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getEdad(),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getCedulaProfesional(),
                especialidad != null ? especialidad.getDescripcion() : null,
                disponibilidad != null ? disponibilidad.getDescripcion() : null,
                disponibilidad != null ? disponibilidad.getCodigo() : null
        );
    }
}
