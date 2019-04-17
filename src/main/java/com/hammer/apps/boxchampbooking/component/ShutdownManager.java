package com.hammer.apps.boxchampbooking.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ShutdownManager{

	@Autowired
	private ApplicationContext appContext;

	public void initiateShutdown(int returnCode){
		SpringApplication.exit(appContext, () -> returnCode);
	}
}
