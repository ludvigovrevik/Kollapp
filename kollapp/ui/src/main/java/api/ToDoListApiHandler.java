package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.ToDoList;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ToDoListApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "http://localhost:8080/api/v1/todolists";

    public ToDoListApiHandler() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Loads the to-do list for a specific user.
     *
     * @param username the username of the user
     * @return the ToDoList object if successful, null otherwise
     */
    public ToDoList loadToDoList(String username) {
        String url = baseUrl + "/" + URLEncoder.encode(username, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ToDoList.class);
            } else {
                System.err.println("Failed to load to-do list. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while loading the to-do list: " + e.getMessage());
            return null;
        }
    }

    /**
     * Assigns a new to-do list to a specific user.
     *
     * @param username the username of the user
     * @return true if successful, false otherwise
     */
    public boolean assignToDoList(String username) {
        String url = baseUrl + "/" + URLEncoder.encode(username, StandardCharsets.UTF_8);
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
     * @param username the username of the user
     * @param toDoList the updated ToDoList object
     * @return true if successful, false otherwise
     */
    public boolean updateToDoList(String username, ToDoList toDoList) {
        String url = baseUrl + "/" + URLEncoder.encode(username, StandardCharsets.UTF_8);
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
     * @param groupName the name of the user group
     * @return the ToDoList object if successful, null otherwise
     */
    public ToDoList loadGroupToDoList(String groupName) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(groupName, StandardCharsets.UTF_8);
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
     * @param groupName the name of the user group
     * @param toDoList  the updated ToDoList object
     * @return true if successful, false otherwise
     */
    public boolean updateGroupToDoList(String groupName, ToDoList toDoList) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(groupName, StandardCharsets.UTF_8);
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
