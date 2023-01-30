package com.example.pdfbotspringboot.controller;

import com.example.pdfbotspringboot.component.Sender;
import com.example.pdfbotspringboot.config.BotConfig;
import com.example.pdfbotspringboot.service.AdminService;
import com.example.pdfbotspringboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.admins;

@Component
@RequiredArgsConstructor
public class UpdateController extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final AdminService adminService;
    private final UserService userService;
    private final Sender sender;


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
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            sendMessage.setChatId(chatId);
            UserService.currentUser = message.getFrom();
            if (message.hasText()) {
                if (admins.containsKey(chatId)) {
                    if (message.getText().equals("/start")){
                        admins.put(chatId, "ADMIN");
                    }
                    if (admins.get(chatId).equals("ADMIN")) {
                        adminService.adminPanel(message.getText(), sendMessage);
                    } else {
                        userService.userPanel(message.getText(), sendMessage);
                    }
                } else {
                    userService.userPanel(message.getText(), sendMessage);
                }
            } else if (message.hasPhoto()) {
                if (admins.containsKey(chatId)) {
                    if (admins.get(chatId).equals("ADMIN")) {
                        adminService.adminPanel(message.getPhoto(), sendMessage, message.getCaption());
                    } else {
                        userService.userPanel(message.getPhoto(), sendMessage, message.getMessageId());
                    }
                } else {
                    userService.userPanel(message.getPhoto(), sendMessage, message.getMessageId());
                }
            } else if (message.hasDocument()) {
                if (admins.containsKey(chatId)){
                    if (admins.get(chatId).equals("ADMIN")){
                        adminService.adminPanel(message.getDocument(), sendMessage);
                    }else {
                        userService.userPanel(message.getDocument(), sendMessage, message.getMessageId());
                    }
                }else {
                    userService.userPanel(message.getDocument(), sendMessage, message.getMessageId());
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            sendMessage.setChatId(chatId);
            if (admins.containsKey(chatId)) {
                if (admins.get(chatId).equals("ADMIN")) {
                    adminService.adminPanel(update.getCallbackQuery(), sendMessage);
                } else {
                    userService.userPanel(update.getCallbackQuery(), sendMessage);
                }
            } else {
                userService.userPanel(update.getCallbackQuery(), sendMessage);
            }
        }
    }


}