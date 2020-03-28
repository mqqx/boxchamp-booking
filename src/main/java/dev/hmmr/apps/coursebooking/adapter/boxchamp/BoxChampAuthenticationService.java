package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import dev.hmmr.apps.coursebooking.model.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class BoxChampAuthenticationService {
    private static final String QUERY_PARAM_EMAIL = "txEmail";
    private static final String QUERY_PARAM_PASSWORD = "txPassword";
    private static final String LOGIN_PATH = "home";

    BoxChampHttpClient boxChampHttpClient;

    HttpEntity<HttpHeaders> authenticate(User user) {
        HttpEntity<MultiValueMap<String, String>> request = authenticationHeaders(user);
        ResponseEntity<String> loginResponse = boxChampHttpClient.post(LOGIN_PATH, request);

        String token = extractAndVerifySessionCookie(user, loginResponse);

        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.add(HttpHeaders.COOKIE, token);

        log.info("User {} logged in to box champ", user.getId());

        return new HttpEntity<>(getHeaders);
    }

    private String extractAndVerifySessionCookie(User user, ResponseEntity<String> loginResponse) {
        String sessionCookie = "";
        List<String> setCookieValues = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (!CollectionUtils.isEmpty(setCookieValues)) {
            sessionCookie = setCookieValues.get(setCookieValues.size() - 1);
        }

        boolean hasLoginFailed = loginResponse.hasBody() &&
                loginResponse.getBody().contains("login_errors");

        if (hasLoginFailed || StringUtils.isEmpty(sessionCookie)) {
            throw new BadCredentialsException("User " + user.getUsername() + " could not be authenticated. Please check username/password combination.");
        }

        return sessionCookie;
    }

    private HttpEntity<MultiValueMap<String, String>> authenticationHeaders(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parts = new LinkedMultiValueMap<>();
        parts.add(QUERY_PARAM_EMAIL, user.getUsername());
        parts.add(QUERY_PARAM_PASSWORD, user.getPassword());

        return new HttpEntity<>(parts, headers);
    }
}
