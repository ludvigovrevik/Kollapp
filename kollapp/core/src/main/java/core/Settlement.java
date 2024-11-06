// File: core/Settlement.java

package core;

import java.io.Serializable;

/**
 * Represents a settlement status for a participant in an expense.
 */
public class Settlement implements Serializable {
    private String username;
    private boolean isSettled;

    // Default constructor
    public Settlement() {
    }

    // Parameterized constructor
    public Settlement(String username, boolean isSettled) {
        this.username = username;
        this.isSettled = isSettled;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }
}
