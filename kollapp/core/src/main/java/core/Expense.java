package core;

import java.io.Serializable;

/**
 * Represents an expense in the application.
 */
public class Expense implements Serializable {
    private String description;
    private double amount;
    private String paidBy;

    // Default constructor
    public Expense() {
    }

    // Parameterized constructor
    public Expense(String description, double amount, String paidBy) {
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
    }

    // Getters and setters
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }
    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }
}
