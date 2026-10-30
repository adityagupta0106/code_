package com.serviceplus.formDesigner.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.serviceplus.formDesigner.production.repository",
        mongoTemplateRef = "productionMongoTemplate"
)
public class ProductionDbConfiguration {

    @Value("${app.mongodb.production.uri}")
    private String uri;

    @Value("${app.mongodb.production.database}")
    private String database;

    @Bean(name = "productionMongoDbFactory")
    public MongoDatabaseFactory productionMongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(uri), database);
    }

    @Bean(name = "productionMongoTemplate")
    public MongoTemplate productionMongoTemplate() {
        return new MongoTemplate(productionMongoDbFactory());
    }
}