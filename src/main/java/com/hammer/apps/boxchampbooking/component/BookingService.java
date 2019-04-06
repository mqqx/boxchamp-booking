package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.Application;
import com.hammer.apps.boxchampbooking.model.Booking;
import com.hammer.apps.boxchampbooking.model.ClassType;
import com.hammer.apps.boxchampbooking.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BookingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private static final String APP_URL = "https://boxchamp.io/";
	private static final String CLASS_LIST_ATHLETE_VIEW_REGEX = ".*?classlist/athlete/view/(.*?)\"";
	private static final String CLASS_LIST_HOME_PROGRESS_REGEX = ".*?classlist/home/progress_tooltip/(.*?)\"";
	private static final String QUERY_PARAM_EMAIL = "txEmail";
	private static final String QUERY_PARAM_PASSWORD = "txPassword";
	private static final String CLASS_LIST_PATH = "classlist?date=";
	private static final String CLASS_BOOKING_PATH = "classlist/athlete/book/";
	private static final String LOGIN_PATH = "home";

	public ResponseEntity<String> bookClass(RestTemplate restTemplate, Booking booking) {

		HttpEntity authorizedEntity = login(restTemplate, booking.getUser());
		ResponseEntity<String> bookingResponse = getAndBookClass(restTemplate, authorizedEntity, booking);
//TODO: check response for error/success message and inform user
		return bookingResponse;
	}

	private ResponseEntity<String> getAndBookClass(RestTemplate restTemplate, HttpEntity authorizedEntity, Booking booking) {
		String classId = getClassId(restTemplate, authorizedEntity, booking.getType());

		String bookingUrl = buildUrl(CLASS_BOOKING_PATH + classId);

		return restTemplate.exchange(bookingUrl, HttpMethod.GET, authorizedEntity, String.class);
	}

	private String buildUrl(String urlPath) {
		return APP_URL + urlPath;
	}

	private HttpEntity login(RestTemplate restTemplate, User user) {
		String sessionCookie = getSessionCookie(restTemplate, user);
		HttpHeaders getHeaders = getGetHeaders(sessionCookie);
		return new HttpEntity(getHeaders);
	}

	private String getClassId(RestTemplate restTemplate, HttpEntity authorizedEntity, ClassType type) {
		String classType = type.getName();

		String classSearchUrl = buildClassSearchUrl();

		ResponseEntity<String> classSearchHttpResponse = restTemplate.exchange(classSearchUrl, HttpMethod.GET, authorizedEntity, String.class);

		String classId = extractClassIdFromResponse(classType, classSearchHttpResponse);

		return classId;
	}

	private String extractClassIdFromResponse(String classType, ResponseEntity<String> classSearchHttpResponse) {
		String classId = null;
		Pattern pattern = Pattern.compile(classType + CLASS_LIST_ATHLETE_VIEW_REGEX, Pattern.DOTALL);

		Matcher matcher = pattern.matcher(classSearchHttpResponse.toString());

		if (matcher.find()) {
			classId = matcher.group(1).trim();
		} else {
			pattern = Pattern.compile(classType + CLASS_LIST_HOME_PROGRESS_REGEX, Pattern.DOTALL);
			matcher = pattern.matcher(classSearchHttpResponse.toString());
			if (matcher.find()) {
				classId = matcher.group(1).trim();
			}
		}

		if (classId == null) {
			throw new IllegalArgumentException("No " + classType + " class found. Please consider rescheduling.");
		}
		return classId;
	}

	private String buildClassSearchUrl() {
		// search requested class in two weeks
		LocalDate twoWeeksFromToday = LocalDate.now().plusWeeks(2);
		String formattedDate = twoWeeksFromToday.format(DateTimeFormatter.ISO_LOCAL_DATE);
		return buildUrl(CLASS_LIST_PATH + formattedDate);
	}

	private HttpHeaders getGetHeaders(String sessionCookie) {
		HttpHeaders getHeaders = new HttpHeaders();
		getHeaders.add(HttpHeaders.COOKIE, sessionCookie);
		return getHeaders;
	}

	private String getSessionCookie(RestTemplate restTemplate, User user) {
		HttpEntity<MultiValueMap<String, String>> request = buildLoginEntity(user);
		ResponseEntity<String> loginResponse = restTemplate.postForEntity(buildUrl(LOGIN_PATH), request, String.class);
		String sessionCookie = extractAndVerifySessionCookie(user, loginResponse);
		return sessionCookie;
	}

	private String extractAndVerifySessionCookie(User user, ResponseEntity<String> loginResponse) {
		String sessionCookie = "";
		List<String> setCookieValues = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
		if (!CollectionUtils.isEmpty(setCookieValues)) {
			sessionCookie = setCookieValues.get(setCookieValues.size() - 1);
		}

		boolean hasLoginFailed = loginResponse.hasBody() && Objects.requireNonNull(loginResponse.getBody()).contains("login_errors");

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
