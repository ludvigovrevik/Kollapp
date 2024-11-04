package core;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents an expense in the application.
 */
public class Expense implements Serializable {
    private String description;
    private double amount;
    private String paidBy;
    private LocalDate date;

    /**
     * Default constructor.
     */
    public Expense() {
    }

    /**
     * Constructs a new Expense with specified details.
     *
     * @param description The description of the expense.
     * @param amount      The amount of the expense.
     * @param paidBy      The username of the person who paid.
     * @param date        The date of the expense.
     */
    public Expense(String description, double amount, String paidBy, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.date = date;
    }

    /**
     * Copy constructor for Expense.
     *
     * @param expense The expense to copy.
     */
    public Expense(Expense expense) {
        this.description = expense.description;
        this.amount = expense.amount;
        this.paidBy = expense.paidBy;
        this.date = expense.date;
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
  
    public LocalDate getDate() {
        return date;
    }
  
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
