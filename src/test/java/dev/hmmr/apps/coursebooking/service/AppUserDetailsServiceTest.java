package dev.hmmr.apps.coursebooking.service;

import dev.hmmr.apps.coursebooking.model.AppUserPrincipal;
import dev.hmmr.apps.coursebooking.model.User;
import dev.hmmr.apps.coursebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AppUserDetailsServiceTest {
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    @Mock
    UserRepository userRepository;

    AppUserDetailsService sut;

    @BeforeEach
    void setUp() {
        sut = new AppUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Should return the suiting user object when called with a username of an existing user.")
    void loadUserByUsername() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        AppUserPrincipal expectedUserDetails = new AppUserPrincipal(user);

        UserDetails actualUserDetails = sut.loadUserByUsername(USERNAME);

        assertThat(actualUserDetails).isEqualTo(expectedUserDetails);
    }

    @Test
    @DisplayName("Should throw an exception when no user is being found with the given username.")
    void loadUserByUsernameThrows() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> sut.loadUserByUsername(USERNAME))
                .withMessageContaining(USERNAME);
    }
}
