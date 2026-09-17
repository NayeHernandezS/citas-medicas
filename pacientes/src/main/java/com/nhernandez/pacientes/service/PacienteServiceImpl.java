package com.nhernandez.pacientes.service;

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
    public PacienteResponse obtener(Long id) {
        Paciente paciente = pacienteRepository.findByIdAndEstadoNot(id, EstadoRegistro.ELIMINADO)
                .orElseThrow(() -> new NoSuchElementException("No se encontro el paciente: " + id));
        return pacienteMapper.entidadResponse(paciente);
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
        validarTelefonoUnico(request.telefono(), null);
        Paciente paciente = pacienteMapper.requestToEntity(request);
        paciente = pacienteRepository.save(paciente);
        return pacienteMapper.entidadResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        Paciente paciente = obtenerActivo(id);
        validarTelefonoUnico(request.telefono(), id);
        pacienteMapper.actualizarEntidad(paciente, request);
        return pacienteMapper.entidadResponse(pacienteRepository.save(paciente));
    }

    @Override
    public PacienteResponse eliminar(Long id) {
        Paciente paciente = obtenerActivo(id);
        paciente.setEstado(EstadoRegistro.ELIMINADO);
        return pacienteMapper.entidadResponse(pacienteRepository.save(paciente));
    }

    private Paciente obtenerActivo(Long id) {
        return pacienteRepository.findByIdAndEstadoNot(id, EstadoRegistro.ELIMINADO)
                .orElseThrow(() -> new NoSuchElementException("No se encontro el paciente: " + id));
    }

    private void validarTelefonoUnico(String telefono, Long idActual) {
        boolean telefonoDuplicado = idActual == null
                ? pacienteRepository.existsByTelefono(telefono)
                : pacienteRepository.existsByTelefonoAndIdNot(telefono, idActual);
        if (telefonoDuplicado) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con el telefono " + telefono);
        }
    }
}
