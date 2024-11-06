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

    /**
     * Constructs an Expense object with the specified description, amount, payer, and participants.
     * Initializes the settlements list where each participant (except the payer) owes money.
     *
     * @param description A brief description of the expense.
     * @param amount The total amount of the expense.
     * @param paidBy The participant who paid for the expense.
     * @param participants A list of participants involved in the expense.
     */
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
    

    /**
     * Retrieves the list of participants. If the participants list is null,
     * it initializes it as an empty ArrayList.
     *
     * @return a list of participants
     */
    public List<String> getParticipants() {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
    
    /**
     * Retrieves the list of settlements for the expense. If the settlements list is null,
     * it initializes the list and optionally populates it based on the participants and
     * the person who paid.
     *
     * @return a list of settlements for the expense. If the settlements list was null,
     *         it will be initialized and populated with settlements for each participant
     *         who did not pay.
     */
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

    /**
     * Marks the settlement of a participant as settled.
     *
     * @param username the username of the participant whose settlement is to be marked as settled
     */
    public void settleParticipant(String username) {
        for (Settlement settlement : getSettlements()) {
            if (settlement.getUsername().equals(username)) {
                settlement.setSettled(true);
                break;
            }
        }
    }

    // Method to check if a participant has settled
    /**
     * Checks if a participant has settled their share of the expense.
     *
     * @param username the username of the participant to check
     * @return true if the participant has settled their share, false otherwise
     */
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

