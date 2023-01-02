package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.enums.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final KeyboardService keyboardService;

    public SendMessage getGreetingMessage(SendMessage sendMessage, String firstName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Salom <b>"+firstName+"</b> Til tanlang \uD83C\uDDFA\uD83C\uDDFF").append(System.lineSeparator()).append("Привет <b>"+firstName+"</b> Выберите свой язык \uD83C\uDDF7\uD83C\uDDFA")
                .append(System.lineSeparator()).append("Helllo <b>"+firstName+"</b> Choose your language \uD83C\uDDFA\uD83C\uDDF8");
        sendMessage.setReplyMarkup(keyboardService.getInlineKeyboard(
                3,
                Language.ENGLISH.name(),
                Language.UZBEK.name(),
                Language.RUS.name()
        ));
        sendMessage.setText(stringBuilder.toString());
        return sendMessage;
    }

    public SendMessage getGreetingMessage(SendMessage sendMessage, User user){
        Language language = user.getLanguageUser();
        switch (language){
            case ENGLISH -> {
                sendMessage.setText("<b>Hi</b> , This bot helps you for media processing that can work with pdf documents\n\n" +
                        "You can get more information with /help\n\nYou can easily make a pdf with this bot so press button <b>PDF Generator</b>");
            }
            case UZBEK ->{
                sendMessage.setText("<b>Salom</b> , bu bot sizga bir nechta amallarni bajarishda yordam beradi\n\n/help buyrug'ini bosish orqali qo'shimcha ma'lumot olishingiz mumkin\n\n" +
                        "<b>PDF yaratish</b> tugmasi orqali siz rasmlarni osongina pdf file ko'rinishiga keltira olasiz");
            }
            case RUS -> {
                sendMessage.setText("<b>Привет</b>, этот бот поможет тебе в нескольких шагах " +
                        "Вы можете узнать больше через /help\n\nОтправьте нужные изображения, нажав кнопку Генератор PDF.");
            }
        }
        sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(
                1,"PDF yaratish \uD83D\uDCD5"
        ));
        return sendMessage;
    }

    public SendMessage getReplyToPhotoMessage(SendMessage sendMessage, Language language, Integer messageId){
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("Photo is received");
            }
            case RUS -> {
                sendMessage.setText("Изображения получено");
            }
            case UZBEK -> {
                sendMessage.setText("Rasm qabul qilindi");
            }
        }
        sendMessage.setReplyToMessageId(messageId);
        return sendMessage;
    }
}
