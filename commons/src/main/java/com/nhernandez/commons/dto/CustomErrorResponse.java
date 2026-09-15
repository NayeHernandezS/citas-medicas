package com.nhernandez.commons.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}
