package com.nhernandez.pacientes.mappers;

import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.pacientes.dto.PacienteResponse;
import com.nhernandez.pacientes.entities.Paciente;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {

    public PacienteResponse entidadResponse(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellidoPaterno(),
                paciente.getApellidoMaterno(),
                paciente.getDireccion(),
                paciente.getEdad(),
                paciente.getPeso(),
                paciente.getEstatura(),
                paciente.getTelefono(),
                paciente.getEmail(),
                paciente.getImc(),
                paciente.getNumeroExpediente(),
                paciente.getEstado()
        );
    }

    public Paciente requestToEntity(PacienteRequest request) {
        if (request == null) {
            return null;
        }
        return Paciente.crear(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.direccion(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.telefono(),
                request.email()
        );
    }

    public void actualizarEntidad(Paciente paciente, PacienteRequest request) {
        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.direccion(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.telefono(),
                request.email()
        );
    }
}
