package com.nhernandez.commons.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitasClient {

    @GetMapping("/medico/{idMedico}/citas-confirmada-en-curso")
    Boolean medicoTieneCitasConfirmadaOEnCurso(@PathVariable("idMedico") Long idMedico);
}
