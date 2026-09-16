package com.nhernandez.msv.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema
public record CitaRequest(
        @NotNull(message = "El id del paciente es requerido")
        @Positive(message = "El id del paciente debe ser positivo")
        @Schema(description = "Identificador del paciente", example = "1")
        Long idPaciente,

        @NotNull(message = "El id del médico es requerido")
        @Positive(message = "El id del médico debe ser positivo")
        @Schema(description = "Identificador del médico", example = "1")
        Long idMedico,

        @NotNull(message = "La fecha de la cita es requerida")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        @Schema(description = "Fecha y hora de la cita", example = "20/09/2026 10:00", type = "string")
        LocalDateTime fechaCita,

        @NotBlank(message = "Los síntomas son requeridos")
        @Size(min = 20, max = 500, message = "Los síntomas deben tener entre 20 y 500 caracteres")
        @Schema(description = "Síntomas del paciente", example = "Dolor de cabeza persistente desde hace tres días")
        String sintomas
) {
}
