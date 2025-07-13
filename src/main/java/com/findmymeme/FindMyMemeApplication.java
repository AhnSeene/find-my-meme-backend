package com.findmymeme;

import com.findmymeme.config.AwsLambdaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(AwsLambdaProperties.class)
@EnableScheduling
public class FindMyMemeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindMyMemeApplication.class, args);
	}

}
