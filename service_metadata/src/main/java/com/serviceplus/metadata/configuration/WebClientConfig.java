package com.serviceplus.metadata.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfig {

	@Bean(name = "restTemplateBuilder01")
    @LoadBalanced
    @Primary
    RestTemplate restTemplateBuilder() {
    	return new RestTemplate();
    }
}
