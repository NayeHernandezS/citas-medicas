package com.nhernandez.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PacienteRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        @Schema(description = "Nombre del paciente", example = "Nayely")
        String nombre,

        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        @Schema(description = "Apellido paterno del paciente", example = "Hernandez")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        @Schema(description = "Apellido materno del paciente", example = "Silva")
        String apellidoMaterno,

        @NotBlank(message = "La dirección es requerida")
        @Size(min = 1, max = 150, message = "La dirección debe tener entre 1 y 150 caracteres")
        @Schema(description = "Direccion del paciente", example = "Morelos calle Ocampo N-8")
        String direccion,

        @NotNull(message = "La edad es requerida")
        @Min(value = 1, message = "La edad mínima es 1")
        @Max(value = 100, message = "La edad máxima es 100")
        @Schema(description = "Edad del paciente", example = "29")
        Integer edad,

        @NotNull(message = "El peso es requerido")
        @DecimalMin(value = "0.1", message = "El peso mínimo es 0.1 kg")
        @DecimalMax(value = "200.0", message = "El peso máximo es 200 kg")
        @Schema(description = "Peso en kg del paciente", example = "60kg")
        Double peso,

        @NotNull(message = "La estatura es requerida")
        @DecimalMin(value = "1.0", message = "La estatura mínima es 1.0 m")
        @DecimalMax(value = "2.0", message = "La estatura máxima es 2.0 m")
        @Schema(description = "Estatura del paciente", example = "1.60")
        Double estatura,

        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener exactamente 10 dígitos numéricos")
        @Schema(description = "El numero telefonico del paciente", example = "7352713050")
        String telefono,

        @NotBlank(message = "El email es requerido")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 100, message = "El email no puede superar 100 caracteres")
        @Schema(description = "El correo del paciente", example = "naue20_97@gmail.com")
        String email
) {
}
