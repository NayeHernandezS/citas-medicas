package com.nhernandez.msv.citas.service;

import com.nhernandez.commons.client.MedicoClient;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.dto.pacientes.PacienteResponse;
import com.nhernandez.commons.enums.DisponibilidadMedico;
import com.nhernandez.commons.enums.EstadoPaciente;
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
        return citaRepository.findByEstadoRegistro(EstadoPaciente.ACTIVO).stream()
                .map(this::aCitaResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        return aCitaResponse(obtenerActiva(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorIdSinEstado(Long id) {
        log.info("Obteniendo de citas sin estado activas solicitado");
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cita: " + id));
        return aCitaResponse(cita);
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

        obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitaActiva(request.idPaciente(), id);
        if (cambiaMedico) {
            validarMedicoActivoDisponible(request.idMedico());
        }

        cita.actualizar(request.idPaciente(), request.idMedico(), request.fechaCita(), request.sintomas());
        citaRepository.save(cita);

        if (cambiaMedico) {
            liberarMedicoSiNoTieneCitasActivas(medicoAnterior);
            cambiarDisponibilidadMedicoSegunEstadoCita(request.idMedico(), cita.getEstadoCita());
        }
        return aCitaResponse(cita);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cita {}", id);
        Cita cita = obtenerActiva(id);
        cita.eliminar();
        citaRepository.save(cita);
        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), EstadoCita.CANCELADA);
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

    private CitaResponse aCitaResponse(Cita cita) {
        return citaMapper.entidadResponse(
                cita,
                obtenerPacienteActivo(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    private void validarPacienteSinCitaActiva(Long idPaciente, Long idCitaActual) {
        List<EstadoCita> estadosActivos = List.of(
                EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);
        boolean tieneOtraCita = idCitaActual == null
                ? citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                        idPaciente, EstadoPaciente.ACTIVO, estadosActivos)
                : citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idPaciente, EstadoPaciente.ACTIVO, estadosActivos, idCitaActual);
        if (tieneOtraCita) {
            throw new IllegalStateException(
                    "El paciente " + idPaciente + " ya tiene una cita activa (PENDIENTE, CONFIRMADA o EN_CURSO)");
        }
    }

    private PacienteResponse obtenerPacienteActivo(Long idPaciente) {
        try {
            return pacientesClient.obtenerPacienteActivoPorId(idPaciente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el paciente activo: " + idPaciente);
        }
    }

    private Cita obtenerActiva(Long id) {
        return citaRepository.findByIdAndEstadoRegistro(id, EstadoPaciente.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro la cita: " + id));
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {
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

    private boolean medicoTieneCitasActivas(Long idMedico) {
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoPaciente.ACTIVO,
                List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO));
    }

    private MedicoResponse obtenerMedicoActivo(Long idMedico) {
        try {
            return medicoClient.obtenerMedicoActivoPorId(idMedico);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el medico activo: " + idMedico);
        }
    }

    private MedicoResponse obtenerMedicoSinEstado(Long idMedico) {
        try {
            return medicoClient.obtenerMedicoSinEstadoPorId(idMedico);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el medico: " + idMedico);
        }
    }
}
