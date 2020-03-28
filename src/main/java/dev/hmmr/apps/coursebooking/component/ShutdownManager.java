package dev.hmmr.apps.coursebooking.component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShutdownManager {
	ApplicationContext appContext;

	public void initiateShutdown(int returnCode) {
		SpringApplication.exit(appContext, () -> returnCode);
	}
}
