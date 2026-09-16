package com.nhernandez.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

public record DatosMedico(

        @Schema(description = "Nombre completo del medico", example = "Nayely Hernandez")
        String nombre,
        @Schema(description = "Cedula profesional del medico", example = "1234567890")
        String cedulaProfesional,
        @Schema(description = "Nombre de la especiaidad medica del medico", example = "Cardiologia")
        String especialidad
) {


}
