package com.nhernandez.msv.citas.entity;

import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.commons.utils.StringCustomUtils;
import com.nhernandez.commons.utils.ValoresNumericosUtils;
import com.nhernandez.commons.enums.EstadoCita;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CITAS")
@NoArgsConstructor()
@AllArgsConstructor()
@Builder
@Getter
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_PACIENTE", nullable = false)
    private Long idPaciente;

    @Column(name = "ID_MEDICO", nullable = false)
    private Long idMedico;

    @Column(name = "FECHA_CITA", nullable = false)
    private LocalDateTime fechaCita;

    @Column(name = "SINTOMAS", nullable = false)
    private String sintomas;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_CITA", nullable = false)
    private EstadoCita estadoCita;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public static final List<EstadoCita> ESTADOS_ACTIVOS = List.of(
            EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);

    public static final List<EstadoCita> ESTADOS_CONFIRMADA_EN_CURSO = List.of(
            EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);

    private static void validarId(Long id, String campo){
        ValoresNumericosUtils.validarLongPositivo(id,
                "El id del " + campo + "es requerido y debe ser positivo");
    }

    private static void validarFecha(LocalDateTime fechaCita){
        if (fechaCita == null || fechaCita.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Fecha de cita es requerida y debe ser presente o futura");
        }
    }

    private void validarNoEliminada(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalStateException("La cita ya esta eliminada");
    }

    public static void  validarDatos(
            Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarId(idPaciente,"paciente");
        validarId(idMedico,"medico");
        validarFecha(fechaCita);

        StringCustomUtils.validarTamanio(sintomas, "Los sintomas son requeridos y debe tener entre 20 y 500 caracteres", 20, 500);
    }

    private void validarEliminacionPermitida(){
        validarNoEliminada();

        if (!estadoCita.isEliminable())
            throw new IllegalStateException(
                    "La cita con estado " + estadoCita + " no puede eliminarse");
    }

    private void validarActualizacionPermitida(){
        validarNoEliminada();

        if (!estadoCita.isActualizable())
            throw  new IllegalStateException("La cita con estado " + estadoCita + " no puede ser actualizable");
    }


    public void actualizar(
            Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas){
        validarActualizacionPermitida();
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.fechaCita = fechaCita;
        this.sintomas = sintomas.trim();
    }

    public void eliminar() {
        validarEliminacionPermitida();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
        this.estadoCita = EstadoCita.CANCELADA;
    }

    public void actualizarEstado(EstadoCita nuevoEstado) {
        validarNoEliminada();
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado de la cita es requerido");
        }
        if (!this.estadoCita.puedeCambiarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "No se puede cambiar la cita de " + this.estadoCita + " a " + nuevoEstado);
        }
        this.estadoCita = nuevoEstado;
    }

    public static Cita crear(
            Long idPaciente, Long idMedico, LocalDateTime fechaCita, String sintomas) {
        validarDatos(idPaciente, idMedico, fechaCita, sintomas);

        return Cita.builder()
                .idPaciente(idPaciente)
                .idMedico(idMedico)
                .fechaCita(fechaCita)
                .sintomas(sintomas.trim())
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
