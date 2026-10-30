
package com.serviceplus.formDesigner.config;

import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.serviceplus.formDesigner", mongoTemplateRef = "configureMongoTemplate", excludeFilters = {
		@org.springframework.context.annotation.ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.serviceplus\\.formDesigner\\.production\\.repository\\..*"),
		@org.springframework.context.annotation.ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.serviceplus\\.formDesigner\\.returnToEdit\\.repository\\..*") })
public class ConfigureDbConfiguration {

	@Value("${app.mongodb.configure.uri}")
	private String uri;

	@Value("${app.mongodb.configure.database}")
	private String database;

	@Primary
	@Bean(name = "configureMongoDbFactory")
	public MongoDatabaseFactory configureMongoDbFactory() {
		return new SimpleMongoClientDatabaseFactory(MongoClients.create(uri), database);
	}

	@Primary
	@Bean(name = "configureMongoTemplate")
	public MongoTemplate configureMongoTemplate() {
		return new MongoTemplate(configureMongoDbFactory());
	}
}
