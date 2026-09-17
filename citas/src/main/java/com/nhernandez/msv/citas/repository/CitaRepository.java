package com.nhernandez.msv.citas.repository;

import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.msv.citas.entity.Cita;
import com.nhernandez.commons.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    List<Cita> findByEstadoRegistroAndEstadoCitaNot(EstadoRegistro estadoRegistro, EstadoCita estadoCita);

    Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente, EstadoRegistro estadoRegistro, Collection<EstadoCita> estadosCita);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idPaciente, EstadoRegistro estadoRegistro, Collection<EstadoCita> estadosCita, Long id);

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico, EstadoRegistro estadoRegistro, Collection<EstadoCita> estadosCita);
}
