package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.model.User;
import com.hammer.apps.boxchampbooking.util.AppUtils;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AuthenticationService {
	private static final String LOGIN_PATH = "home";
	private static final String QUERY_PARAM_EMAIL = "txEmail";
	private static final String QUERY_PARAM_PASSWORD = "txPassword";


	public HttpEntity login(RestTemplate restTemplate, User user) {
		String sessionCookie = getSessionCookie(restTemplate, user);
		HttpHeaders getHeaders = getGetHeaders(sessionCookie);
		return new HttpEntity(getHeaders);
	}

	private HttpHeaders getGetHeaders(String sessionCookie) {
		HttpHeaders getHeaders = new HttpHeaders();
		getHeaders.add(HttpHeaders.COOKIE, sessionCookie);
		return getHeaders;
	}

	private String getSessionCookie(RestTemplate restTemplate, User user) {
		HttpEntity<MultiValueMap<String, String>> request = buildLoginEntity(user);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity(AppUtils.buildUrl(LOGIN_PATH), request, String.class);
		String sessionCookie = extractAndVerifySessionCookie(user, loginResponse);
		return sessionCookie;
	}

	private String extractAndVerifySessionCookie(User user, ResponseEntity<String> loginResponse) {
		String sessionCookie = "";
		List<String> setCookieValues = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
		if (!CollectionUtils.isEmpty(setCookieValues)) {
			sessionCookie = setCookieValues.get(setCookieValues.size() - 1);
		}

		boolean hasLoginFailed = loginResponse.hasBody() && loginResponse.getBody().contains("login_errors");

		if (hasLoginFailed || StringUtils.isEmpty(sessionCookie)) {
			throw new BadCredentialsException("User " + user.getUsername() + " could not be logged in. Please check username/password combination.");
		}

		return sessionCookie;
	}

	private HttpEntity<MultiValueMap<String, String>> buildLoginEntity(User user) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> parts = new LinkedMultiValueMap<>();
		parts.add(QUERY_PARAM_EMAIL, user.getUsername());
		parts.add(QUERY_PARAM_PASSWORD, user.getPassword());

		return new HttpEntity<>(parts, headers);
	}
}
