package org.example;

import org.example.HTTPService.RestApiService;

import java.util.Scanner;

public class Consola {

    private RestApiService apiService;

    public Consola(RestApiService apiService) {
        this.apiService = apiService;
    }

    public void run() {
        System.out.println("Essribe 'exit' para salir... Ya puedes empezar a escribir comandos");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine().trim();
            try {
                if (command.startsWith("post")) post(command);
                else if (command.startsWith("follow")) follow(command);
                else if (command.startsWith("dashboard")) dashboard(command);
                else if (command.startsWith("wall")) wall(command);
                else if (command.equalsIgnoreCase("exit")) break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void wall(String command) {
        // wall @username
        command = command.trim();
        String username = command.split(" ")[1].replace("@", "");

        String str = this.apiService.wall(username);
        System.out.println(str);
    }

    private void dashboard(String command) {
        // dashboard @username
        command = command.trim();
        String username = command.split(" ")[1].replace("@", "");

        String str = this.apiService.dashboard(username);
        System.out.println(str);
    }

    private void follow(String command) {
        // follow @username @username
        command = command.trim();
        String[] parts = command.split(" ");
        String follower = parts[1].replace("@", "");
        String followed = parts[2].replace("@", "");

        String str = this.apiService.follow(follower, followed);
        System.out.println(str);
    }

    private void post(String command) {
        // post @username hola esto es un post
        command = command.trim();
        String[] parts = command.split(" ", 3);
        String username = parts[1].replace("@", "");
        String content = parts[2];

        String str = this.apiService.post(username, content);
        System.out.println(str);
    }
}
