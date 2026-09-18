package com.nhernandez.pacientes.service;

import com.nhernandez.commons.client.CitasClient;
import com.nhernandez.commons.dto.pacientes.PacienteRequest;
import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.pacientes.dto.PacienteResponse;
import com.nhernandez.pacientes.entities.Paciente;
import com.nhernandez.pacientes.mappers.PacienteMapper;
import com.nhernandez.pacientes.repositories.PacienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final CitasClient citasClient;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Listado de pacientes activos solicitado");
        return pacienteRepository.findByEstadoNot(EstadoRegistro.ELIMINADO).stream()
                .map(pacienteMapper::entidadResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listarSinValidarEstado() {
        log.info("Listado de pacientes sin validar estado solicitado");
        return pacienteRepository.findAll().stream()
                .map(pacienteMapper::entidadResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadResponse(obtenerActivo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerSinValidarEstado(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontro el paciente: " + id));
        return pacienteMapper.entidadResponse(paciente);
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando paciente con telefono {}", request.telefono());
        validarDatosUnicos(request, null);
        Paciente paciente = pacienteMapper.requestToEntity(request);
        paciente = pacienteRepository.save(paciente);
        return pacienteMapper.entidadResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {
        Paciente paciente = obtenerActivo(id);
        validarSinCitasConfirmadaOEnCurso(id);
        validarDatosUnicos(request, id);
        pacienteMapper.actualizarEntidad(paciente, request);
        return pacienteMapper.entidadResponse(pacienteRepository.save(paciente));
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerActivo(id);
        validarSinCitasConfirmadaOEnCurso(id);
        paciente.eliminar();
        pacienteRepository.save(paciente);
    }

    private Paciente obtenerActivo(Long id) {
        return pacienteRepository.findByIdAndEstadoNot(id, EstadoRegistro.ELIMINADO)
                .orElseThrow(() -> new NoSuchElementException("No se encontro el paciente: " + id));
    }

    private void validarSinCitasConfirmadaOEnCurso(Long idPaciente) {
        if (Boolean.TRUE.equals(citasClient.pacienteTieneCitasConfirmadaOEnCurso(idPaciente))) {
            throw new IllegalStateException(
                    "No se puede actualizar o eliminar lógicamente un paciente si tiene citas CONFIRMADA o EN_CURSO");
        }
    }

    private void validarDatosUnicos(PacienteRequest request, Long idActual) {
        boolean telefonoDuplicado = idActual == null
                ? pacienteRepository.existsByTelefono(request.telefono())
                : pacienteRepository.existsByTelefonoAndIdNot(request.telefono(), idActual);
        if (telefonoDuplicado) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con el telefono " + request.telefono());
        }

        boolean emailDuplicado = idActual == null
                ? pacienteRepository.existsByEmailIgnoreCase(request.email())
                : pacienteRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), idActual);
        if (emailDuplicado) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con el correo " + request.email());
        }
    }
}
