package com.nhernandez.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

public record DatosPaciente(

        @Schema(description = "Nombre del paciente", example = "Naye Hernandez")
        String nombre,

        @Schema(description = "Numero del expediente del paciente", example = "1x2x3x45x6")
        String numExpediente,

        @Schema(description = "Edad del paciente", example = "20")
        String edad,

        @Schema(description = "Peso del paciente", example = "70.0")
        String peso,

        @Schema(description = "Estatura del paciente", example = "1.60")
        String estatura,

        @Schema(description = "IMC del paciente", example = "22.2 peso normal")
        String imc,

        @Schema(description = "Telefono del paciente", example = "123456789")
        String telefono
) {
}
