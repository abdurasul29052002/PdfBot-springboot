package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.controller.UpdateController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Arrays;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final KeyboardService keyboardService;

    public SendMessage adminPanel(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage(chatId.toString(), "");
        switch (text) {
            case "Foydalanuvchilarga habar jo`natish" -> {
                sendMessage.setText("Jo`natmoqchi bo`lgan habarni kiriting");
            }
            case "Foydalanuvchilar soni" -> {

            }
            default -> {
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getReplyKeyboard(
                        2,
                        "Foydalanuvchilarga habar jo`natish",
                        "Foydalanuvchilar soni"
                );
                sendMessage.setText("Assalomu alaykum.\nAdmin panelga hush kelibsiz");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
        }
        return sendMessage;
    }
}