package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.enums.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
                "English",
                "Русский",
                "O`zbekcha"
        ));
        sendMessage.setText(stringBuilder.toString());
    }

    public void getGreetingMessage(SendMessage sendMessage, Language language){
        switch (language){
            case ENGLISH -> {
                sendMessage.setText("<b>Hi</b> , This bot helps you for media processing that can work with pdf documents\n\n" +
                        "You can get more information with /help\n\nYou can easily make a pdf with this bot so press button <b>PDF Generator</b>\n"+
                        "You can easily compress files with press <b>Compress images</b> button"
                );
            }
            case RUS -> {
                sendMessage.setText("<b>Привет</b>, этот бот поможет тебе в нескольких шагах " +
                        "Вы можете узнать больше через /help\n\nОтправьте нужные изображения, нажав кнопку Генератор PDF.\n" +
                        "Вы можете сжат файлы с кнопкой <b>Сжат файлы</b>"
                );
            }
            case UZBEK ->{
                sendMessage.setText("<b>Salom</b> , bu bot sizga bir nechta amallarni bajarishda yordam beradi\n\n/help buyrug'ini bosish orqali qo'shimcha ma'lumot olishingiz mumkin\n\n" +
                        "<b>PDF yaratish</b> tugmasi orqali siz rasmlarni osongina pdf file ko'rinishiga keltira olasiz\n"+
                        "<b>Fayllarni zip qilish</b> tugmasi orqali faylani osongina zip qilishingiz mumkin bo`ladi"
                );
            }
        }
        sendMessage.setReplyMarkup(keyboardService.getHomeKeyboard(language));
    }

    public void getReplyToPhotoMessage(SendMessage sendMessage, Language language, Integer messageId){
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
        sendMessage.setReplyMarkup(keyboardService.getPdfKeyboard(language));
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
        sendMessage.setReplyMarkup(null);
    }

    public void getReadyFileMessage(SendDocument sendDocument, Language language){
        switch (language) {
            case ENGLISH -> {
                sendDocument.setCaption("Your file is ready\nShare our bot to your friends");
            }
            case RUS -> {
                sendDocument.setCaption("Ваш файл готов\nПоделис наш бот с друзями");
            }
            case UZBEK -> {
                sendDocument.setCaption("Sizning faylingiz tayyor\nBotimizni do`stlaringizga ham ulashing");
            }
        }
        sendDocument.setReplyMarkup(keyboardService.getShareKeyboard(language));
    }

    public void getErrorFileTypeMessage(SendMessage sendMessage, Language language) {
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("These files are not a photo\nPlease send a photo file");
            }
            case RUS -> {
                sendMessage.setText("Эти рисунки не типа фото\nПожалюста отправте толко фотографии");
            }
            case UZBEK -> {
                sendMessage.setText("Bu fayllar rasm formatida emas\nIltimos faqat rasm jo`nating");
            }
        }
    }

    public void getErrorStateMessage(SendMessage sendMessage, Language language) {
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("I'm not ready for this files\nPlease use the blow buttons");
            }
            case RUS -> {
                sendMessage.setText("Я пока не готов для зтих файлы\nПожалюста исползуйте кнопки ниже");
            }
            case UZBEK -> {
                sendMessage.setText("Men hozircha bu fayllar uchun tayyor emasman\nPastdagi tugmalardan foydalaning");
            }
        }
    }

    public void getAskDocumentForCompress(SendMessage sendMessage, Language languageUser) {
        switch (languageUser) {
            case ENGLISH -> {
                sendMessage.setText("Ok. Send me files for compress.\n\nPlease send only file‼");
            }
            case RUS -> {
                sendMessage.setText("Ок. Отправте мне файлы для сжатие.\n\nПожалуйста отправьте только файлы‼");
            }
            case UZBEK -> {
                sendMessage.setText("Yaxshi. Zip qilish uchun fayllarni yuboring.\n\nIltimos faqat fayl jo`nating‼");
            }
        }
        sendMessage.setReplyMarkup(null);
    }

    public void getReplyToDocument(SendMessage sendMessage, Language language, Integer messageId) {
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("File is received");
            }
            case RUS -> {
                sendMessage.setText("Файл получено");
            }
            case UZBEK -> {
                sendMessage.setText("Fayl qabul qilindi");
            }
        }
        sendMessage.setReplyMarkup(keyboardService.getCompressKeyboard(language));
        sendMessage.setReplyToMessageId(messageId);
    }

    public void getMessageForDeclare(SendMessage sendMessage){

    }

    public void newReferralMessage(SendMessage sendMessage, User user, Language language) {
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("You are invited a new user " + user.getUserName());
            }
            case RUS -> {
                sendMessage.setText("Вы пригласили новый пользовател " + user.getUserName());
            }
            case UZBEK -> {
                sendMessage.setText("Siz yangi foydalanuvchi taklif qildingiz " + user.getUserName());
            }
        }
    }

    public void referralSystemMessage(SendMessage sendMessage, Language language) {
        switch (language) {
            case ENGLISH -> {
                sendMessage.setText("Welcome to referral system");
            }
            case RUS -> {
                sendMessage.setText("Добро пожаловать на рефералная система");
            }
            case UZBEK -> {
                sendMessage.setText("Referal tizimiga hush kelibsiz");
            }
        }
        sendMessage.setReplyMarkup(keyboardService.getReferralKeyboard(language));
    }
}
