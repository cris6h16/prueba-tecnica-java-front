package org.example;

import org.example.HTTPService.RestApiServiceImpl;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Ejecucion: java -jar <host> <port>");
            System.out.println("- Ejemplo: java -jar http://localhost 8080");
            System.exit(1);
        }
        Consola consola = new Consola(new RestApiServiceImpl(args[0], args[1]));
        consola.run();
    }
}
