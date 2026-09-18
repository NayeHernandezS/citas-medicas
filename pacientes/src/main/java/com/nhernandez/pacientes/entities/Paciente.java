package com.nhernandez.pacientes.entities;

import com.nhernandez.commons.enums.EstadoRegistro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Column(name = "EDAD", nullable = false)
    private Integer edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "TELEFONO", nullable = false, length = 10, unique = true)
    private String telefono;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numeroExpediente;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false, length = 20)
    private EstadoRegistro estado;

    public static Paciente crear(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String direccion,
            Integer edad,
            Double peso,
            Double estatura,
            String telefono,
            String email
    ) {
        Paciente paciente = new Paciente();
        paciente.aplicarDatos(
                nombre, apellidoPaterno, apellidoMaterno, direccion, edad, peso, estatura, telefono, email);
        paciente.estado = EstadoRegistro.ACTIVO;
        return paciente;
    }

    public void actualizar(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String direccion,
            Integer edad,
            Double peso,
            Double estatura,
            String telefono,
            String email
    ) {
        validarNoEliminado();
        aplicarDatos(
                nombre, apellidoPaterno, apellidoMaterno, direccion, edad, peso, estatura, telefono, email);
    }

    public void eliminar() {
        validarNoEliminado();
        this.estado = EstadoRegistro.ELIMINADO;
    }

    private void validarNoEliminado() {
        if (this.estado == EstadoRegistro.ELIMINADO) {
            throw new IllegalStateException("El paciente ya esta eliminado");
        }
    }

    private void aplicarDatos(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String direccion,
            Integer edad,
            Double peso,
            Double estatura,
            String telefono,
            String email
    ) {
        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.direccion = direccion.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.telefono = telefono.trim();
        this.email = email.trim();
        this.imc = calcularImc(peso, estatura);
        this.numeroExpediente = generarNumeroExpediente(this.telefono);
    }

    private static double calcularImc(double peso, double estatura) {
        if (estatura <= 0) {
            throw new IllegalArgumentException("La estatura debe ser mayor a 0 para calcular el IMC");
        }
        double imc = Math.round((peso / (estatura * estatura)) * 100.0) / 100.0;
        if (imc < 10.0 || imc > 50.0) {
            throw new IllegalArgumentException("El IMC calculado (" + imc + ") debe estar entre 10.0 y 50.0");
        }
        return imc;
    }

    private static String generarNumeroExpediente(String telefono) {
        if (telefono == null || !telefono.matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos numéricos");
        }
        return telefono.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining("X"));
    }
}
