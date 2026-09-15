package com.nhernandez.pacientes.repositories;

import com.nhernandez.pacientes.enums.EstadoPaciente;
import com.nhernandez.pacientes.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    //LA FUNCION DEL REPOSITORY ES COMUNICARSE CON LA BASE DE DATOS.
    //SABE COMO HACER EL SELECT* FROM  paciente WHERE estado !=.
    //PERO NO SABE NADA DE LOGICA DE NEGOCIO NI DE LO QUE EL USURIO FINAL NECESITA VER

    List<Paciente> findByEstadoNot(EstadoPaciente estado);

    Optional<Paciente> findByIdAndEstadoNot(Long id, EstadoPaciente estado);

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);
}
