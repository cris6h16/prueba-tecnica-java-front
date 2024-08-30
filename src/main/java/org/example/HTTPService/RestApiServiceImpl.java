package org.example.HTTPService;

import okhttp3.*;
import org.example.WebApiInfo.HttpPaths;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;
import java.util.function.Supplier;

public class RestApiServiceImpl implements RestApiService {

    private final OkHttpClient client;
    private final String url;

    public RestApiServiceImpl(String host, String port) {
        this.client = new OkHttpClient().newBuilder().build();
        this.url = host + ":" + port;
    }

    @Override
    public String post(String username, String content) {
        String endpoint = url + HttpPaths.CREAR_POST.getPath();
        String json = String.format("{\"username\": \"%s\", \"content\": \"%s\"}", username, content);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        return executePostRequest(endpoint, body, () -> username + " posted _> \"" + content + "\" @" + fechaYHoraActual());
    }

    @Override
    public String follow(String follower, String followed) {
        String endpoint = url + HttpPaths.FOLLOW.getPath();
        String json = String.format("{\"followerUsername\": \"%s\", \"followedUsername\": \"%s\"}", follower, followed);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Supplier<String> successMessage = () -> "wal" + follower + " empezo a seguir a " + followed + "wal";
        return executePostRequest(endpoint, body, successMessage);
    }

    @Override
    public String dashboard(String username) {
        String endpoint = url + HttpPaths.DASHBOARD.getPath() + "?username=" + username;
        return executeGetRequest(endpoint, this::presentarPosts);
    }

    @Override
    public String wall(String username) {
        String endpoint = url + HttpPaths.WALL.getPath() + "?username=" + username;
        return executeGetRequest(endpoint, this::presentarPosts);
    }

    private String executePostRequest(String url, RequestBody body, Supplier<String> successMessage) {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return successMessage.get();
            } else {
                return extractMessage(response.body());
            }
        } catch (ConnectException e) {
            return "ERROR: no se pudo conectar al servidor: " + url;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR executePostRequest";
        }
    }

    private String executeGetRequest(String url, Function<ResponseBody, String> successHandler) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return successHandler.apply(response.body());
            } else {
                return extractMessage(response.body());
            }
        }catch (ConnectException e) {
            return "ERROR: no se pudo conectar al servidor: " + url;
        }  catch (Exception e) {
            e.printStackTrace();
            return "Error executing GET request";
        }
    }

    private String extractMessage(ResponseBody body) {
        if (body == null) return "Error: Response body is null";
        try {
            String json = body.string();
            String[] parts = json.split("\"message\":\"");
            if (parts.length > 1) {
                return parts[1].split("\"")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error extracting message from response body";
    }
    /* antes
        [
            {
                "id": "4",
                "content": "soy alicia 41",
                "instant": 1724964747925,
                "userUsername": "cris6h16"
            },
            {
                "id": "5",
                "content": "hello world",
                "instant": 1724964747925,
                "userUsername": "cris6h16"
            }
        ]


        despues
        "soy alicia 41" @Alicia @17:30
        "hello world" @cris6h16 @17:30
      */
    private String presentarPosts(ResponseBody body) {
        if (body == null) return "Error: Response body is null";
        try {
            String json = body.string();
            StringBuilder sb = new StringBuilder();
            String[] posts = json.split("\\{");
            for (int i = 1; i < posts.length; i++) {
                String post = posts[i];
                String content = extractJsonValue(post, "\"content\":");
                String user = extractJsonValue(post, "\"userUsername\":");
                long instant = Long.parseLong(extractJsonValue(post, "\"instant\":"));
                sb.append("\"").append(content).append("\" @").append(user).append(" @").append(instantAFechaYHora(instant)).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error presenting posts";
        }
    }

    private String extractJsonValue(String json, String key) {
        int startIndex = json.indexOf(key) + key.length();
        char startChar = json.charAt(startIndex);
        int endIndex;

        if (startChar == '\"') { // string value
            startIndex++;
            endIndex = json.indexOf('\"', startIndex);
        } else { // number value
            endIndex = json.indexOf(',', startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf('}', startIndex);
            }
        }

        return json.substring(startIndex, endIndex).trim();
    }

    private String instantAFechaYHora(long instant) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(instant / 1000, 0, ZoneOffset.UTC);
        return dateTime.getHour() + ":" + String.format("%02d", dateTime.getMinute());
    }

    private String fechaYHoraActual() {
        LocalDateTime now = LocalDateTime.now();
        return now.getHour() + ":" + String.format("%02d", now.getMinute());
    }
}
