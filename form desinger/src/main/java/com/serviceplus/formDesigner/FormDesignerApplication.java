package com.serviceplus.formDesigner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = { "com.serviceplus.formDesigner", "com.serviceplus.formDesigner.controller" })
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableFeignClients
public class FormDesignerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FormDesignerApplication.class, args);
	}
	@Override
	   protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	      return application.sources(FormDesignerApplication.class);
	   }

}
