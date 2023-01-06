package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.PdfBotSpringbootApplication;
import com.example.pdfbotspringboot.component.Sender;
import com.example.pdfbotspringboot.controller.UpdateController;
import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final KeyboardService keyboardService;
    private final UserRepository userRepository;
    private final Sender sender;
    public String declareMessage;

    public void adminPanel(String text, SendMessage sendMessage) {
        switch (text) {
            case "/start" -> {
                sendMessage.setText("Assalomu alaykum.\nAdmin panelga hush kelibsiz");
                ReplyKeyboardMarkup replyKeyboard = keyboardService.getReplyKeyboard(
                        2,
                        "Foydalanuvchilarga habar jo`natish",
                        "Foydalanuvchilar soni",
                        "Statistika"
                );
                sendMessage.setReplyMarkup(replyKeyboard);
            }
            case "Foydalanuvchilarga habar jo`natish" -> {
                sendMessage.setText("Jo`natmoqchi bo`lgan habarni kiriting");
            }
            case "Foydalanuvchilar soni" -> {
                sendMessage.setText("Barcha foydalanuvchilar: "+userRepository.count()+"\n<b>Active:</b> "+userRepository.countAllByActive(true)+"\n<b>Blocked:</b> "+userRepository.countAllByActive(false));
            }
            case "Statistika" -> {
                sendMessage.setText("<b>Bugun kun bo`yicha</b>\nPDF\uD83D\uDCD5: "+ countPdf + "\nYangi obunachilar: " + countUsers);
            }
            default -> {
                declareMessage = text;
                getThread().start();
                sendMessage.setText("Habaringiz barcha foydalanuvchilarga tez orada jo`natiladi");
            }
        }
        sender.sendMessage(sendMessage);
    }

    public Thread getThread() {
        return new Thread(() -> {
            List<User> users = userRepository.findAll();
            SendMessage declareSender = new SendMessage();
            int success = 0, fail = 0;
            for (User user : users) {
                declareSender.setChatId(user.getUserId().toString());
                declareSender.setText(declareMessage);
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
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("Too many request exception");
                }
            }
        });
    }
}