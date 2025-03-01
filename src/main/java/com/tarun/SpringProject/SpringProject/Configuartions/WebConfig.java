package com.tarun.SpringProject.SpringProject.Configuartions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://51.20.133.206", "http://ec2-51-20-133-206.eu-north-1.compute.amazonaws.com", "http://hidenreveal.duckdns.org", "https://staging.d20c4e477w3261.amplifyapp.com/","http://51.20.133.206:3000", "https://hidenreveal.d1wjr4i2lskwik.amplifyapp.com/", "https://iamscrappy.d29ce4pv6q9fao.amplifyapp.com/")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}