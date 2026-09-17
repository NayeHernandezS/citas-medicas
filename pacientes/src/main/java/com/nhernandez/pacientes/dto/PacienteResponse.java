package com.nhernandez.pacientes.dto;

import com.nhernandez.commons.enums.EstadoRegistro;
import io.swagger.v3.oas.annotations.media.Schema;

public record PacienteResponse(
        Long id,
        @Schema(description = "Nombre del paciente", example = "Nayely")
        String nombre,

        @Schema(description = "Apellido paterno del paciente", example = "Hernandez")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del paciente", example = "Silva")
        String apellidoMaterno,

        @Schema(description = "Direccion del paciente", example = "Morelos calle Ocampo N-8")
        String direccion,

        @Schema(description = "Edad del paciente", example = "29")
        Integer edad,

        @Schema(description = "Peso en kg del paciente", example = "60kg")
        Double peso,

        @Schema(description = "Estatura del paciente", example = "1.60")
        Double estatura,

        @Schema(description = "El numero telefonico del paciente", example = "7352713050")
        String telefono,

        @Schema(description = "El correo del paciente", example = "naye20_97@gmail.com")
        String email,

        @Schema(description = "EL imc del paciente", example = "65")
        Double imc,

        @Schema(description = "Numero de expediente del paciente", example = "L5032")
        String numeroExpediente,

        @Schema(description = "Estado (Activo, eliminado) del paciente")
        EstadoRegistro estado
) {
}
