package com.example.pdfbotspringboot.entity;

import com.example.pdfbotspringboot.enums.BotState;
import com.example.pdfbotspringboot.enums.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "botstate")
    private BotState botState;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_user")
    private Language languageUser;
}
