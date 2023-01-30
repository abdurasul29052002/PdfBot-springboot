package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.enums.Language;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardService {

    private ReplyKeyboardMarkup getReplyKeyboard(int columnCount, String... texts) {
        int buttonCount = texts.length;
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (int i = 0; i < buttonCount; i++) {
            keyboardRow.add(new KeyboardButton(texts[i]));
            if ((i + 1) % columnCount == 0 || (i + 1) == buttonCount) {
                keyboardRows.add(keyboardRow);
                keyboardRow = new KeyboardRow();
            }
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboard(int columnCount, String... texts) {
        int buttonCount = texts.length;
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = 0; i < buttonCount; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(texts[i]);
            button.setCallbackData(texts[i]);
            row.add(button);
            if ((i + 1) % columnCount == 0 || (i + 1) == buttonCount) {
                rowList.add(row);
                row = new ArrayList<>();
            }
        }
        return new InlineKeyboardMarkup(rowList);
    }

    public ReplyKeyboardMarkup getHomeKeyboard(Language language) {
        switch (language) {
            case ENGLISH -> {
                return getReplyKeyboard(
                        2,
                        "Generate PDF \uD83D\uDCD5",
                        "Compress files\uD83D\uDCDA",
                        "Referral system\uD83D\uDC65"
                );
            }
            case RUS -> {
                return getReplyKeyboard(
                        2,
                        "Генерировать PDF \uD83D\uDCD5",
                        "Сжать файлы\uD83D\uDCDA",
                        "Реферальная система\uD83D\uDC65"
                );
            }
            case UZBEK -> {
                return getReplyKeyboard(
                        2,
                        "PDF yaratish \uD83D\uDCD5",
                        "Fayllarni zip qilish\uD83D\uDCDA",
                        "Referal tizimi\uD83D\uDC65"
                );
            }
            default -> {
                return null;
            }
        }
    }

    public ReplyKeyboardMarkup getPdfKeyboard(Language language) {
        switch (language) {
            case ENGLISH -> {
                return getReplyKeyboard(
                        1,
                        "Generate\uD83D\uDCD5"
                );
            }
            case RUS -> {
                return getReplyKeyboard(
                        1,
                        "Генерировать\uD83D\uDCD5"
                );
            }
            case UZBEK -> {
                return getReplyKeyboard(
                        1,
                        "Yaratish\uD83D\uDCD5"
                );
            }
            default -> {
                return null;
            }
        }
    }

    public ReplyKeyboardMarkup getCompressKeyboard(Language language) {
        switch (language) {
            case ENGLISH -> {
                return getReplyKeyboard(
                        1, "Compress\uD83D\uDCDA"
                );
            }
            case RUS -> {
                return getReplyKeyboard(
                        1, "Сжать\uD83D\uDCDA"
                );
            }
            case UZBEK -> {
                return getReplyKeyboard(
                        1, "Zip\uD83D\uDCDA"
                );
            }
            default -> {
                return null;
            }
        }
    }

    public InlineKeyboardMarkup getShareKeyboard(Language language){
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardButton shareButton = new InlineKeyboardButton();
        switch (language) {
            case ENGLISH -> shareButton.setText("Share");
            case RUS -> shareButton.setText("Поделится");
            case UZBEK -> shareButton.setText("Ulashish");
        }
        shareButton.setUrl("https://t.me/share/url?url=https://t.me/pdfdocsbot?start=" + UserService.currentUser.getId());
        row.add(shareButton);
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboard getReferralKeyboard(Language language) {
        switch (language) {
            case ENGLISH -> {
                return getReplyKeyboard(
                        2,
                        "My referrals\uD83D\uDC65",
                        "My referral link\uD83D\uDD17",
                        "Top referrals\uD83C\uDFC6",
                        "Main menu\uD83C\uDFE0"
                );
            }
            case RUS -> {
                return getReplyKeyboard(
                        2,
                        "Мои рефералы\uD83D\uDC65",
                        "Моя реферальная ссылка\uD83D\uDD17",
                        "Топ рефералы\uD83C\uDFC6",
                        "Главное меню\uD83C\uDFE0"
                );
            }
            case UZBEK -> {
                return getReplyKeyboard(
                        2,
                        "Mening referallarim\uD83D\uDC65",
                        "Mening referal ssilkam\uD83D\uDD17",
                        "Top referallar\uD83C\uDFC6",
                        "Bosh menyu\uD83C\uDFE0"
                );
            }
            default -> {
                return null;
            }
        }
    }

    public ReplyKeyboardMarkup getAdminKeyboard() {
        return getReplyKeyboard(
                2,
                "Foydalanuvchilarga habar jo`natish",
                "Foydalanuvchilar soni",
                "Statistika"
        );
    }

    public InlineKeyboardMarkup getYesOrNoKeyboard(){
        return getInlineKeyboard(
                2,
                "Ha✅",
                "Yo`q❌"
        );
    }

    public InlineKeyboardMarkup getLanguageKeyboard() {
        return getInlineKeyboard(3, "English", "Русский", "O`zbekcha");
    }

    public ReplyKeyboardMarkup getChoosePanelKeyboard(){
        return getReplyKeyboard(2, "Admin panel", "User panel");
    }
}
