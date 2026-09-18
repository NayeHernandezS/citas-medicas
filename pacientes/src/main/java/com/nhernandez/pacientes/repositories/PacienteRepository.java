package com.nhernandez.pacientes.repositories;

import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.pacientes.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    
    List<Paciente> findByEstadoNot(EstadoRegistro estado);

    Optional<Paciente> findByIdAndEstadoNot(Long id, EstadoRegistro estado);

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

}
