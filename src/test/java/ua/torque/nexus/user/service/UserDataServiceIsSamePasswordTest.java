package ua.torque.nexus.user.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataService – isSamePassword() testing")
class UserDataServiceIsSamePasswordTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDataService userDataService;

    private String userExistingPassword;

    private static Method isSamePasswordMethod;

    @BeforeAll
    static void initReflection() throws Exception {
        isSamePasswordMethod = UserDataService.class
                .getDeclaredMethod("isSamePassword", String.class, String.class);
        isSamePasswordMethod.setAccessible(true);
    }

    @AfterAll
    static void tearDownReflection() {
        isSamePasswordMethod.setAccessible(false);
    }

    @BeforeEach
    void beforeEach() {
        userExistingPassword = passwordEncoder.encode("Password1234");
    }


    private boolean callIsSamePassword(String old, String raw) throws Exception {
        return (boolean) isSamePasswordMethod.invoke(userDataService, old, raw);
    }

    @Test
    void whenPasswordsMatch_andValidRawPassword_thenReturnTrue() throws Exception {
        when(passwordEncoder.matches("validRaw", userExistingPassword)).thenReturn(true);

        assertTrue(callIsSamePassword(userExistingPassword, "validRaw"));
    }

    @Test
    void whenPasswordsMatch_andInvalidRawPassword_thenReturnTrue() throws Exception {
        when(passwordEncoder.matches("invalidRaw", userExistingPassword)).thenReturn(true);

        assertTrue(callIsSamePassword(userExistingPassword, "invalidRaw"));
    }

    @Test
    void whenPasswordsDoNotMatch_andValidRawPassword_thenReturnFalse() throws Exception {
        when(passwordEncoder.matches("validRaw", userExistingPassword)).thenReturn(false);

        assertFalse(callIsSamePassword(userExistingPassword, "validRaw"));
    }

    @Test
    void whenPasswordsDoNotMatch_andInvalidRawPassword_thenReturnFalse() throws Exception {
        when(passwordEncoder.matches("invalidRaw", userExistingPassword)).thenReturn(false);

        assertFalse(callIsSamePassword(userExistingPassword, "invalidRaw"));
    }
}
