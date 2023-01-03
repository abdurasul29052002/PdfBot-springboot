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
    private final Map<Long, List<List<PhotoSize>>> photoMap = new HashMap<>();
    private final Map<Long, List<Document>> documentMap = new HashMap<>();
    public static org.telegram.telegrambots.meta.api.objects.User currentUser;

    @SneakyThrows
    public void userPanel(String text, SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        if (userRepository.existsByUserId(chatId)) {
            User user = userRepository.findByUserId(chatId).orElseThrow();
            switch (text) {
                case "PDF yaratish \uD83D\uDCD5", "Generate PDF \uD83D\uDCD5", "Генерировать PDF \uD83D\uDCD5" -> {
                    messageService.getAskPhotoMessage(sendMessage, user.getLanguageUser());
                }
                case "Generate\uD83D\uDCD5", "Yaratish\uD83D\uDCD5", "Генерировать\uD83D\uDCD5" -> {
                    SendMessage stickerMessage = new SendMessage(chatId.toString(), "\uD83D\uDCDD");
                    sender.execute(stickerMessage);
                    SendChatAction sendChatAction = new SendChatAction();
                    sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
                    sendChatAction.setChatId(chatId);
                    sender.execute(sendChatAction);
                    List<String> filePaths = getFilePaths(chatId);
                    pdfService.generate(filePaths, chatId);
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(chatId);
                    InputFile inputFile = pdfService.getPdfFile(chatId);
                    sendDocument.setDocument(inputFile);
                    messageService.getReadyPdfMessage(sendDocument,user.getLanguageUser());
                    sender.execute(sendDocument);
                    pdfService.deleteFiles(chatId, photoMap.get(chatId).size());
                    photoMap.remove(chatId); documentMap.remove(chatId);
                    System.out.println(chatId+": Successfully generated a pdf file");
                    countPdf++;
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
                    BotState.GETLANG,
                    null
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
        user.setBotState(BotState.GETPHOTO);
        messageService.getGreetingMessage(sendMessage, user);
        userRepository.save(user);
        sender.sendMessage(sendMessage);
    }

    public void userPanel(List<PhotoSize> photo, SendMessage sendMessage, Integer messageId) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        User user = userRepository.findByUserId(chatId).orElseThrow();
        if (!photoMap.containsKey(chatId)) {
            photoMap.put(chatId, new ArrayList<>());
        }
        photoMap.get(chatId).add(photo);
        messageService.getReplyToPhotoMessage(sendMessage, user.getLanguageUser(), messageId);
        sender.sendMessage(sendMessage);
    }

    @SneakyThrows
    public List<String> getFilePaths(Long chatId) {
        List<List<PhotoSize>> lists = photoMap.get(chatId);
        List<String> filePaths = new ArrayList<>();
        for (List<PhotoSize> list : lists) {
            GetFile getFile = new GetFile(list.get(list.size() - 1).getFileId());
            File executedFile = sender.execute(getFile);
            filePaths.add(executedFile.getFilePath());
        }
        return filePaths;
    }
}
