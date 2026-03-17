package com.social.csocial.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${csocial.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String location = dirPath.toUri().toString();
        registry.addResourceHandler("/uploads/**", "/api/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);

    }
}
