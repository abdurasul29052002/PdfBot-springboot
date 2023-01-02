package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.config.BotConfig;
import com.example.pdfbotspringboot.controller.UpdateController;
import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.enums.BotState;
import com.example.pdfbotspringboot.enums.Language;
import com.example.pdfbotspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final MessageService messageService;
    private final PDFService pdfService;
    @Lazy
    @Autowired
    UpdateController updateController;
    private Map<Long, List<List<PhotoSize>>> photoMap = new HashMap<>();
    private Map<Long, List<Document>> documentMap = new HashMap<>();
    private SendMessage sendMessage = new SendMessage();
    @SneakyThrows
    public SendMessage userPanel(Message message) {
        Long chatId = message.getChatId();
        sendMessage.setChatId(chatId);
        if (userRepository.existsByUserId(chatId)) {
            User user = userRepository.findByUserId(chatId).orElseThrow();
            if (message.hasText()) {
                String text = message.getText();
                switch (text) {
                    case "PDF yaratish \uD83D\uDCD5" -> {
                        sendMessage.setText("Iltimos PDF generatsiya qilish uchun rasm yuboring");
                    }
                    default -> {
                        sendMessage = messageService.getGreetingMessage(sendMessage, user);
                    }
                }
            }
        } else {
            User user = new User(
                    null,
                    message.getFrom().getUserName(),
                    chatId,
                    BotState.GETLANG,
                    null
            );
            userRepository.save(user);
            sendMessage = messageService.getGreetingMessage(sendMessage, message.getFrom().getFirstName());
        }
        return sendMessage;
    }

    public SendMessage userPanel(CallbackQuery callbackQuery) {
        String callBackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        sendMessage.setChatId(chatId);
        User user = userRepository.findByUserId(chatId).orElseThrow();
        switch (callBackData) {
            case "ENGLISH" -> {
                user.setLanguageUser(Language.ENGLISH);
            }
            case "UZBEK" -> {
                user.setLanguageUser(Language.UZBEK);
            }
            case "RUS" -> {
                user.setLanguageUser(Language.RUS);
            }
        }
        user.setBotState(BotState.GETPHOTO);
        sendMessage = messageService.getGreetingMessage(sendMessage, user);
        userRepository.save(user);
        return sendMessage;
    }

    public SendMessage replyToPhotoMessage(Message message){
        List<PhotoSize> photo = message.getPhoto();
        Long chatId = message.getChatId();
        sendMessage.setChatId(chatId.toString());
        User user = userRepository.findByUserId(chatId).orElseThrow();
        if (!photoMap.containsKey(chatId)){
            photoMap.put(chatId, new ArrayList<>());
        }
        photoMap.get(chatId).add(photo);
        sendMessage = messageService.getReplyToPhotoMessage(sendMessage, user.getLanguageUser(), message.getMessageId());
        System.out.println(photo.size());
        return sendMessage;
    }
}
