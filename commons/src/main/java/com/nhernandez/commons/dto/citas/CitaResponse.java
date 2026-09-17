package com.nhernandez.commons.dto.citas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhernandez.commons.dto.medicos.DatosMedico;
import com.nhernandez.commons.dto.pacientes.DatosPaciente;
import com.nhernandez.commons.enums.EstadoCita;
import com.nhernandez.commons.enums.EstadoRegistro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema
public record CitaResponse(
        Long id,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        @Schema(description = "Fecha y hora de la cita", example = "20/09/2026 10:00", type = "string")
        LocalDateTime fechaCita,

        @Schema(description = "Sintomas del paciente", example = "El paciente viene con dolor de cabeza y nausea desde hace 3 dias")
        String sintomas,

        @Schema(description = "El estado de actual del paciente para su cita", example = "EN_CONSULTA")
        EstadoCita estadoCita,

        @Schema(description = "Estado de registro del paciente", example = "ACTIVO")
        EstadoRegistro estadoRegistro,

        @Schema(description = "Datos del paciente", example = "Nayely Hernandez, 29 años, naye@gmail.com")
        DatosPaciente paciente,

        @Schema(description = "Datos del medico")
        DatosMedico medico
) {
}
