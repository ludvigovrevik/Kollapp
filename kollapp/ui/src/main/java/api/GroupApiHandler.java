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

import core.UserGroup;

public class GroupApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GroupApiHandler() {
        this.httpClient = createHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    protected HttpClient createHttpClient() {
        return HttpClient.newHttpClient();
    }

    /**
     * Retrieves a UserGroup from the API.
     *
     * @param groupName The name of the group to retrieve.
     * @return An Optional containing the UserGroup if found; otherwise, Optional.empty().
     */
    public Optional<UserGroup> getGroup(String groupName) {
        String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
        String url = "http://localhost:8080/api/v1/groups/" + encodedGroupName;
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
            System.out.println("An error occurred while retrieving group: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Creates a UserGroup by making an HTTP POST request to the API.
     *
     * @param username  The username of the user who creates the group.
     * @param groupName The name of the group to be created.
     * @return true if the operation was successful; false otherwise.
     */
    public boolean createGroup(String username, String groupName) {
        groupName = sanitizeInput(groupName);
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
        
        String url = String.format("http://localhost:8080/api/v1/groups/%s/%s", 
                                    encodedUsername, encodedGroupName);
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json") 
                .build();
    
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                System.out.println("Group created successfully.");
                return true;
            } else {
                System.out.println("Failed to create group. Status Code: " + response.statusCode());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while creating group: " + e.getMessage());
            return false;
        }
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
        String formData = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);
    
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
            System.out.println("An error occurred while assigning user to group: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a group exists by making an HTTP GET request to the API.
     *
     * @param groupName The name of the group to check.
     * @return true if the group exists; false otherwise.
     */
    public boolean groupExists(String groupName) {
        String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
        String url = "http://localhost:8080/api/v1/groups/exists/" + encodedGroupName;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && Boolean.parseBoolean(response.body());
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while checking if group exists: " + e.getMessage());
            return false;
        }
    }

    public String validateGroupAssignment(String username, String groupName) {
        String url = "http://localhost:8080/api/v1/groups/validate-assignment?username=" 
                        + URLEncoder.encode(username, StandardCharsets.UTF_8)
                        + "&groupName=" + URLEncoder.encode(groupName, StandardCharsets.UTF_8);
    
        System.out.println("Validating group assignment - URL: " + url);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Validation response code: " + response.statusCode());
            System.out.println("Validation response body: " + response.body());
            
            if (response.statusCode() == 200) {
                return null;
            } else {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            String error = "An error occurred while validating group assignment: " + e.getMessage();
            System.out.println(error);
            return error;
        }
    }

    /**
     * Sanitizes the input string by trimming leading and trailing whitespace.
     *
     * @param input the input string to be sanitized
     * @return the sanitized string with leading and trailing whitespace removed,
     *         or the original input if it is null
     */
    private String sanitizeInput(String input) {
        return input != null ? input.trim() : input;
    }
}
