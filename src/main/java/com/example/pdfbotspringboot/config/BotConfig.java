package com.example.pdfbotspringboot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.yml")
public class BotConfig {

    @Value("${token}")
    private String token;

    @Value("${name}")
    private String name;
}
