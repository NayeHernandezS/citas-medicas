package com.nhernandez.msv.citas.mapper;

import com.nhernandez.commons.dto.medicos.DatosMedico;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.dto.pacientes.DatosPaciente;
import com.nhernandez.commons.dto.pacientes.PacienteResponse;
import com.nhernandez.commons.mappers.CommonMapper;
import com.nhernandez.commons.dto.citas.CitaRequest;
import com.nhernandez.commons.dto.citas.CitaResponse;
import com.nhernandez.msv.citas.entity.Cita;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {

    @Override
    public Cita requestEntidad(CitaRequest request) {
        if (request == null) {
            return null;
        }
        return Cita.crear(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );
    }

    @Override
    public CitaResponse entidadResponse(Cita entidad) {
        return entidadResponse(entidad, null, null);
    }

    public CitaResponse entidadResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        if (entidad == null) {
            return null;
        }
        return new CitaResponse(
                entidad.getId(),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita(),
                entidad.getEstadoRegistro(),
                aDatosPaciente(paciente),
                aDatosMedico(medico)
        );
    }

    private DatosPaciente aDatosPaciente(PacienteResponse paciente) {
        if (paciente == null) {
            return null;
        }
        return new DatosPaciente(
                paciente.nombre(),
                paciente.numExpediente(),
                paciente.edad() != null ? String.valueOf(paciente.edad()) : null,
                String.valueOf(paciente.peso()),
                paciente.estatura() != null ? String.valueOf(paciente.estatura()) : null,
                paciente.imc() != null ? String.valueOf(paciente.imc()) : null,
                paciente.telefono()
        );
    }

    private DatosMedico aDatosMedico(MedicoResponse medico) {
        if (medico == null) {
            return null;
        }
        return new DatosMedico(
                medico.nombre(),
                medico.cedulaProfesional(),
                medico.especialidad()
        );
    }
}
