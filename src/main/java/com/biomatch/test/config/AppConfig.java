package com.biomatch.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class AppConfig {
	@Bean
	@Profile("local")
	public PropertySourcesPlaceholderConfigurer fetchLocalProperties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

		ClassPathResource resource = new ClassPathResource("application-local.yaml");

		configurer.setLocation(resource);
		return configurer;
	}
	
	@Bean
	@Profile("inMemDB")
	public PropertySourcesPlaceholderConfigurer fetchEmbeddedDBProperties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

		ClassPathResource resource = new ClassPathResource("application-in-mem-db.yaml");

		configurer.setLocation(resource);
		return configurer;
	}

	@Bean
	@Profile("dev")
	public PropertySourcesPlaceholderConfigurer fetchDevProperties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

		ClassPathResource resource = new ClassPathResource("application-dev.yaml");

		configurer.setLocation(resource);
		return configurer;
	}

	@Bean
	@Profile("qa")
	public PropertySourcesPlaceholderConfigurer fetchQAProperties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

		ClassPathResource resource = new ClassPathResource("application-qa.yaml");

		configurer.setLocation(resource);
		return configurer;
	}

	@Bean
	@Profile("prod")
	public PropertySourcesPlaceholderConfigurer fetchProductionProperties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();

		ClassPathResource resource = new ClassPathResource("application-prod.yaml");

		configurer.setLocation(resource);
		return configurer;
	}

}
