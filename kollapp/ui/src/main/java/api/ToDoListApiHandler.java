package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.ToDoList;
import core.User;
import core.UserGroup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ToDoListApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "http://localhost:8080/api/v1/todolists";

    public ToDoListApiHandler() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional
    }

    /**
     * Loads the to-do list for a specific user.
     *
     * @param user the user whose to-do list is to be loaded
     * @return the ToDoList object if successful, null otherwise
     */
    public ToDoList loadToDoList(User user) {
        String url = baseUrl + "/" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Request URL: " + url);
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body()); // Add this for better debugging
        
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ToDoList.class);
            } else {
                System.err.println("Failed to load to-do list. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while loading the to-do list: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Assigns a new to-do list to a specific user.
     *
     * @param user the user to whom the ToDoList will be assigned
     * @return true if successful, false otherwise
     */
    public boolean assignToDoList(User user) {
        String url = baseUrl + "/" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            return statusCode == 201 || statusCode == 200;
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while assigning to-do list: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the to-do list for a specific user.
     *
     * @param user The user whose to-do list is being updated.
     * @param toDoList the updated ToDoList object
     * @return true if successful, false otherwise
     */
    public boolean updateToDoList(User user, ToDoList toDoList) {
        if (user == null) {
            System.err.println("User is null.");
            return false;
        }
        
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            System.err.println("Username is null or empty.");
            return false;
        }

        String url;
        try {
            url = baseUrl + "/" + URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.err.println("Encoding not supported: " + e.getMessage());
            return false;
        }

        try {
            String jsonBody = objectMapper.writeValueAsString(toDoList);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while updating to-do list: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads the to-do list for a specific user group.
     *
     * @param userGroup the user group whose to-do list is to be loaded
     * @return the ToDoList object if successful, null otherwise
     */
    public ToDoList loadGroupToDoList(UserGroup userGroup) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(userGroup.getGroupName(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ToDoList.class);
            } else {
                System.err.println("Failed to load group to-do list. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while loading group to-do list: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates the to-do list for a specific user group.
     *
     * @param userGroup the user group whose to-do list is to be updated
     * @param toDoList  the updated ToDoList object
     * @return true if successful, false otherwise
     */
    public boolean updateGroupToDoList(UserGroup userGroup, ToDoList toDoList) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(userGroup.getGroupName(), StandardCharsets.UTF_8);
        try {
            String jsonBody = objectMapper.writeValueAsString(toDoList);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while updating group to-do list: " + e.getMessage());
            return false;
        }
    }
}
