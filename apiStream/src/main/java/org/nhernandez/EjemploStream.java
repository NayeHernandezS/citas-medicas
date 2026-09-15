package org.nhernandez;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class EjemploStream {
    public static void main(String[] args) {

        Stream<String> nombre = Stream.of("Pato", "Paco", "Pepa", "Pepa", "Pepe");
        nombre.forEach(System.out::println);

        String[] arr = {"Pato", "Paco", "Pepa", "Pepa", "Pepe"};
        //Stream<String> nombre = Arrays.stream(arr);
        nombre.forEach(System.out::println);
        Stream<String> nombres = Stream.<String>builder()
                .add("Pato")
                .add("Paco")
                .add("Pepa")
                .add("Pepe")
                .build();
        nombres.forEach(System.out::println);

        List<String> lista = new ArrayList<>();
        lista.add("Pato");
        lista.add ("Paco");
        lista.add("Pepa");
        lista.add("Pepa");
        lista.add("Pepe");
    }
}