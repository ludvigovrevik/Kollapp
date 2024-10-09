package ui;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import core.User;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import persistence.UserHandler;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;


class LoginControllerTest {

    @Mock
    private TextField usernameField;

    @Mock
    private PasswordField passwordField;

    @Mock
    private Label loginErrorMessage;

    @InjectMocks
    private LoginController loginController;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set default behavior for UI components
        when(usernameField.getText()).thenReturn("testUser");
        when(passwordField.getText()).thenReturn("testPassword");
    }

    @Test
    void testHandleLoginButton_UserDoesNotExist() throws Exception {
        try (MockedStatic<UserHandler> mockedUserHandler = mockStatic(UserHandler.class)) {
            // Mock UserHandler to simulate user not existing
            mockedUserHandler.when(() -> UserHandler.userExists("testUser")).thenReturn(false);

            // Call the method under test
            loginController.handleLoginButtonAction();

            // Verify that the correct error message is shown
            verify(loginErrorMessage).setText("No such user exists.");
        }
    }

    @Test
    void testHandleLoginButton_IncorrectPassword() throws Exception {
        try (MockedStatic<UserHandler> mockedUserHandler = mockStatic(UserHandler.class)) {
            // Mock UserHandler to simulate user existing but with incorrect password
            mockedUserHandler.when(() -> UserHandler.userExists("testUser")).thenReturn(true);
            mockedUserHandler.when(() -> UserHandler.loadUser("testUser", "testPassword")).thenReturn(null);

            // Call the method under test
            loginController.handleLoginButtonAction();

            // Verify that the correct error message is shown
            verify(loginErrorMessage).setText("Incorrect password. Please try again.");
        }
    }

    @Test
    void testHandleLoginButton_SuccessfulLogin() throws Exception {
        try (MockedStatic<UserHandler> mockedUserHandler = mockStatic(UserHandler.class)) {
            // Mock UserHandler to simulate user existing and correct password
            User mockUser = mock(User.class);
            mockedUserHandler.when(() -> UserHandler.userExists("testUser")).thenReturn(true);
            mockedUserHandler.when(() -> UserHandler.loadUser("testUser", "testPassword")).thenReturn(mockUser);

            // Spy on the loginController and mock the loadKollektivScene method
            LoginController loginControllerSpy = spy(loginController);
            doNothing().when(loginControllerSpy).loadKollektivScene(mockUser); // Mock the method

            // Call the method under test
            loginControllerSpy.handleLoginButtonAction();

            // Verify that loadKollektivScene is called with the correct user
            verify(loginControllerSpy, times(1)).loadKollektivScene(mockUser);

            // Also verify that no error message was set
            verify(loginErrorMessage, never()).setText(anyString());
        }
    }
}
