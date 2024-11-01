package api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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

    public User loadUser(String username, String password) {
        String url = "http://localhost:8080/api/v1/users/login";
        String formData = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                  "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(responseBody, User.class);
                return user;
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean confirmNewValidUser(String username, String password, String confirmPassword) {
        String url = "http://localhost:8080/api/v1/users/validate";
        String formData = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                      "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8) +
                      "&confirmPassword=" + URLEncoder.encode(confirmPassword, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                Boolean bool = Boolean.valueOf(responseBody);
                return bool; 
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveUser(User user) {
        String url = "http://localhost:8080/api/v1/users/";
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonInputString;
        try {
            jsonInputString = objectMapper.writeValueAsString(user);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                System.out.println(responseBody);
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserValidationErrorMessage(String username, String password, String confirmPassword) {
        String url = "http://localhost:8080/api/v1/users/validate/message"; 
        String formData = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8) +
                      "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8) +
                      "&confirmPassword=" + URLEncoder.encode(confirmPassword, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                String message = String.valueOf(responseBody);
                return message; 
            } else {
                return "Error: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while validating the user.";
        }
    }

    public boolean removeUser(String username) {
        String url = "http://localhost:8080/api/v1/users/" + URLEncoder.encode(username, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return true;
            } else if (response.statusCode() == 400) {
                System.err.println("Bad Request: The server could not understand the request due to invalid syntax.");
                return false;
            } else if (response.statusCode() == 500) {
                System.err.println("Internal Server Error: The server encountered an unexpected condition.");
                return false;
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
