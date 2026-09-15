package com.nhernandez.pacientes.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
