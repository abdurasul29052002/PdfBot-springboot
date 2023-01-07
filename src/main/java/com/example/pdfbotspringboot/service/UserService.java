package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.PdfBotSpringbootApplication;
import com.example.pdfbotspringboot.component.Sender;
import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.enums.BotState;
import com.example.pdfbotspringboot.enums.Language;
import com.example.pdfbotspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MessageService messageService;
    private final PDFService pdfService;
    private final Sender sender;
    private final Map<Long, Integer> userPhotosCount = new HashMap<>();
    public static org.telegram.telegrambots.meta.api.objects.User currentUser;

    @SneakyThrows
    public void userPanel(String text, SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        if (userRepository.existsByUserId(chatId)) {
            User user = userRepository.findByUserId(chatId).orElseThrow();
            switch (text) {
                case "PDF yaratish \uD83D\uDCD5", "Generate PDF \uD83D\uDCD5", "Генерировать PDF \uD83D\uDCD5" -> {
                    messageService.getAskPhotoMessage(sendMessage, user.getLanguageUser());
                    userPhotosCount.put(chatId, 0);
                    user.setBotState(BotState.GET_PHOTO);
                    userRepository.save(user);
                }
                case "Generate\uD83D\uDCD5", "Yaratish\uD83D\uDCD5", "Генерировать\uD83D\uDCD5" -> {
                    SendMessage stickerMessage = new SendMessage(chatId.toString(), "\uD83D\uDCDD");
                    sender.execute(stickerMessage);
                    SendChatAction sendChatAction = new SendChatAction();
                    sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
                    sendChatAction.setChatId(chatId);
                    sender.execute(sendChatAction);
                    pdfService.generate(chatId, userPhotosCount.get(chatId));
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(chatId);
                    InputFile inputFile = pdfService.getPdfFile(chatId);
                    sendDocument.setDocument(inputFile);
                    messageService.getReadyPdfMessage(sendDocument, user.getLanguageUser());
                    sender.execute(sendDocument);
                    pdfService.deleteFiles(chatId, userPhotosCount.get(chatId));
                    userPhotosCount.remove(chatId);
                    System.out.println(chatId + ": Successfully generated a pdf file");
                    countPdf++;
                    user.setBotState(BotState.START);
                    userRepository.save(user);
                    return;
                }
                default -> {
                    messageService.getGreetingMessage(sendMessage, user);
                }
            }
        } else {
            User user = new User(
                    null,
                    currentUser.getUserName(),
                    chatId,
                    BotState.GET_LANG,
                    null,
                    true
            );
            userRepository.save(user);
            countUsers++;
            messageService.getGreetingMessage(sendMessage, currentUser.getFirstName());
        }
        sender.sendMessage(sendMessage);
    }

    public void userPanel(CallbackQuery callbackQuery, SendMessage sendMessage) {
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
        user.setBotState(BotState.START);
        messageService.getGreetingMessage(sendMessage, user);
        userRepository.save(user);
        sender.sendMessage(sendMessage);
    }

    @SneakyThrows
    public void userPanel(List<PhotoSize> photos, SendMessage sendMessage, Integer messageId) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        User user = userRepository.findByUserId(chatId).orElseThrow();
        if (user.getBotState().equals(BotState.GET_PHOTO)){
            GetFile getFile = new GetFile(photos.get(photos.size()-1).getFileId());
            File executedFile = sender.execute(getFile);
            pdfService.downloadPhotos(chatId, executedFile.getFilePath(), userPhotosCount.get(chatId));
            userPhotosCount.put(chatId,userPhotosCount.get(chatId)+1);
            messageService.getReplyToPhotoMessage(sendMessage, user.getLanguageUser(), messageId);
            sender.sendMessage(sendMessage);
        }else {
            messageService.getErrorStateMessage(sendMessage,user.getLanguageUser());
        }
    }

    @SneakyThrows
    public void userPanel(Document document, SendMessage sendMessage, Integer messageId) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        User user = userRepository.findByUserId(chatId).orElseThrow();
        System.out.println(document.getFileName());
        if (document.getFileName().endsWith(".jpg") || document.getFileName().endsWith(".png")) {
            GetFile getFile = new GetFile(document.getFileId());
            File executedFile = sender.execute(getFile);
            pdfService.downloadPhotos(chatId, executedFile.getFilePath(), userPhotosCount.get(chatId));
            userPhotosCount.put(chatId, userPhotosCount.get(chatId)+1);
            messageService.getReplyToPhotoMessage(sendMessage, user.getLanguageUser(), messageId);
            sender.sendMessage(sendMessage);
        }else {
            messageService.getErrorFileTypeMessage(sendMessage, user.getLanguageUser());
        }
    }
}
