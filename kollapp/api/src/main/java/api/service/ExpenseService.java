package api.service;

import core.Expense;
import core.User;
import core.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.ExpenseHandler;
import persistence.UserHandler;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseHandler expenseHandler;
    private final UserHandler userHandler;

    @Autowired
    public ExpenseService() {
        this.expenseHandler = new ExpenseHandler();
        this.userHandler = new UserHandler();
    }

    public List<Expense> loadUserExpenses(String username) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            return expenseHandler.loadExpenses(userOpt.get());
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    public void updateUserExpenses(String username, List<Expense> expenses) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            expenseHandler.updateExpenses(userOpt.get(), expenses);
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    public List<Expense> loadGroupExpenses(String groupName) {
        UserGroup group = new UserGroup(groupName);
        return expenseHandler.loadGroupExpenses(group);
    }

    public void updateGroupExpenses(String groupName, List<Expense> expenses) {
        UserGroup group = new UserGroup(groupName);
        expenseHandler.updateGroupExpenses(group, expenses);
    }
}
