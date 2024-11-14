package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.Expense;
import core.UserGroup;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExpenseApiHandler {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "http://localhost:8080/api/v1/expenses";

    /**
     * Constructs an instance of ExpenseApiHandler.
     * Initializes the HttpClient and ObjectMapper.
     * Registers the JavaTimeModule with the ObjectMapper to handle Java 8 date and time API.
     */
    public ExpenseApiHandler() {
        this.httpClient = createHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    protected HttpClient createHttpClient() {
        return HttpClient.newHttpClient();
    }

    /**
     * Helper method to properly encode URL path segments, ensuring spaces are encoded as %20
     */
    private String encodePathSegment(String segment) {
        return URLEncoder.encode(segment, StandardCharsets.UTF_8)
                        .replace("+", "%20");
    }

    /**
     * Loads the expenses for a given user group from the server.
     *
     * @param group the user group whose expenses are to be loaded
     * @return a list of expenses for the specified group, or null if no expenses are found or an error occurs
     */
    public List<Expense> loadGroupExpenses(UserGroup group) {
        String encodedGroupName = encodePathSegment(group.getGroupName());
        String url = baseUrl + "/groups/" + encodedGroupName;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<Expense> expenses = objectMapper.readValue(response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
                return expenses;
            } else if (response.statusCode() == 404) {
                // Return empty list if no expenses found
                return null;
            } else {
                System.err.println("Failed to load group expenses. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while loading group expenses: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates the expenses for a given user group by sending a PUT request to the server.
     *
     * @param group the user group whose expenses are to be updated
     * @param expenses the list of expenses to be updated for the group
     * @return true if the update was successful (HTTP status code 200), false otherwise
     */
    public boolean updateGroupExpenses(UserGroup group, List<Expense> expenses) {
        String encodedGroupName = encodePathSegment(group.getGroupName());
        String url = baseUrl + "/groups/" + encodedGroupName;
        
        try {
            String jsonBody = objectMapper.writeValueAsString(expenses);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while updating group expenses: " + e.getMessage());
            return false;
        }
    }
}