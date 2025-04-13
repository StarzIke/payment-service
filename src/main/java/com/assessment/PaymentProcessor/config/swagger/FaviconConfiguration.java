package com.assessment.PaymentProcessor.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FaviconConfiguration {

    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);

        // Create a map for both favicon.ico and favicon.png
        Map<String, ResourceHttpRequestHandler> urlMap = new HashMap<>();
        urlMap.put("/favicon.ico", faviconRequestHandler());
        urlMap.put("/favicon.png", faviconRequestHandler());

        mapping.setUrlMap(urlMap); // Set the URL mapping

        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        // Set the locations to look for favicons inside the public folder
        requestHandler.setLocations(Collections.singletonList(new ClassPathResource("public/")));
        return requestHandler;
    }
}
