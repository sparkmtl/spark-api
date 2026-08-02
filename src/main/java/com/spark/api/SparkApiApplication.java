package com.spark.api;

import com.spark.api.config.SparkProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SparkProperties.class)
public class SparkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SparkApiApplication.class, args);
	}

}
