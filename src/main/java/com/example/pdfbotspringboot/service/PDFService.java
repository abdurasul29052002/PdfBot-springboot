package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.config.BotConfig;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PDFService {

    private final BotConfig botConfig;
    private StringBuilder apiUrl = new StringBuilder("https://api.telegram.org/file/bot");
    public static final String pdfFolder = "/home/ubuntu/files/pdf";
    public static final String imageFolder = "/home/ubuntu/files/images";

    @PostConstruct
    public void init() {
        apiUrl.append(botConfig.getToken() + "/");
    }

    @SneakyThrows
    public void generate(Long chatId, Integer size) {
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFolder + "/" + chatId + ".pdf"));
        document.open();
        document.setPageCount(size);
        for (int i = 0; i < size; i++) {
            Image image = Image.getInstance(imageFolder + "/" + chatId + "_" + i + ".jpg");
            image.scaleToFit(new Rectangle(image.getWidth(), image.getHeight()));
            image.setAbsolutePosition(0, 0);
            document.setPageSize(new Rectangle(image.getWidth(), image.getHeight()));
            document.newPage();
            document.add(image);
        }
        document.close();
        pdfWriter.close();
    }

    @SneakyThrows
    public void downloadPhotos(Long chatId, String filePath, int photoNumber) {
        URL url = new URL(apiUrl.toString() + filePath);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        Path path = Paths.get(imageFolder + "/" + chatId + "_" + photoNumber + ".jpg");
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    }

    public InputFile getPdfFile(Long chatId) {
        return new InputFile(new File(pdfFolder + "/" + chatId + ".pdf"));
    }

    public void deleteFiles(Long chatId, Integer photoCount) {
        for (int i = 0; i < photoCount; i++) {
            File file = new File(imageFolder + "/" + chatId + "_" + i + ".jpg");
            file.delete();
        }
        File file = new File(pdfFolder + "/" + chatId + ".pdf");
        System.out.println("All files deleted");
    }
}
