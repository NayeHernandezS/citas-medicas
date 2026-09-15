package com.nhernandez.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

public record MedicoResponse(
        @Schema(description = "Identificador del médico", example = "1")
        Long id,

        @Schema(description = "Nombre del médico", example = "Juan")
        String nombre,

        @Schema(description = "Edad del médico", example = "35")
        Short edad,

        @Schema(description = "Correo del médico", example = "juan.perez@hospital.com")
        String email,

        @Schema(description = "Teléfono del médico", example = "7352713050")
        String telefono,

        @Schema(description = "Cédula profesional del médico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "Especialidad del médico", example = "Cardiología")
        String especialidad,

        @Schema(description = "Disponibilidad del médico", example = "Disponible para atender pacientes")
        String disponibilidad,

        @Schema(description = "Identificador de la disponibilidad del médico", example = "1")
        Long idDisponibilidad
) {
}
