package com.nhernandez.msv.citas.controller;

import com.nhernandez.commons.controller.CrudController;
import com.nhernandez.commons.dto.citas.CitaRequest;
import com.nhernandez.commons.dto.citas.CitaResponse;
import com.nhernandez.msv.citas.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "API Citas", description = "Metodos para la gestión de citas")
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService> {
    public CitaController(CitaService service) {
        super(service);
    }

    @Operation(
            summary = "Actualizar estado de la cita",
            description = "Actualiza el estado de una cita utilizando el identificador de la cita y el identificador del nuevo estado"
    )
    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El id de la cita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El id estado debe ser positivo") Long idEstado
    ) {
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medico/{idMedico}/citas-confirmada-en-curso")
    @Operation(summary = "Indica si el médico tiene citas CONFIRMADA o EN_CURSO")
    public ResponseEntity<Boolean> medicoTieneCitasConfirmadaOEnCurso(
            @PathVariable @Positive(message = "El id del médico debe ser positivo") Long idMedico
    ) {
        return ResponseEntity.ok(service.medicoTieneCitasConfirmadaOEnCurso(idMedico));
    }

    @GetMapping("/medico/{idMedico}/citas-activas")
    @Operation(summary = "Indica si el médico tiene citas PENDIENTE, CONFIRMADA o EN_CURSO")
    public ResponseEntity<Boolean> medicoTieneCitasActivas(
            @PathVariable @Positive(message = "El id del médico debe ser positivo") Long idMedico
    ) {
        return ResponseEntity.ok(service.medicoTieneCitasActivas(idMedico));
    }

    @GetMapping("/paciente/{idPaciente}/citas-confirmada-en-curso")
    @Operation(summary = "Indica si el paciente tiene citas CONFIRMADA o EN_CURSO")
    public ResponseEntity<Boolean> pacienteTieneCitasConfirmadaOEnCurso(
            @PathVariable @Positive(message = "El id del paciente debe ser positivo") Long idPaciente
    ) {
        return ResponseEntity.ok(service.pacienteTieneCitasConfirmadaOEnCurso(idPaciente));
    }
}
