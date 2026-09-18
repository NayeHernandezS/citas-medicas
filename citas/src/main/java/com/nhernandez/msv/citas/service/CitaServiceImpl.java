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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
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
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(this::aCitaResponseSinEstado)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        return aCitaResponseSinEstado(obtenerActiva(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorIdSinEstado(Long id) {
        log.info("Obteniendo de citas sin estado activas solicitado");
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cita: " + id));
        return aCitaResponseSinEstado(cita);
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando cita para paciente");
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitaActiva(request.idPaciente(), null);
        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        validarMedicoDisponible(medico);
        Cita cita = citaMapper.requestEntidad(request);
        citaRepository.save(cita);

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());
        return citaMapper.entidadResponse(
                cita,
                paciente,
                medico
        );

    }

    private void validarMedicoDisponible(MedicoResponse medico) {
        if (!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad())) {
            throw new IllegalStateException("El médico " + medico.id() + " no está disponible para agendar citas");
        }
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        log.info("Actualizando cita {}", id);
        Cita cita = obtenerActiva(id);
        Long medicoAnterior = cita.getIdMedico();
        boolean cambiaMedico = !medicoAnterior.equals(request.idMedico());

        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitaActiva(request.idPaciente(), id);

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        if (cambiaMedico) {
            validarMedicoDisponible(medico);
        }

        cita.actualizar(request.idPaciente(), request.idMedico(), request.fechaCita(), request.sintomas());
        citaRepository.save(cita);

        if (cambiaMedico) {
            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());
            liberarMedicoSiQuedaSinCitasActivas(medicoAnterior);
        }
        return citaMapper.entidadResponse(cita, paciente, medico);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando cita {}", id);
        Cita cita = obtenerActiva(id);
        Long idMedico = cita.getIdMedico();
        cita.eliminar();
        citaRepository.save(cita);
        //no liberar al medico
        if(cita.getEstadoCita() == EstadoCita.PENDIENTE) {
            cambiarDisponibilidadMedicoSegunEstadoCita(idMedico, EstadoCita.CANCELADA);
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

    /** Solo para lecturas: paciente y médico sin filtrar estado de registro. */
    private CitaResponse aCitaResponseSinEstado(Cita cita) {
        return citaMapper.entidadResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    private void validarPacienteSinCitaActiva(Long idPaciente, Long idCitaActual) {
        boolean tieneOtraCita = idCitaActual == null
                ? citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                        idPaciente, EstadoRegistro.ACTIVO, Cita.ESTADOS_ACTIVOS)
                : citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
                        idPaciente, EstadoRegistro.ACTIVO, Cita.ESTADOS_ACTIVOS, idCitaActual);
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

    private PacienteResponse obtenerPacienteSinEstado(Long idPaciente) {
        try {
            return pacientesClient.obtenerPacienteSinEstadoPorId(idPaciente);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontro el paciente: " + idPaciente);
        }
    }

    private Cita obtenerActiva(Long id) {
        return citaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro la cita: " + id));
    }

    /** Ocupa o libera al médico según EstadoCita.codigoDisponibilidadMedico(). */
    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {
        Long codigo = estadoCita.codigoDisponibilidadMedico();
        Runnable aplicar = () -> medicoClient.actualizarDisponibilidadMedico(idMedico, codigo);
        if (DisponibilidadMedico.DISPONIBLE.getCodigo().equals(codigo)
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    aplicar.run();
                }
            });
            return;
        }
        aplicar.run();
    }

    /** Libera al médico anterior a DISPONIBLE; no usa el mapeo de estado de cita. */
    private void liberarMedicoSiQuedaSinCitasActivas(Long idMedico) {
        Runnable liberar = () -> {
            if (!medicoTieneCitasActivas(idMedico)) {
                medicoClient.actualizarDisponibilidadMedico(
                        idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    liberar.run();
                }
            });
            return;
        }
        liberar.run();
    }

    @Override
    public boolean medicoTieneCitasConfirmadaOEnCurso(Long idMedico) {
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                Cita.ESTADOS_CONFIRMADA_EN_CURSO);
    }

    @Override
    public boolean pacienteTieneCitasConfirmadaOEnCurso(Long idPaciente) {
        return citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente,
                EstadoRegistro.ACTIVO,
                Cita.ESTADOS_CONFIRMADA_EN_CURSO);
    }

    @Override
    public boolean medicoTieneCitasActivas(Long idMedico) {
        return citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico,
                EstadoRegistro.ACTIVO,
                Cita.ESTADOS_ACTIVOS);
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
