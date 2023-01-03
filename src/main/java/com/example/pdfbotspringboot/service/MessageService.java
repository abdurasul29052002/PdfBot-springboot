package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.enums.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final KeyboardService keyboardService;

    public void getGreetingMessage(SendMessage sendMessage, String firstName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Salom <b>"+firstName+"</b> Til tanlang \uD83C\uDDFA\uD83C\uDDFF").append(System.lineSeparator()).append("Привет <b>"+firstName+"</b> Выберите свой язык \uD83C\uDDF7\uD83C\uDDFA")
                .append(System.lineSeparator()).append("Helllo <b>"+firstName+"</b> Choose your language \uD83C\uDDFA\uD83C\uDDF8");
        sendMessage.setReplyMarkup(keyboardService.getInlineKeyboard(
                3,
                Language.ENGLISH.name(),
                Language.RUS.name(),
                Language.UZBEK.name()
        ));
        sendMessage.setText(stringBuilder.toString());
    }

    public void getGreetingMessage(SendMessage sendMessage, User user){
        Language language = user.getLanguageUser();
        switch (language){
            case ENGLISH -> {
                sendMessage.setText("<b>Hi</b> , This bot helps you for media processing that can work with pdf documents\n\n" +
                        "You can get more information with /help\n\nYou can easily make a pdf with this bot so press button <b>PDF Generator</b>");
                sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(
                        1,"Generate PDF \uD83D\uDCD5"
                ));
            }
            case RUS -> {
                sendMessage.setText("<b>Привет</b>, этот бот поможет тебе в нескольких шагах " +
                        "Вы можете узнать больше через /help\n\nОтправьте нужные изображения, нажав кнопку Генератор PDF.");
                sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(
                        1,"Генерировать PDF \uD83D\uDCD5"
                ));
            }
            case UZBEK ->{
                sendMessage.setText("<b>Salom</b> , bu bot sizga bir nechta amallarni bajarishda yordam beradi\n\n/help buyrug'ini bosish orqali qo'shimcha ma'lumot olishingiz mumkin\n\n" +
                        "<b>PDF yaratish</b> tugmasi orqali siz rasmlarni osongina pdf file ko'rinishiga keltira olasiz");
                sendMessage.setReplyMarkup(keyboardService.getReplyKeyboard(
                        1,"PDF yaratish \uD83D\uDCD5"
                ));
            }
        }

    }

    public void getReplyToPhotoMessage(SendMessage sendMessage, Language language, Integer messageId){
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("Photo is received");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getReplyKeyboard(1, "Generate\uD83D\uDCD5");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case RUS -> {
                sendMessage.setText("Изображения получено");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getReplyKeyboard(1, "Генерировать\uD83D\uDCD5");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case UZBEK -> {
                sendMessage.setText("Rasm qabul qilindi");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getReplyKeyboard(1, "Yaratish\uD83D\uDCD5");
                sendMessage.setReplyMarkup(replyKeyboard);
            }
        }
        sendMessage.setReplyToMessageId(messageId);
    }

    public void getAskPhotoMessage(SendMessage sendMessage, Language language){
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("Please send photos for generating a PDF");
            }
            case RUS -> {
                sendMessage.setText("Пожалюста отправтье картинки для PDF");
            }
            case UZBEK -> {
                sendMessage.setText("Iltimos PDF generatsiya qilish uchun rasm yuboring");
            }
        }
    }

    public void getReadyPdfMessage(SendDocument sendDocument, Language language){
        switch (language) {
            case ENGLISH -> {
                sendDocument.setCaption("Your PDF file is ready\nShare our bot to your friends");
            }
            case RUS -> {
                sendDocument.setCaption("Твой PDF файл готов\nПоделис наш бот с друзями");
            }
            case UZBEK -> {
                sendDocument.setCaption("PDF faylingiz tayyor\nBotimizni do`stlaringizga ham ulashing");
            }
        }
    }
}
