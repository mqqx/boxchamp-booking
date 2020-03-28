package dev.hmmr.apps.coursebooking.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AppUserPrincipalTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Mock
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
    }

    @Test
    @DisplayName("Should set username and password from given user object when initialized.")
    void getPassword() {
        AppUserPrincipal actualAppUserPrincipal = new AppUserPrincipal(user);

        assertThat(actualAppUserPrincipal.getUsername()).isEqualTo(USERNAME);
        assertThat(actualAppUserPrincipal.getPassword()).isEqualTo(PASSWORD);
    }
}
