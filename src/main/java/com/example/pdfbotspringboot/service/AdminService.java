package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.component.Sender;
import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final KeyboardService keyboardService;
    private final UserRepository userRepository;
    private final Sender sender;
    private final UserService userService;
    public Pair<String, File> declareMessage = null;
    private Thread thread;
    private boolean completed = false;

    @PostConstruct
    public void init() {
        thread = new Thread(() -> {
            List<User> users = userRepository.findAll();
            if(declareMessage!=null) {
                if (declareMessage.getSecond().canRead()) {
                    send(users, new SendPhoto(), declareMessage.getFirst());
                } else {
                    send(users, new SendMessage(), declareMessage.getFirst());
                }
            }
        });
    }

    public void adminPanel(String text, SendMessage sendMessage) {
        switch (text) {
            case "/start" -> {
                sendMessage.setText("Tanlang");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getChoosePanelKeyboard();
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case "Admin panel" -> {
                admins.put(Long.valueOf(sendMessage.getChatId()),"ADMIN");
                sendMessage.setText("Assalomu alaykum.\nAdmin panelga hush kelibsiz");
                sendMessage.setReplyMarkup(keyboardService.getAdminKeyboard());
            }
            case "User panel"-> {
                admins.put(Long.valueOf(sendMessage.getChatId()), "USER");
                userService.userPanel("/start",sendMessage);
                return;
            }
            case "Foydalanuvchilarga habar jo`natish" -> {
                sendMessage.setText("Jo`natmoqchi bo`lgan habarni kiriting");
            }
            case "Foydalanuvchilar soni" -> {
                sendMessage.setText("Barcha foydalanuvchilar\uD83D\uDC65: " + userRepository.count() + "\n<b>Active\uD83D\uDC64:</b> " + userRepository.countAllByActive(true) + "\n<b>Blocked\uD83D\uDEAB:</b> " + userRepository.countAllByActive(false));
            }
            case "Statistika" -> {
                sendMessage.setText("<b>Bugun kun bo`yicha</b>\nPDF\uD83D\uDCD5: " + countPdf + "\nZip\uD83D\uDCDA: " + countZips + "\nYangi obunachilar\uD83D\uDC64: " + countUsers);
            }
            default -> {
                declareMessage = Pair.of(text, new File("gdfgsdfsd"));
                InlineKeyboardMarkup inlineKeyboard = keyboardService.getYesOrNoKeyboard();
                sender.sendMessage(new SendMessage(sendMessage.getChatId(), text));
                sendMessage.setText("Haqiqatdan ham habarni jo`natmoqchimisiz siz ning habar yuqoridagi dek");
                sendMessage.setReplyMarkup(inlineKeyboard);
            }
        }
        sender.sendMessage(sendMessage);
    }

    public void adminPanel(CallbackQuery callbackQuery, SendMessage sendMessage) {
        switch (callbackQuery.getData()) {
            case "Ha✅" -> {
                if (thread.isAlive()) {
                    if (completed) {
                        thread.interrupt();
                        thread.start();
                        sendMessage.setText("Habar jo`natish jarayoni boshlandi");
                    } else {
                        sendMessage.setText("Hozirda foydalanuvchilarga habar jo`natish jarayoni ketyapti\n" +
                                "Tez orada habar jo`natilish jarayoni boshlanadi");
                    }
                } else {
                    thread.interrupt();
                    thread.start();
                    completed = false;
                    sendMessage.setText("Habar jo`natish jarayoni boshlandi");
                }
            }
            case "Yo`q❌" -> {
                sendMessage.setText("Bekor qilindi");
            }
        }
        sender.sendMessage(sendMessage);
    }

    @SneakyThrows
    public void adminPanel(List<PhotoSize> photo, SendMessage sendMessage, String caption) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        PhotoSize photoSize = photo.get(photo.size() - 1);
        GetFile getFile = new GetFile(photoSize.getFileId());
        org.telegram.telegrambots.meta.api.objects.File executedFile = sender.execute(getFile);
        File file = downloadPhotos(executedFile.getFilePath(), "declare.jpg");
        declareMessage = Pair.of(caption, file);
        InlineKeyboardMarkup inlineKeyboard = keyboardService.getYesOrNoKeyboard();
        SendPhoto sendPhoto = new SendPhoto(chatId.toString(), new InputFile(file));
        sendPhoto.setCaption(caption);
        sender.sendPhoto(sendPhoto);
        sendMessage.setText("Haqiqatdan ham habarni jo`natmoqchimisiz siz ning habar yuqoridagi dek");
        sendMessage.setReplyMarkup(inlineKeyboard);
    }

    public void adminPanel(Document document, SendMessage sendMessage) {

    }

    @SneakyThrows
    private File downloadPhotos(String filePath, String fileName) {
        URL url = new URL(apiUrl + filePath);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        Path path = Paths.get(imageFolder + "/" + fileName);
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        return path.toFile();
    }

    private void send(List<User> users, SendMessage declareSender, String message){
        int success = 0, fail = 0;
        for (User user : users) {
            declareSender.setChatId(user.getUserId().toString());
            declareSender.setText(message);
            if (sender.sendMessage(declareSender)) {
                success++;
                user.setActive(true);
            } else {
                fail++;
                user.setActive(false);
            }
            userRepository.save(user);
            int count = success + fail;
            if (count % 100 == 0 || count == users.size()) {
                declareSender.setText(count + " ta habar jo`natildi\nSuccess✅: " + success + "\nFail❌: " + fail);
                declareSender.setChatId("1324394249");
                sender.sendMessage(declareSender);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Too many request exception");
            }
        }
        completed = true;
    }
    private void send(List<User> users, SendPhoto sendPhoto, String caption){
        int success = 0, fail = 0;
        for (User user : users) {
            sendPhoto.setChatId(user.getUserId().toString());
            sendPhoto.setCaption(caption);
            if (sender.sendPhoto(sendPhoto)) {
                success++;
                user.setActive(true);
            } else {
                fail++;
                user.setActive(false);
            }
            userRepository.save(user);
            int count = success + fail;
            if (count % 100 == 0 || count == users.size()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(count + " ta habar jo`natildi\nSuccess✅: " + success + "\nFail❌: " + fail);
                sendMessage.setChatId("1324394249");
                sender.sendMessage(sendMessage);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Too many request exception");
            }
        }
        completed = true;
    }
}