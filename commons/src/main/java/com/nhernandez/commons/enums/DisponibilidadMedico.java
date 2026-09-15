package com.nhernandez.commons.enums;

import com.nhernandez.commons.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DisponibilidadMedico {

    DISPONIBLE(1L, "Disponible para atender pacientes"),
    EN_CONSULTA(2L, "Atendiendo a un paciente actualmente"),
    FUERA_DE_TURNO(3L, "No se encuentra en turno"),
    DE_GUARDIA(4L, "Disponible bajo guardia"),
    NO_DISPONIBLE(5L, "No disponible por el momento");

    private final Long codigo;
    private final String descripcion;
    public static DisponibilidadMedico findByCodigo(Long codigo) {
        for (DisponibilidadMedico disponibilidadMedico : values()) {
            if (disponibilidadMedico.getCodigo().equals(codigo)) {
                return disponibilidadMedico;
            }
        }
        throw new RecursoNoEncontradoException("Codigo de disponibilidad no valido: " + codigo);
    }
}
