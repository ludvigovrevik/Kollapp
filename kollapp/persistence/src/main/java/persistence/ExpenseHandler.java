package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.Expense;
import core.User;
import core.UserGroup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the persistence of expenses for users and groups.
 */
public class ExpenseHandler {
    private final String expensePath;
    private final String groupExpensePath;
    private final ObjectMapper mapper;

    /**
     * Constructs a new ExpenseHandler.
     */
    public ExpenseHandler() {
        this.expensePath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "expenses") + File.separator;
        this.groupExpensePath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groupexpenses") + File.separator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    // Constructors with custom paths if needed...

    /**
     * Assigns a new expense list to the specified user.
     *
     * @param user the user to whom the expense list will be assigned
     */
    public void assignExpenseList(User user) {
        List<Expense> expenses = new ArrayList<>();
        File file = new File(expensePath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to assign expense list to user");
        }
    }

    /**
     * Loads the expense list for the specified user.
     *
     * @param user the user whose expenses are to be loaded
     * @return the list of expenses
     */
    public List<Expense> loadExpenses(User user) {
        File file = new File(expensePath + user.getUsername() + ".json");
        if (!file.exists()) {
            return new ArrayList<>(); // Return an empty list if file doesn't exist
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load expenses");
        }
    }

    /**
     * Updates the expense list for the specified user.
     *
     * @param user     the user whose expenses are being updated
     * @param expenses the updated list of expenses
     */
    public void updateExpenses(User user, List<Expense> expenses) {
        File file = new File(expensePath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to update expenses");
        }
    }

    /**
     * Similar methods for groups...
     */
    public void assignGroupExpenseList(UserGroup group) {
        List<Expense> expenses = new ArrayList<>();
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        try {
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to assign expense list to group");
        }
    }

    public List<Expense> loadGroupExpenses(UserGroup group) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        if (!file.exists()) {
            return new ArrayList<>(); // Return an empty list if file doesn't exist
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load group expenses");
        }
    }

    public void updateGroupExpenses(UserGroup group, List<Expense> expenses) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        try {
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to update group expenses");
        }
    }
}
