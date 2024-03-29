package com.example.pdfbotspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.util.Pair;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class PdfBotSpringbootApplication {

    public static Integer countUsers = 0;
    public static Integer countPdf = 0;
    public static Integer countZips = 0;
    public static final long dayInMilliseconds = 1000 * 60 * 60 * 24;
    public static final String imageFolder = "/home/ubuntu/assets/images";
    public static final String pdfFolder = "/home/ubuntu/assets/pdf";
    public static final String compressFolder = "/home/ubuntu/assets/compress";
    public static Pair<String, File> declareMessage = null;
    public static final StringBuilder apiUrl = new StringBuilder("https://api.telegram.org/file/bot");
    public static Map<Long, String> admins = new HashMap<>(
            Map.of(1324394249L, "ADMIN",
                    968877318L, "ADMIN")
    );

    public static void main(String[] args) {
        SpringApplication.run(PdfBotSpringbootApplication.class, args);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                countPdf = 0;
                countUsers = 0;
                countZips = 0;
                System.out.println("Timer ishladi " + LocalDateTime.now());
            }
        }, Date.valueOf(tomorrow), dayInMilliseconds);

    }

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        return new DefaultBotOptions();
    }
}
