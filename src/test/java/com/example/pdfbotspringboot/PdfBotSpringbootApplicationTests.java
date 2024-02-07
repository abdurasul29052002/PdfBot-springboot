package com.example.pdfbotspringboot;

import com.example.pdfbotspringboot.entity.User;
import com.example.pdfbotspringboot.enums.BotState;
import com.example.pdfbotspringboot.enums.Language;
import com.example.pdfbotspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfBotSpringbootApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void saveUserTest(){
        User user = new User(
                null,
                "Legolas",
                123456789L,
                BotState.START,
                Language.UZBEK,
                true,
                null,
                0
        );
        User savedUser = userRepository.save(user);
        assertNotEquals(null, savedUser.getId());

    }

}
