package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.Expense;
import core.User;
import core.UserGroup;
import org.springframework.stereotype.Service;
import persistence.UserHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ObjectMapper mapper;
    private final UserHandler userHandler;
    private final String expensePath;
    private final String groupExpensePath;

    public ExpenseService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.userHandler = new UserHandler();

        this.expensePath = Paths.get("kollapp", "persistence", "src", "main", "java", "persistence", "expenses") + File.separator;
        this.groupExpensePath = Paths.get("kollapp", "persistence", "src", "main", "java", "persistence", "groupexpenses") + File.separator;
    }

    // Load expenses for a user
    public List<Expense> loadUserExpenses(String username) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            return loadExpensesForUser(userOpt.get());
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    // Update expenses for a user
    public void updateUserExpenses(String username, List<Expense> expenses) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            saveExpensesForUser(userOpt.get(), expenses);
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    // Load expenses for a group
    public List<Expense> loadGroupExpenses(String groupName) {
        UserGroup group = new UserGroup(groupName);
        return loadExpensesForGroup(group);
    }

    // Update expenses for a group
    public void updateGroupExpenses(String groupName, List<Expense> expenses) {
        UserGroup group = new UserGroup(groupName);
        saveExpensesForGroup(group, expenses);
    }

    // Helper methods for loading and saving expenses

    private List<Expense> loadExpensesForUser(User user) {
        File file = new File(expensePath + user.getUsername() + ".json");
        if (!file.exists()) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load expenses for user: " + user.getUsername(), e);
        }
    }

    private void saveExpensesForUser(User user, List<Expense> expenses) {
        File file = new File(expensePath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save expenses for user: " + user.getUsername(), e);
        }
    }

    private List<Expense> loadExpensesForGroup(UserGroup group) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        if (!file.exists()) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load expenses for group: " + group.getGroupName(), e);
        }
    }

    private void saveExpensesForGroup(UserGroup group, List<Expense> expenses) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        try {
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save expenses for group: " + group.getGroupName(), e);
        }
    }
}
