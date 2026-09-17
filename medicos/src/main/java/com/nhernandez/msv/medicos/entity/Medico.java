package com.nhernandez.msv.medicos.entity;

import com.nhernandez.commons.enums.DisponibilidadMedico;
import com.nhernandez.commons.enums.EspecialidadMedico;
import com.nhernandez.commons.enums.EstadoRegistro;
import com.nhernandez.commons.utils.StringCustomUtils;
import com.nhernandez.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEDICOS")
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MEDICO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 12)
    private String telefono;

    @Column(name = "CEDULA_PROFESIONAL", nullable = false)
    private String cedulaProfesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIALIDAD", nullable = false)
    private EspecialidadMedico especialidadMedico;

    @Enumerated(EnumType.STRING)
    @Column(name = "DISPONIBILIDAD", nullable = false)
    private DisponibilidadMedico disponibilidadMedico;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private void validadDatos(String nombre, String apellidoPaterno, String apellidoMaterno,
                              Short edad, String email, String telefone, String cedulaProfesional,
                              EspecialidadMedico especialidadMedico) {

        StringCustomUtils.validarTamanio(nombre,"El nombre es requerido y debe tener entre 1 y 50 caracteres", 1, 50);
        StringCustomUtils.validarTamanio(apellidoPaterno, "El apellido paterno es requerido", 1, 50);
        StringCustomUtils.validarTamanio(apellidoMaterno, "El apellido materno es requerido", 1, 50);
        StringCustomUtils.validarTamanio(email, "El email es requerido y debe tener entre 1 y 100 caracteres", 1, 100);
        StringCustomUtils.validarTamanio(telefone, "El telefono es requerido y debe tener 10 dígitos", 10, 10);
        StringCustomUtils.validarTamanio(cedulaProfesional, "La cedula profesional es requerida y debe tener 12 dígitos", 12, 12);
        ValoresNumericosUtils.validarRangoShort(edad, (short) 18, (short) 100, "La edad es requerida y debe tener entre 18 y 100 anios");

    }

    private void validarNoEliminado(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw  new IllegalArgumentException("El estado no puede ser eliminado");
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarEspecicialidad(EspecialidadMedico especialidad){
        validarNoEliminado();

        if (especialidad == null)
            throw  new IllegalArgumentException("El especialidad no puede ser null");

        this.especialidadMedico = especialidad;
    }

    public void actualizarDisponibilidad(DisponibilidadMedico disponibilidad){
        validarNoEliminado();
        if (disponibilidad == null) {
            throw new IllegalArgumentException("La disponibilidad no puede ser null");
        }
        this.disponibilidadMedico = disponibilidad;
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno,
                           Short edad, String email, String telefono, String cedulaProfesional,
                           EspecialidadMedico especialidad) {
        validarNoEliminado();

        validadDatos(
                nombre, apellidoPaterno, apellidoMaterno, edad, email, telefono, cedulaProfesional, especialidad
        );

        actualizarEspecicialidad(especialidad);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.email = email;
        this.telefono = telefono.trim();
        this.cedulaProfesional = cedulaProfesional.trim();
    }
}
