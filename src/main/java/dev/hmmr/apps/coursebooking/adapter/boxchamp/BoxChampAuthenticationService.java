package dev.hmmr.apps.coursebooking.adapter.boxchamp;

import dev.hmmr.apps.coursebooking.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(BoxChampProperties.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class BoxChampAuthenticationService {
    private static final String USER_COULD_NOT_BE_AUTHENTICATED_MESSAGE = "User %s could not be authenticated. Please check username/password combination.";

    BoxChampHttpClient boxChampHttpClient;
    BoxChampProperties boxChampProperties;

    HttpEntity<HttpHeaders> authenticate(User user) {
        HttpEntity<MultiValueMap<String, String>> request = authenticationHeaders(user);
        String loginPath = boxChampProperties.getLoginPath();
        ResponseEntity<String> loginResponse = boxChampHttpClient.post(loginPath, request);

        String token = extractAndVerifySessionCookie(user, loginResponse);

        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.add(COOKIE, token);

        log.info("User {} logged in to box champ", user.getId());

        return new HttpEntity<>(getHeaders);
    }

    private String extractAndVerifySessionCookie(User user, ResponseEntity<String> loginResponse) {
        String sessionCookie = "";
        List<String> setCookieValues = loginResponse.getHeaders().get(SET_COOKIE);

        if (!isEmpty(setCookieValues)) {
            sessionCookie = setCookieValues.get(setCookieValues.size() - 1);
        }

        boolean hasLoginFailed = loginResponse.hasBody()
                && loginResponse.getBody().contains("login_errors");

        boolean hasAuthenticationFailed = hasLoginFailed || StringUtils.isEmpty(sessionCookie);

        if (hasAuthenticationFailed) {
            String userCouldNotBeAuthenticatedMessage = format(USER_COULD_NOT_BE_AUTHENTICATED_MESSAGE, user.getUsername());
            throw new BadCredentialsException(userCouldNotBeAuthenticatedMessage);
        }

        return sessionCookie;
    }

    private HttpEntity<MultiValueMap<String, String>> authenticationHeaders(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parts = new LinkedMultiValueMap<>();
        String queryParamEmail = boxChampProperties.getQueryParamEmail();
        String queryParamPassword = boxChampProperties.getQueryParamPassword();

        parts.add(queryParamEmail, user.getUsername());
        parts.add(queryParamPassword, user.getPassword());

        return new HttpEntity<>(parts, headers);
    }
}
