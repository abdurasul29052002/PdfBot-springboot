package com.example.pdfbotspringboot.component;

import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.admins;
import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.declareMessage;

@Component
@RequiredArgsConstructor
public class CustomThread extends Thread {

    private final UserRepository userRepository;
    private final Sender sender;

    @Override
    public void run() {
        int pageSize = 50; // Example: Batch size of 50 users per page
        int pageNumber = 0;
        boolean morePages = true;

        while (morePages) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<User> users = userRepository.findAll(pageable).getContent();

            if (users.isEmpty()) {
                morePages = false; // No more users to fetch
            } else {
                if (declareMessage != null) {
                    if (declareMessage.getSecond().canRead()) {
                        send(users, new SendPhoto("", new InputFile(
                                        declareMessage.getSecond())),
                                declareMessage.getFirst()
                        );
                    } else {
                        send(users, new SendMessage(), declareMessage.getFirst());
                    }
                }
                pageNumber++; // Move to the next page
            }
        }
    }

    private void send(List<User> users, SendMessage declareSender, String message) {
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
                sendStatisticsToAdmins(count, success, fail);
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Too many request exception");
            }
        }
    }

    private void send(List<User> users, SendPhoto sendPhoto, String caption) {
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
                sendStatisticsToAdmins(count, success, fail);
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Too many request exception");
            }
        }
    }

    private void sendStatisticsToAdmins(Integer count, Integer success, Integer fail) {
        SendMessage sendMessage = new SendMessage();
        for (Long chatId : admins.keySet()) {
            sendMessage.setText(count + " ta habar jo`natildi\nSuccess✅: " + success + "\nFail❌: " + fail);
            sendMessage.setChatId(chatId);
            sender.sendMessage(sendMessage);
        }
    }
}
