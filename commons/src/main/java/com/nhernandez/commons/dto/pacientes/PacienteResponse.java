package com.nhernandez.commons.dto.pacientes;

import com.fasterxml.jackson.annotation.JsonAlias;

public record PacienteResponse(
        Long id,
        String nombre,
        @JsonAlias("numeroExpediente")
        String numExpediente,
        Integer edad,
        double peso,
        Double estatura,
        Double imc,
        String telefono
) {
}
