package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.UserGroup;

public class GroupApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GroupApiHandler() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Assigns a user to a group by making an HTTP POST request to the API.
     *
     * @param username  The username of the user to assign.
     * @param groupName The name of the group.
     * @return true if the operation was successful; false otherwise.
     */
    public boolean assignUserToGroup(String username, String groupName) {
        String url = "http://localhost:8080/api/v1/groups/" + groupName + "/assignUser";

        // Create the request body
        AssignUserRequest requestBody = new AssignUserRequest(username);

        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response status code indicates success
            return response.statusCode() == 200 || response.statusCode() == 201;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a UserGroup from the API.
     *
     * @param groupName The name of the group to retrieve.
     * @return An Optional containing the UserGroup if found; otherwise, Optional.empty().
     */
    public Optional<UserGroup> getGroup(String groupName) {
        String url = "http://localhost:8080/api/v1/groups/" + groupName;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                UserGroup group = objectMapper.readValue(responseBody, UserGroup.class);
                return Optional.of(group);
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Inner class for the request body
    public static class AssignUserRequest {
        private String username;

        public AssignUserRequest(String username) {
            this.username = username;
        }

        // Getter and setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
