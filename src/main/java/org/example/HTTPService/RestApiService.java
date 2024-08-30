package org.example.HTTPService;

public interface RestApiService {
    String post(String username, String content); // post = publicar
    String follow(String follower, String followed);
    String dashboard(String username);
    String wall(String username);
}
