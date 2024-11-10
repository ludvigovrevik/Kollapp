package core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the {@link Expense} class.
 */
@Tag("expense")
public class ExpenseTest {

    private Expense expense;
    private List<String> participants;
    private static final String DESCRIPTION = "Dinner";
    private static final double AMOUNT = 100.0;
    private static final String PAID_BY = "Alice";

    @BeforeEach
    @DisplayName("Initialize Expense object before each test")
    void setUp() {
        participants = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie"));
        expense = new Expense(DESCRIPTION, AMOUNT, PAID_BY, participants);
    }

    @Test
    @DisplayName("Test default constructor for Jackson")
    @Tag("constructor")
    void testDefaultConstructor() {
        Expense defaultExpense = new Expense();
        assertNotNull(defaultExpense, "Expense object should not be null");
    }

    @Test
    @DisplayName("Test parameterized constructor sets values")
    @Tag("constructor")
    void testParameterizedConstructor() {
        assertEquals(DESCRIPTION, expense.getDescription(), "Description should match the initialized value");
        assertEquals(AMOUNT, expense.getAmount(), "Amount should match the initialized value");
        assertEquals(PAID_BY, expense.getPaidBy(), "PaidBy should match the initialized value");
        assertEquals(participants, expense.getParticipants(), "Participants should match the initialized list");
        
        // Test settlements initialization
        List<Settlement> settlements = expense.getSettlements();
        assertEquals(2, settlements.size(), "Should have settlements for all participants except payer");
        assertTrue(settlements.stream().anyMatch(s -> s.getUsername().equals("Bob")), "Should have settlement for Bob");
        assertTrue(settlements.stream().anyMatch(s -> s.getUsername().equals("Charlie")), "Should have settlement for Charlie");
    }

    @Test
    @DisplayName("Test setting and getting description")
    @Tag("setter-getter")
    void testDescription() {
        String newDescription = "Lunch";
        expense.setDescription(newDescription);
        assertEquals(newDescription, expense.getDescription(), "Description should be updated to the new value");
    }

    @Test
    @DisplayName("Test setting and getting amount")
    @Tag("setter-getter")
    void testAmount() {
        double newAmount = 150.0;
        expense.setAmount(newAmount);
        assertEquals(newAmount, expense.getAmount(), "Amount should be updated to the new value");
    }

    @Test
    @DisplayName("Test setting and getting paidBy")
    @Tag("setter-getter")
    void testPaidBy() {
        String newPayer = "Bob";
        expense.setPaidBy(newPayer);
        assertEquals(newPayer, expense.getPaidBy(), "PaidBy should be updated to the new value");
    }

    @Test
    @DisplayName("Test setting and getting participants")
    @Tag("setter-getter")
    void testParticipants() {
        List<String> newParticipants = Arrays.asList("Dave", "Eve");
        expense.setParticipants(newParticipants);
        assertEquals(newParticipants, expense.getParticipants(), "Participants should be updated to the new list");
    }

    @Test
    @DisplayName("Test getting participants with null list")
    @Tag("getter")
    void testGetParticipantsWhenNull() {
        Expense newExpense = new Expense();
        assertNotNull(newExpense.getParticipants(), "Participants list should be initialized when null");
        assertTrue(newExpense.getParticipants().isEmpty(), "Participants list should be empty when initialized from null");
    }

    @Test
    @DisplayName("Test getting settlements with null list")
    @Tag("getter")
    void testGetSettlementsWhenNull() {
        Expense newExpense = new Expense();
        assertNotNull(newExpense.getSettlements(), "Settlements list should be initialized when null");
        assertTrue(newExpense.getSettlements().isEmpty(), "Settlements list should be empty when initialized from null");
    }

    @Test
    @DisplayName("Test share per person calculation")
    @Tag("calculation")
    void testGetSharePerPerson() {
        assertEquals(AMOUNT / participants.size(), expense.getSharePerPerson(), 
            "Share per person should be total amount divided by number of participants");
    }

    @Test
    @DisplayName("Test isFullySettled method")
    @Tag("status")
    void testIsFullySettled() {
        assertFalse(expense.isFullySettled(), "Newly created expense should not be fully settled");
        
        expense.settleParticipant("Bob");
        assertFalse(expense.isFullySettled(), "Expense should not be fully settled with only one participant settled");
        
        expense.settleParticipant("Charlie");
        assertTrue(expense.isFullySettled(), "Expense should be fully settled after all participants have settled");
    }

    @Test
    @DisplayName("Test settleParticipant method")
    @Tag("settlement")
    void testSettleParticipant() {
        expense.settleParticipant("Bob");
        assertTrue(expense.hasParticipantSettled("Bob"), "Bob should be marked as settled");
        assertFalse(expense.hasParticipantSettled("Charlie"), "Charlie should still be marked as not settled");
    }

    @Test
    @DisplayName("Test hasParticipantSettled method")
    @Tag("settlement")
    void testHasParticipantSettled() {
        // Payer should always be considered settled
        assertTrue(expense.hasParticipantSettled(PAID_BY), "Payer should be considered settled by default");
        
        // Other participants should start as not settled
        assertFalse(expense.hasParticipantSettled("Bob"), "Non-payer should start as not settled");
        
        // After settling
        expense.settleParticipant("Bob");
        assertTrue(expense.hasParticipantSettled("Bob"), "Participant should be settled after settling");
        
        // Non-participant should be considered settled
        assertTrue(expense.hasParticipantSettled("NonParticipant"), 
            "Non-participants should be considered settled");
    }
}