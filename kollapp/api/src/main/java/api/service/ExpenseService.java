package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.Expense;
import core.UserGroup;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {

    private final ObjectMapper mapper;
    private final String groupExpensePath;

    /**
     * Service class for handling expenses.
     * This class is responsible for managing the mapping of JSON data to Java objects
     * and setting up the file path for group expenses persistence.
     * 
     * The constructor initializes the ObjectMapper with a JavaTimeModule to handle
     * Java 8 date and time API serialization and deserialization.
     * It also sets up the file path for storing group expenses data.
     */
    public ExpenseService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        this.groupExpensePath = Paths.get("..", "persistence", "src", "main", "java",
        "persistence", "groupexpenses").toAbsolutePath()
        .normalize().toString() + File.separator;
    }


    /**
     * Loads the list of expenses for a given user group.
     *
     * @param groupName the name of the user group whose expenses are to be loaded
     * @return a list of expenses associated with the specified user group
     */
    public List<Expense> loadGroupExpenses(String groupName) {
        if (groupName == null) {
            throw new IllegalArgumentException("Group name cannot be null");
        }
        UserGroup group = new UserGroup(groupName);
        return loadExpensesForGroup(group);
    }

    /**
     * Updates the expenses for a specified user group.
     *
     * @param groupName the name of the group whose expenses are to be updated
     * @param expenses the list of expenses to be saved for the group
     */
    public void updateGroupExpenses(String groupName, List<Expense> expenses) {
        if (groupName == null) {
            throw new IllegalArgumentException("Group name cannot be null");
        }
        if (expenses == null) {
            throw new IllegalArgumentException("Expenses list cannot be null");
        }
        UserGroup group = new UserGroup(groupName);
        saveExpensesForGroup(group, expenses);
    }

    /**
     * Loads the list of expenses for a given user group from a JSON file.
     *
     * @param group the user group for which to load expenses
     * @return a list of expenses for the specified group, or an empty list if the file does not exist or an error occurs
     */
    private List<Expense> loadExpensesForGroup(UserGroup group) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        if (!file.exists()) {
            System.out.println("Warning: Expense file for group " + group.getGroupName() + " not found.");
            return new ArrayList<>(); 
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (IOException e) {
            System.out.println("Error: Failed to load expenses for group " + group.getGroupName() + ". " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the list of expenses for a given user group to a JSON file.
     * The file is named after the group's name and stored in the directory specified by groupExpensePath.
     * If the necessary directories do not exist, they will be created.
     *
     * @param group the user group for which the expenses are being saved
     * @param expenses the list of expenses to save
     * @throws IllegalArgumentException if an I/O error occurs during saving
     */
    private void saveExpensesForGroup(UserGroup group, List<Expense> expenses) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        try {
            File parentDir = file.getParentFile();
            // Check if parent directory exists or can be created
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IllegalArgumentException("Failed to create directory structure for path: " + groupExpensePath);
            }
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save expenses for group: " + group.getGroupName(), e);
        }
    }
}
