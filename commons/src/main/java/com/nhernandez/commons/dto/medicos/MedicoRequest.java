package com.nhernandez.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema
public record MedicoRequest(

        @NotBlank(message = "El nombre es requerisdo")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        @Schema(description = "Nombre del médico", example = "Juan")
        String nombre,

        @NotBlank(message = "El apellido paterno es requerisdo")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        @Schema(description = "Apellido paterno del médico", example = "Perez")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es requerisdo")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        @Schema(description = "Apellido materno del médico", example = "Lopez")
        String apellidoMaterno,

        @NotNull(message = "La edad es requerida")
        @Min(value = 18, message = "La edad minima es 18")
        @Max(value = 100, message = "La esda maxima es 100")
        @Schema(description = "Edad del médico", example = "35")
        Short edad,

        @NotBlank(message = "El email es requerido")
        @Email(message = "El email no tiene un formato válido")
        @Size(min = 1, max = 100, message = "El email no puede superar 100 caracteres")
        @Schema(description = "Correo del médico", example = "juan.perez@hospital.com")
        String email,

        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener solo 10 digitos")
        @Schema(description = "Teléfono del médico", example = "7352713050")
        String telefono,

        @NotBlank(message = "La cedula es requerida")
        @Size(min = 12, max = 12, message = "La cedutal debe tener exactamente 12 digitos")
        @Schema(description = "Cédula profesional del médico", example = "123456789012")
        String cedulaProfesional,

        @NotNull(message = "El id de especialidad es requerido")
        @Positive(message = "El id de especialidad debe ser positivo")
        @Schema(description = "Identificador de la especialidad del médico", example = "1")
        Long idEspecialidad
) {
}
