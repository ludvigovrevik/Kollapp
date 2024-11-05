package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Expense;
import core.User;
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

    public ExpenseApiHandler() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Load expenses for a user
    public List<Expense> loadUserExpenses(User user) {
        String url = baseUrl + "/" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
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
            } else {
                System.err.println("Failed to load expenses. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while loading expenses: " + e.getMessage());
            return null;
        }
    }

    // Update expenses for a user
    public boolean updateUserExpenses(User user, List<Expense> expenses) {
        String url = baseUrl + "/" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8);
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
            System.err.println("An error occurred while updating expenses: " + e.getMessage());
            return false;
        }
    }

    // Load expenses for a group
    public List<Expense> loadGroupExpenses(UserGroup group) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(group.getGroupName(), StandardCharsets.UTF_8);
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
            } else {
                System.err.println("Failed to load group expenses. Status code: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while loading group expenses: " + e.getMessage());
            return null;
        }
    }

    // Update expenses for a group
    public boolean updateGroupExpenses(UserGroup group, List<Expense> expenses) {
        String url = baseUrl + "/groups/" + URLEncoder.encode(group.getGroupName(), StandardCharsets.UTF_8);
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
