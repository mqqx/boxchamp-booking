package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import dev.hmmr.apps.coursebooking.model.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BoxChampAuthenticationServiceTest {

    private static final String SESSION_COOKIE = "cisession=O0gyK4f5kmbibsFkcj%2FwjwkPSTAVmjodCDffoJB4BUbcmX1qkIgt%2FlccNJ0X1beISUhljR1gUU3RBvxKk0iEpm4Ke3aKTCE8IbXHbX8QP%2FwpTHIGz8WuHHf5Hw0Llz17rJIUCUNHZvno3L18rPr0A4cSBBzAXmdPWnoNkqK8GrMlCJs7AvWgDRIMaJSmqp7%2F6ro8UAHF0zZr5eBtcYBzkeQRZHXwJY2EAKULjcNRJB3z6MIucXxiXzLzdgEEC5CRYLcxuMz%2Bwn54y23VHrLdMF6WuW%2FM4ReFv4FBB%2BnhctaHD11TKBjWwU%2FZ2jCO0GgD9jVTXWL8ccbRgxOJyAcvYQ%3D%3D; expires=Wed, 21-Aug-2019 21:13:31 GMT; Max-Age=21600; path=/";
    BoxChampAuthenticationService loginService;

    @Mock
    BoxChampHttpClient boxChampHttpClient;

    @BeforeEach
    void setUp() {
        loginService = new BoxChampAuthenticationService(boxChampHttpClient);
    }

    @Test
    @DisplayName("Should return authenticated cookie when the user could be authenticated.")
    void authenticate_ok() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String username = "username";
        String password = "password";
        MultiValueMap<String, String> parts = new LinkedMultiValueMap<>();
        parts.add("txEmail", username);
        parts.add("txPassword", password);

        ResponseEntity<String> responseEntity = ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, SESSION_COOKIE)
                .body("<!doctype html><html />");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(parts, headers);
        when(boxChampHttpClient.post("home", httpEntity)).thenReturn(responseEntity);

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        HttpEntity<HttpHeaders> sut = loginService.authenticate(user);

        assertThat(sut).isNotNull();
        assertThat(sut.getBody()).isNull();
        assertThat(sut.getHeaders().get(HttpHeaders.COOKIE)).contains(SESSION_COOKIE);
    }

    @Test
    @DisplayName("Should throw exception if session cookie is missing in authentication server response.")
    void authenticate_missingBody() {
        when(boxChampHttpClient.post(anyString(), any())).thenReturn(ResponseEntity
                .ok()
                .body(""));

        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> loginService.authenticate(new User()))
                .withMessageContaining("could not be authenticated");
    }
}
