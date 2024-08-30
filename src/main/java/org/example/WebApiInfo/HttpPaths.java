package org.example.WebApiInfo;

public enum HttpPaths {
    CREAR_POST("/api/v1/posts/create"),
    FOLLOW("/api/v1/users/follow"),
    DASHBOARD("/api/v1/users/dashboard"),
    WALL("/api/v1/users/wall");

    private final String path;

    HttpPaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
