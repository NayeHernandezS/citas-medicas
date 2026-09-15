package com.nhernandez.pacientes.util;

import java.util.stream.Collectors;

public final class PacienteReglas {

    private PacienteReglas() {
    }

    public static double calcularImc(double peso, double estatura) {
        if (estatura <= 0) {
            throw new IllegalArgumentException("La estatura debe ser mayor a 0 para calcular el IMC");
        }
        double imc = Math.round((peso / (estatura * estatura)) * 100.0) / 100.0;
        if (imc < 10.0 || imc > 50.0) {
            throw new IllegalArgumentException("El IMC calculado (" + imc + ") debe estar entre 10.0 y 50.0");
        }
        return imc;
    }

    public static String generarNumeroExpediente(String telefono) {
        if (telefono == null || !telefono.matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 10 dígitos numéricos");
        }
        return telefono.chars()//lo comprende en ASCII para convertirlo a Stream
                //se  convierte a un objeto
                .mapToObj(c -> String.valueOf((char) c))//lo convierte a String
                .collect(Collectors.joining("X"));
    }                    //une todas las letras con una X en medio
}
