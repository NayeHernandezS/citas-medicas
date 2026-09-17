package com.nhernandez.msv.citas.repository;

import com.nhernandez.commons.enums.EstadoPaciente;
import com.nhernandez.msv.citas.entity.Cita;
import com.nhernandez.commons.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByEstadoRegistro(EstadoPaciente estadoRegistro);

    Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoPaciente estadoRegistro);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente, EstadoPaciente estadoRegistro, Collection<EstadoCita> estadosCita);

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idPaciente, EstadoPaciente estadoRegistro, Collection<EstadoCita> estadosCita, Long id);

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico, EstadoPaciente estadoRegistro, Collection<EstadoCita> estadosCita);
}
