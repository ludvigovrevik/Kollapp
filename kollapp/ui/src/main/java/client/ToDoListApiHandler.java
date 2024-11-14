package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.ToDoList;
import core.User;
import core.UserGroup;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ToDoListApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "http://localhost:8080/api/v1/todolists";

    public ToDoListApiHandler() {
        this.httpClient = createHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    protected HttpClient createHttpClient() {
        return HttpClient.newHttpClient();
    }

    private String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8)
                        .replace("+", "%20");
    }

    /**
     * Loads the to-do list for a specific user.
     *
     * @param user the user whose to-do list is to be loaded
     * @return the ToDoList object if successful, null otherwise
     */
    public Optional<ToDoList> loadToDoList(User user) {
        String url = baseUrl + "/" + encodePathSegment(user.getUsername());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(objectMapper.readValue(response.body(), ToDoList.class));
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }

    /**
     * Assigns a new to-do list to a specific user.
     *
     * @param user the user to whom the ToDoList will be assigned
     * @return true if successful, false otherwise
     */
    public boolean assignToDoList(User user) {
        String url = baseUrl + "/" + encodePathSegment(user.getUsername());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            return statusCode == 201 || statusCode == 200;
        } catch (IOException | InterruptedException e) {
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
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            return false;
        }

        String url = baseUrl + "/" + encodePathSegment(user.getUsername());

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
            return false;
        }
    }

    /**
     * Loads the to-do list for a specific user group.
     *
     * @param userGroup the user group whose to-do list is to be loaded
     * @return the ToDoList object if successful, null otherwise
     */
    public Optional<ToDoList> loadGroupToDoList(UserGroup userGroup) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(userGroup.getGroupName(), StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(objectMapper.readValue(response.body(), ToDoList.class));
            } else {
                System.err.println("Failed to load group to-do list. Status code: " + response.statusCode());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
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
        String url = baseUrl + "/groups/" + encodePathSegment(userGroup.getGroupName());
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
            return false;
        }
    }
}
