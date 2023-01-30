package com.example.pdfbotspringboot.component;

import com.example.pdfbotspringboot.config.BotConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Sender extends DefaultAbsSender {

    private final BotConfig botConfig;

    protected Sender(DefaultBotOptions options, BotConfig botConfig) {
        super(options);
        this.botConfig = botConfig;
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    public boolean sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }

    @SneakyThrows
    public boolean sendPhoto(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }
}
