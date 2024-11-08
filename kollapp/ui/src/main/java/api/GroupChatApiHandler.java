package api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; 
import com.fasterxml.jackson.databind.SerializationFeature;  

import core.GroupChat;
import core.Message;

/**
 * Handler class for interacting with the GroupChat REST API.
 */
public class GroupChatApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "http://localhost:8080/api/v1/groupchats/";

    /**
     * Constructor for GroupChatApiHandler.
     *
     * @param baseUrl The base URL of the GroupChat REST API (e.g., "http://localhost:8080/api/v1/groupchats").
     */
    public GroupChatApiHandler() {
        this.httpClient = createHttpClient();
        this.objectMapper = new ObjectMapper();

        // Register the JavaTimeModule to handle Java 8 Date/Time API types
        this.objectMapper.registerModule(new JavaTimeModule());

        // Optional: Configure the ObjectMapper to serialize dates as ISO-8601 strings instead of timestamps
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    protected HttpClient createHttpClient() {
        return HttpClient.newHttpClient();
    }

    /**
     * Creates a new group chat by making an HTTP POST request to the API.
     *
     * @param groupName The name of the group chat to create.
     * @return true if the group chat was created successfully; false otherwise.
     */
    public boolean createGroupChat(String groupName) {
        try {
            String url = baseUrl + groupName;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("Group chat created successfully.");
                return true;
            } else {
                System.out.println("Failed to create group chat. Status Code: " + response.statusCode());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while creating group chat: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a GroupChat from the API.
     *
     * @param groupName The name of the group chat to retrieve.
     * @return An Optional containing the GroupChat if found; otherwise, Optional.empty().
     */
    public Optional<GroupChat> getGroupChat(String groupName) {
        try {
            String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
            String url = baseUrl + encodedGroupName;
            System.out.println(url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                GroupChat groupChat = objectMapper.readValue(responseBody, GroupChat.class);
                return Optional.of(groupChat);
            } else {
                System.out.println("Group chat not found. Status Code: " + response.statusCode());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while retrieving group chat: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Sends a message to a specific group chat by making an HTTP POST request to the API.
     *
     * @param groupName The name of the group chat.
     * @param message   The message to send.
     * @return true if the message was sent successfully; false otherwise.
     */
    public boolean sendMessage(String groupName, Message message) {
        try {
            String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
            String url = baseUrl + encodedGroupName + "/messages";

            String requestBody = objectMapper.writeValueAsString(message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Message sent successfully.");
                return true;
            } else {
                System.out.println("Failed to send message. Status Code: " + response.statusCode());
                return false;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while sending message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all messages from a specific group chat by making an HTTP GET request to the API.
     *
     * @param groupName The name of the group chat.
     * @return An Optional containing the list of messages if found; otherwise, Optional.empty().
     */
    public Optional<List<Message>> getMessages(String groupName) {
        try {
            String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
            String url = baseUrl + encodedGroupName + "/messages";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                List<Message> messages = objectMapper.readValue(responseBody, new TypeReference<List<Message>>() { });
                return Optional.of(messages);
            } else {
                System.out.println("Failed to retrieve messages. Status Code: " + response.statusCode());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while retrieving messages: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if a group chat exists by making an HTTP GET request to the API.
     *
     * @param groupName The name of the group chat to check.
     * @return true if the group chat exists; false otherwise.
     */
    public boolean groupChatExists(String groupName) {
        try {
            String encodedGroupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8);
            String url = baseUrl + encodedGroupName;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while checking group chat existence: " + e.getMessage());
            return false;
        }
    }
}