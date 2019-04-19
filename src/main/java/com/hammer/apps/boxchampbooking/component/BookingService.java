package com.hammer.apps.boxchampbooking.component;

import com.hammer.apps.boxchampbooking.Application;
import com.hammer.apps.boxchampbooking.model.Booking;
import com.hammer.apps.boxchampbooking.model.ClassType;
import com.hammer.apps.boxchampbooking.util.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BookingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private static final String CLASS_LIST_ATHLETE_VIEW_REGEX = ".*?classlist/athlete/view/(.*?)\"";
	private static final String CLASS_LIST_HOME_PROGRESS_REGEX = ".*?classlist/home/progress_tooltip/(.*?)\"";

	private static final String CLASS_LIST_PATH = "classlist?date=";
	private static final String CLASS_BOOKING_PATH = "classlist/athlete/book/";

	private final AuthenticationService authenticationService;
	private final BookingRepository bookingRepository;


	public BookingService(AuthenticationService authenticationService, BookingRepository bookingRepository) {
		this.authenticationService = authenticationService;
		this.bookingRepository = bookingRepository;
	}

	public ResponseEntity<String> authenticateAndBookClass(RestTemplate restTemplate, Booking booking) {

		HttpEntity authorizedEntity = authenticationService.authenticate(restTemplate, booking.getUser());
		return bookClass(restTemplate, booking, authorizedEntity);
	}

	public ResponseEntity<String> bookClass(RestTemplate restTemplate, Booking booking, HttpEntity authorizedEntity) {
		String classId = getClassId(restTemplate, authorizedEntity, booking.getClassType());

		String bookingUrl = AppUtils.buildUrl(CLASS_BOOKING_PATH + classId);

		return restTemplate.exchange(bookingUrl, HttpMethod.GET, authorizedEntity, String.class);
	}


	public String getClassId(RestTemplate restTemplate, HttpEntity authorizedEntity, ClassType type) {
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
		return AppUtils.buildUrl(CLASS_LIST_PATH + formattedDate);
	}


	public Iterable<Booking> getBookings() {
//		return bookingRepository.findAllByUserUsername("user");
		return bookingRepository.findAll();
	}
}
