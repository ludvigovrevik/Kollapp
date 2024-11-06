package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents an expense in the application.
 */
public class Expense implements Serializable {
    private String description;
    private double amount;
    private String paidBy;
    private List<String> participants;
    private List<Settlement> settlements; // Updated field

    // Default constructor
    public Expense() {
    }

    // Parameterized constructor
    public Expense(String description, double amount, String paidBy, List<String> participants) {
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.participants = participants;
        this.settlements = new ArrayList<>();

        // Initialize settlements: everyone owes unless they are the payer
        for (String participant : participants) {
            if (!participant.equals(paidBy)) {
                settlements.add(new Settlement(participant, false)); // Not yet paid
            }
        }
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
    
    // Ensure participants and settlements are initialized
    public List<String> getParticipants() {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
    
    public List<Settlement> getSettlements() {
        if (settlements == null) {
            settlements = new ArrayList<>();
            // Optionally initialize settlements based on participants
            if (participants != null && paidBy != null) {
                for (String participant : participants) {
                    if (!participant.equals(paidBy)) {
                        settlements.add(new Settlement(participant, false));
                    }
                }
            }
        }
        return settlements;
    }

    public void setSettlements(List<Settlement> settlements) {
        this.settlements = settlements;
    }

    // Method to calculate share per person
    @JsonIgnore
    public double getSharePerPerson() {
        int numParticipants = getParticipants().size();
        return amount / numParticipants;
    }

    // Method to check if all participants have settled
    @JsonIgnore
    public boolean isFullySettled() {
        for (Settlement settlement : getSettlements()) {
            if (!settlement.isSettled()) {
                return false;
            }
        }
        return true;
    }

    // Method to mark a participant's share as paid
    public void settleParticipant(String username) {
        for (Settlement settlement : getSettlements()) {
            if (settlement.getUsername().equals(username)) {
                settlement.setSettled(true);
                break;
            }
        }
    }

    // Method to check if a participant has settled
    public boolean hasParticipantSettled(String username) {
        if (username.equals(paidBy)) {
            return true; // Payer is considered settled
        }
        for (Settlement settlement : getSettlements()) {
            if (settlement.getUsername().equals(username)) {
                return settlement.isSettled();
            }
        }
        return true; // Participants not in the settlement list are considered settled
    }
}

