package com.tomato.naraclub.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
public class TwitterConfig {
    @Value("${x.api-key}")
    private String apiKey;

    @Value("${x.api-key-secret}")
    private String apiKeySecret;

    @Bean
    public TwitterFactory twitterFactory() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(apiKey)
                .setOAuthConsumerSecret(apiKeySecret);
        return new TwitterFactory(cb.build());
    }
}
