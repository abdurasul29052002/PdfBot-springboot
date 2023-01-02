package com.example.pdfbotspringboot.controller;

import com.example.pdfbotspringboot.config.BotConfig;
import com.example.pdfbotspringboot.service.AdminService;
import com.example.pdfbotspringboot.service.MessageService;
import com.example.pdfbotspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UpdateController extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final AdminService adminService;
    private final UserService userService;
    private SendMessage sendMessage = new SendMessage();


    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            sendMessage.setChatId(chatId);
            if (message.hasText()) {
                if (chatId == 1324394249) {
                    sendMessage(adminService.adminPanel(message));
                } else {
                    sendMessage(userService.userPanel(message));
                }
            } else if (message.hasPhoto()) {
                sendMessage(userService.replyToPhotoMessage(message));
            } else if (message.hasDocument()) {

            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (chatId == 1324394249){

            }else {
                sendMessage(userService.userPanel(update.getCallbackQuery()));
            }
        } else if (update.getMessage().hasDocument()) {
            System.out.println("file bor");
            Document document = update.getMessage().getDocument();
            System.out.println(document.getFileId());
        }
    }

    private boolean sendMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }
}
