package api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.User;

public class UserApiHandler {
    private final HttpClient httpClient;

    public UserApiHandler() {
        this.httpClient = HttpClient.newHttpClient();
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
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while retrieving user data: " + e.getMessage());
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
                return objectMapper.readValue(responseBody, User.class);
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while retrieving user data: " + e.getMessage());
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
                return Boolean.parseBoolean(responseBody);
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while validating user data: " + e.getMessage());
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
            System.out.println("An error occurred while retrieving user data: " + e.getMessage());
            return;
        }
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
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
            System.out.println("An error occurred while saving user data: " + e.getMessage());
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
                return String.valueOf(responseBody);
            } else {
                return "Error: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            System.out.println("An error occurred while validating user: " + e.getMessage());
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
            System.out.println("An error occurred while trying to remove user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean assignGroupToUser(String username, String groupName) {
        String url = "http://localhost:8080/api/v1/users/" + username + "/assignGroup";
        
        // Encode the form data
        String formData = "groupName=" + URLEncoder.encode(groupName, StandardCharsets.UTF_8);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
    
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
            // Log response code for debugging
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
    
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred assigning group to user: " + e.getMessage());
            return false;
        }
    }
}
