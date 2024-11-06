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



    public ExpenseService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        this.groupExpensePath = Paths.get("kollapp", "persistence", "src", "main", "java",
        "persistence", "groupexpenses") + File.separator;
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

    private List<Expense> loadExpensesForGroup(UserGroup group) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        if (!file.exists()) {
            return new ArrayList<>(); // Return empty list if file doesn't exist
        }
        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveExpensesForGroup(UserGroup group, List<Expense> expenses) {
        File file = new File(groupExpensePath + group.getGroupName() + ".json");
        try {
            // **Ensure parent directories exist**
            file.getParentFile().mkdirs(); // This line ensures the directory is created
            mapper.writeValue(file, expenses);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to save expenses for group: " + group.getGroupName(), e);
        }
    }
}
