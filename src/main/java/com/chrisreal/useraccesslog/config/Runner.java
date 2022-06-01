package com.chrisreal.useraccesslog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.chrisreal.useraccesslog.service.UserAccessLogService;

@Component
public class Runner implements CommandLineRunner {
	@Autowired
	private UserAccessLogService userAccessLogService;

	@Override
	public void run(String... args) throws Exception {
		userAccessLogService.execute(args);
		
	}

}
