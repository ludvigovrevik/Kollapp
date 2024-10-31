package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.User;

public class UserApiHandler {
    private final HttpClient httpClient;

    public UserApiHandler() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public Optional<User> getUser(String username) {
        String url = "http://localhost:8080/api/v1/users/" + username;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(responseBody, User.class);
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Checks if a user exists by making an HTTP GET request to the API.
     *
     * @param username The username to check.
     * @return true if the user exists; false otherwise.
     */
    public boolean userExists(String username) {
        String url = "http://localhost:8080/api/v1/users/exists/" + username;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            if (response.statusCode() == 200) {
                return Boolean.parseBoolean(response.body().trim());
            } else {
                // Handle error responses as needed
                return false;
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
