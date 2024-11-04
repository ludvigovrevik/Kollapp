package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.Expense;
import core.User;
import core.UserGroup;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ExpenseApiHandler {
    private final String baseUrl = "http://localhost:8080/api/v1/expenses";
    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    public ExpenseApiHandler() {
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<Expense> loadGroupExpenses(UserGroup group) {
        String url = baseUrl + "/groups/" + group.getGroupName();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Expense[] expensesArray = mapper.readValue(response.body(), Expense[].class);

            return Arrays.asList(expensesArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load group expenses: " + e.getMessage());
        }
    }

    public void updateGroupExpenses(UserGroup group, List<Expense> expenses) {
        String url = baseUrl + "/groups/" + group.getGroupName();
        try {
            String json = mapper.writeValueAsString(expenses);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to update group expenses: HTTP error code " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update group expenses: " + e.getMessage());
        }
    }

    // Similar methods for user expenses
    public List<Expense> loadUserExpenses(User user) {
        String url = baseUrl + "/" + user.getUsername();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Expense[] expensesArray = mapper.readValue(response.body(), Expense[].class);

            return Arrays.asList(expensesArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load user expenses: " + e.getMessage());
        }
    }

    public void updateUserExpenses(User user, List<Expense> expenses) {
        String url = baseUrl + "/" + user.getUsername();
        try {
            String json = mapper.writeValueAsString(expenses);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to update user expenses: HTTP error code " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update user expenses: " + e.getMessage());
        }
    }
}
