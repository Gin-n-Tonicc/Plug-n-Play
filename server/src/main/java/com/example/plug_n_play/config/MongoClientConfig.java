package com.example.plug_n_play.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoClientConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Value("${spring.data.openai.api-key}")
    private String openAIApiKey;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoClientSettings mongoClientSettings(){
        ConnectionString connectionString = new ConnectionString(mongoURI);
        return MongoClientSettings.builder().applyConnectionString(connectionString).build();
    }

    @Bean
    public EmbeddingModel embeddingModel(){
        return new OpenAiEmbeddingModel(new OpenAiApi(openAIApiKey));
    }
}
