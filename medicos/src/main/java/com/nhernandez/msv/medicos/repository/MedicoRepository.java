package com.nhernandez.msv.medicos.repository;

import com.nhernandez.commons.enums.EstadoPaciente;
import com.nhernandez.msv.medicos.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findByEstadoRegistro(EstadoPaciente estadoRegistro);

    Optional<Medico> findByIdAndEstadoRegistro(Long id, EstadoPaciente estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoPaciente estadoRegistro);

    boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoPaciente estadoRegistro);

    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(String cedula, EstadoPaciente estadoRegistro);

    boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(String email, EstadoPaciente estadoRegistro, Long id);

    boolean existsByTelefonoAndEstadoRegistroAndIdNot(String telefono, EstadoPaciente estadoRegistro, Long id);

    boolean existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(String cedula, EstadoPaciente estadoRegistro, Long id);
}
