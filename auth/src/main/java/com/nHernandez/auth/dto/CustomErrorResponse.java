package com.nHernandez.auth.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {}