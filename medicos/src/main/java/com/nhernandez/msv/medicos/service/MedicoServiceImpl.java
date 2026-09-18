package com.nhernandez.msv.medicos.service;

import com.nhernandez.commons.client.CitasClient;
import com.nhernandez.commons.dto.medicos.MedicoRequest;
import com.nhernandez.commons.dto.medicos.MedicoResponse;
import com.nhernandez.commons.enums.DisponibilidadMedico;
import com.nhernandez.commons.enums.EspecialidadMedico;
import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.commons.exceptions.RecursoNoEncontradoException;
import com.nhernandez.msv.medicos.entity.Medico;
import com.nhernandez.msv.medicos.mapper.MedicoMapper;
import com.nhernandez.msv.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final CitasClient citasClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listado de medicos activos solicitado");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadResponse(obtenerActivo(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el medico: " + id));
        return medicoMapper.entidadResponse(medico);
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando medico con cedula {}", request.cedulaProfesional());
        validarDatosUnicos(request, null);
        Medico medico = medicoMapper.requestEntidad(request);
        return medicoMapper.entidadResponse(medicoRepository.save(medico));
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {
        log.info("Actualizando medico {}", id);
        Medico medico = obtenerActivo(id);
        validarSinCitasConfirmadaOEnCurso(id);
        validarDatosUnicos(request, id);
        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );
        return medicoMapper.entidadResponse(medicoRepository.save(medico));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando médico {}", id);
        Medico medico = obtenerActivo(id);
        validarSinCitasConfirmadaOEnCurso(id);
        medico.eliminar();
        medicoRepository.save(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        log.info("Actualizando disponibilidad del médico {} a {}", idMedico, idDisponibilidad);
        Medico medico = obtenerActivo(idMedico);
        DisponibilidadMedico disponibilidad = DisponibilidadMedico.findByCodigo(idDisponibilidad);
        if (disponibilidad == DisponibilidadMedico.DISPONIBLE
                && Boolean.TRUE.equals(citasClient.medicoTieneCitasActivas(idMedico))) {
            throw new IllegalStateException(
                    "No se puede pasar al médico a DISPONIBLE si tiene citas activas (PENDIENTE, CONFIRMADA o EN_CURSO)");
        }
        medico.actualizarDisponibilidad(disponibilidad);
        medicoRepository.save(medico);
    }

    private void validarSinCitasConfirmadaOEnCurso(Long idMedico) {
        // No actualizar ni eliminar lógicamente si tiene citas CONFIRMADA o EN_CURSO
        if (Boolean.TRUE.equals(citasClient.medicoTieneCitasConfirmadaOEnCurso(idMedico))) {
            throw new IllegalStateException(
                    "No se puede actualizar o eliminar lógicamente un médico si tiene citas CONFIRMADA o EN_CURSO");
        }
    }

    private Medico obtenerActivo(Long id) {
        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el médico: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request, Long idActual) {
        boolean emailDuplicado = idActual == null
                ? medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(request.email(), EstadoRegistro.ACTIVO)
                : medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(request.email(), EstadoRegistro.ACTIVO, idActual);
        if (emailDuplicado) {
            throw new IllegalArgumentException("Ya existe un médico registrado con el email " + request.email());
        }

        boolean telefonoDuplicado = idActual == null
                ? medicoRepository.existsByTelefonoAndEstadoRegistro(request.telefono(), EstadoRegistro.ACTIVO)
                : medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(request.telefono(), EstadoRegistro.ACTIVO, idActual);
        if (telefonoDuplicado) {
            throw new IllegalArgumentException("Ya existe un médico registrado con el teléfono " + request.telefono());
        }

        boolean cedulaDuplicada = idActual == null
                ? medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(request.cedulaProfesional(), EstadoRegistro.ACTIVO)
                : medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(request.cedulaProfesional(), EstadoRegistro.ACTIVO, idActual);
        if (cedulaDuplicada) {
            throw new IllegalArgumentException("Ya existe un médico registrado con la cédula " + request.cedulaProfesional());
        }
    }
}
