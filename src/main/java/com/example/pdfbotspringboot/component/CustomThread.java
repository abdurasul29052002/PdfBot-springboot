package com.example.pdfbotspringboot.component;

import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.declareMessage;
import static com.example.pdfbotspringboot.service.AdminService.*;

@Component
@RequiredArgsConstructor
public class CustomThread extends Thread {

    private final UserRepository userRepository;
    private final Sender sender;

    @Override
    public void run() {
        List<User> users = userRepository.findAll();
        if(declareMessage!=null) {
            if (declareMessage.getSecond().canRead()) {
                send(users, new SendPhoto("", new InputFile(declareMessage.getSecond())), declareMessage.getFirst());
            } else {
                send(users, new SendMessage(), declareMessage.getFirst());
            }
        }
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
