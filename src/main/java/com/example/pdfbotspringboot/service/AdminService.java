package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.component.Sender;
import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final KeyboardService keyboardService;
    private final UserRepository userRepository;
    private final Sender sender;
    public Queue<String> declareMessage = new LinkedList<>();
    private Thread thread;
    private boolean completed = false;

    @PostConstruct
    public void init() {
        thread = new Thread(() -> {
            List<User> users = userRepository.findAll();
            SendMessage declareSender = new SendMessage();
            int success = 0, fail = 0;
            for (String s : declareMessage) {
                for (User user : users) {
                    declareSender.setChatId(user.getUserId().toString());
                    declareSender.setText(s);
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
            }
            completed = true;
        });
    }

    public void adminPanel(String text, SendMessage sendMessage) {
        switch (text) {
            case "/start" -> {
                sendMessage.setText("Assalomu alaykum.\nAdmin panelga hush kelibsiz");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getAdminKeyboard();
                sendMessage.setReplyMarkup(replyKeyboard);
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
                declareMessage.add(text);
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
                declareMessage.poll();
                sendMessage.setText("Bekor qilindi");
            }
        }
        sender.sendMessage(sendMessage);
    }
}