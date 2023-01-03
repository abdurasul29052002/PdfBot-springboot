package com.example.pdfbotspringboot;

import com.example.pdfbotspringboot.config.BotConfig;
import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.service.AdminService;
import com.example.pdfbotspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class PdfBotSpringbootApplication {

    public static Integer countUsers = 0;
    public static Integer countPdf = 0;
    public static final long dayInMilliseconds = 1000*60*60*24;

    public static void main(String[] args) {
        SpringApplication.run(PdfBotSpringbootApplication.class, args);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                countPdf = 0;
                countUsers = 0;
                System.out.println("Timer ishladi " + LocalDateTime.now());
            }
        },new Date(),dayInMilliseconds);
    }

    @Bean
    public DefaultBotOptions defaultBotOptions(){
        return new DefaultBotOptions();
    }
}
