package com.nhernandez.msv.citas.service;

import com.nhernandez.commons.client.MedicoClient;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.dto.pacientes.PacienteResponse;
import com.nhernandez.commons.enums.DisponibilidadMedico;
import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.commons.exceptions.RecursoNoEncontradoException;
import com.nhernandez.commons.client.PacientesClient;
import com.nhernandez.commons.dto.citas.CitaRequest;
import com.nhernandez.commons.dto.citas.CitaResponse;
import com.nhernandez.msv.citas.entity.Cita;
import com.nhernandez.commons.enums.EstadoCita;
import com.nhernandez.msv.citas.mapper.CitaMapper;
import com.nhernandez.msv.citas.repository.CitaRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacientesClient pacientesClient;

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        log.info("Listado de citas activas solicitado");
        return citaRepository.findByEstadoRegistroAndEstadoCitaNot(EstadoRegistro.ACTIVO, EstadoCita.CANCELADA).stream()
                .map(cita -> citaMapper.entidadResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerActiva(id);
        return citaMapper.entidadResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorIdSinEstado(Long id) {
        log.info("Obteniendo de citas sin estado activas solicitado");
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cita: " + id));
        return citaMapper.entidadResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando cita para paciente");
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitaActiva(request.idPaciente(), null);
        MedicoResponse medico = validarMedicoActivoDisponible(request.idMedico());
        Cita cita = citaMapper.requestEntidad(request);
        citaRepository.save(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), EstadoCita.PENDIENTE);
        return citaMapper.entidadResponse(
                cita,
                paciente,
                medico
        );

    }

    private MedicoResponse validarMedicoActivoDisponible(Long idMedico) {
        log.info("Validando medico disponible");
        MedicoResponse medico = obtenerMedicoActivo(idMedico);
        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad())) {
            throw new IllegalStateException("El médico " + idMedico + " no está disponible para agendar citas");
        }
        return medico;
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        log.info("Actualizando cita {}", id);
        Cita cita = obtenerActiva(id);
        Long medicoAnterior = cita.getIdMedico();
        boolean cambiaMedico = !medicoAnterior.equals(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitaActiva(request.idPaciente(), id);
        MedicoResponse medico = cambiaMedico
                ? validarMedicoActivoDisponible(request.idMedico())
                : obtenerMedicoSinEstado(medicoAnterior);

        cita.actualizar(request.idPaciente(), request.idMedico(), request.fechaCita(), request.sintomas());
        citaRepository.save(cita);

        if (cambiaMedico) {
            liberarMedicoSiNoTieneCitasActivas(medicoAnterior);
            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());
        }
        return citaMapper.entidadResponse(cita, paciente, medico);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cita {}", id);
        Cita cita = obtenerActiva(id);
        EstadoCita estadoAnterior = cita.getEstadoCita();
        Long idMedico = cita.getIdMedico();
        cita.eliminar();
        citaRepository.save(cita);
        if (estadoAnterior == EstadoCita.PENDIENTE) {
            liberarMedicoSiNoTieneCitasActivas(idMedico);
        }
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstado) {
        log.info("Actualizando estado de la cita {} a {}", idCita, idEstado);
        Cita cita = obtenerActiva(idCita);
        EstadoCita nuevoEstado = EstadoCita.obtenerEstadoCitaPorCodigo(idEstado);
        cita.actualizarEstado(nuevoEstado);
        citaRepository.save(cita);
        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), nuevoEstado);
    }

    private void validarPacienteSinCitaActiva(Long idPaciente, Long idCitaActual) {
        log.info("Validando paciente sin cita activa");
        List<EstadoCita> estadosActivos = List.of(
                EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);
        boolean tieneOtraCita = idCitaActual == null
                ? citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                        idPaciente, EstadoRegistro.ACTIVO, estadosActivos)
                : citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idPaciente, EstadoRegistro.ACTIVO, estadosActivos, idCitaActual);
        if (tieneOtraCita) {
            throw new IllegalStateException(
                    "El paciente " + idPaciente + " ya tiene una cita activa (PENDIENTE, CONFIRMADA o EN_CURSO)");
        }
    }

    private PacienteResponse obtenerPacienteActivo(Long idPaciente) {
        log.info("Obteniendo paciente activo");
        try {
            return pacientesClient.obtenerPacienteActivoPorId(idPaciente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el paciente activo: " + idPaciente);
        }
    }

    private PacienteResponse obtenerPacienteSinEstado(Long idPaciente) {
        log.info("Obteniendo paciente sin estado");
        try {
            return pacientesClient.obtenerPacienteSinEstadoPorId(idPaciente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el paciente: " + idPaciente);
        }
    }

    private Cita obtenerActiva(Long id) {
        log.info("Obteniendo cita activa");
        return citaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro la cita: " + id));
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {

        log.info("Cmabiando disponibilidad de medico segun estado cita");
        Long codigoDisponibilidad = estadoCita.codigoDisponibilidadMedico();
        if (DisponibilidadMedico.DISPONIBLE.getCodigo().equals(codigoDisponibilidad)
                && medicoTieneCitasActivas(idMedico)) {
            log.info("El medico {} no pasa a DISPONIBLE porque aun tiene citas activas", idMedico);
            return;
        }
        medicoClient.actualizarDisponibilidadMedico(idMedico, codigoDisponibilidad);
    }

    private void liberarMedicoSiNoTieneCitasActivas(Long idMedico) {
        if (medicoTieneCitasActivas(idMedico)) {
            log.info("El medico {} no se libera; aun tiene citas PENDIENTE, CONFIRMADA o EN_CURSO", idMedico);
            return;
        }
        medicoClient.actualizarDisponibilidadMedico(
                idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean medicoTieneCitasConfirmadaOEnCurso(Long idMedico) {
        log.info("Validando citas CONFIRMADA o EN_CURSO del medico {}", idMedico);
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean pacienteTieneCitasConfirmadaOEnCurso(Long idPaciente) {
        log.info("Validando citas CONFIRMADA o EN_CURSO del paciente {}", idPaciente);
        return citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                List.of(EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean medicoTieneCitasActivas(Long idMedico) {
        log.info("Citas activas de medico");
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO));
    }

    private MedicoResponse obtenerMedicoActivo(Long idMedico) {
        log.info("Obteniendo medico activo");
        try {
            return medicoClient.obtenerMedicoActivoPorId(idMedico);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el medico activo: " + idMedico);
        }
    }

    private MedicoResponse obtenerMedicoSinEstado(Long idMedico) {
        log.info("Obteniendo medico sin estado");
        try {
            return medicoClient.obtenerMedicoSinEstadoPorId(idMedico);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el medico: " + idMedico);
        }
    }
}