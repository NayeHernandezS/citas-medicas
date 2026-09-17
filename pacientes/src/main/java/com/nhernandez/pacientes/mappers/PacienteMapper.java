package com.nhernandez.pacientes.mappers;

import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.pacientes.dto.PacienteResponse;
import com.nhernandez.pacientes.entities.Paciente;
import com.nhernandez.pacientes.util.PacienteReglas;
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
        Paciente paciente = new Paciente();
        aplicarDatos(paciente, request);
        paciente.setEstado(EstadoRegistro.ACTIVO);
        return paciente;
    }

    public void actualizarEntidad(Paciente paciente, PacienteRequest request) {
        aplicarDatos(paciente, request);
    }

    private void aplicarDatos(Paciente paciente, PacienteRequest request) {
        paciente.setNombre(request.nombre().trim());
        paciente.setApellidoPaterno(request.apellidoPaterno().trim());
        paciente.setApellidoMaterno(request.apellidoMaterno().trim());
        paciente.setDireccion(request.direccion().trim());
        paciente.setEdad(request.edad());
        paciente.setPeso(request.peso());
        paciente.setEstatura(request.estatura());
        paciente.setTelefono(request.telefono().trim());
        paciente.setEmail(request.email().trim());
        paciente.setImc(PacienteReglas.calcularImc(request.peso(), request.estatura()));
        paciente.setNumeroExpediente(PacienteReglas.generarNumeroExpediente(request.telefono().trim()));

    }
}
