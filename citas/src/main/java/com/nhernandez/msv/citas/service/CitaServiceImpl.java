package com.nhernandez.msv.citas.service;

import com.nhernandez.commons.client.MedicoClient;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.dto.pacientes.PacienteResponse;
import com.nhernandez.commons.enums.DisponibilidadMedico;
import com.nhernandez.commons.enums.EstadoPaciente;
import com.nhernandez.commons.exceptions.RecursoNoEncontradoException;
import com.nhernandez.commons.client.PacientesClient;
import com.nhernandez.msv.citas.dto.CitaRequest;
import com.nhernandez.msv.citas.dto.CitaResponse;
import com.nhernandez.msv.citas.entity.Cita;
import com.nhernandez.msv.citas.enums.EstadoCita;
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
        obtenerPacienteActivo(request.idPaciente());
        validarPacienteSinCitaActiva(request.idPaciente(), id);
        validarMedicoActivoDisponible(request.idMedico());
        cita.actualizar(request.idPaciente(), request.idMedico(), request.fechaCita(), request.sintomas());
        return aCitaResponse(citaRepository.save(cita));
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
            throw new RecursoNoEncontradoException("No se encontró el paciente activo: " + idPaciente);
        }
    }

    private Cita obtenerActiva(Long id) {
        return citaRepository.findByIdAndEstadoRegistro(id, EstadoPaciente.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cita: " + id));
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {
        medicoClient.actualizarDisponibilidadMedico(
                idMedico,
                estadoCita.codigoDisponibilidadMedico());
    }

    private MedicoResponse obtenerMedicoActivo(Long idMedico) {
        try {
            return medicoClient.obtenerMedicoActivoPorId(idMedico);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontró el médico activo: " + idMedico);
        }
    }

    private MedicoResponse obtenerMedicoSinEstado(Long idMedico) {
        try {
            return medicoClient.obtenerMedicoSinEstadoPorId(idMedico);
        } catch (FeignException.NotFound e) {
            throw new RecursoNoEncontradoException("No se encontró el médico: " + idMedico);
        }
    }
}
