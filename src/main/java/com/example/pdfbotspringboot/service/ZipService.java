package com.example.pdfbotspringboot.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.example.pdfbotspringboot.PdfBotSpringbootApplication.*;

@Service
public class ZipService {
    @SneakyThrows
    public void zip(Long chatId) {
        ZipUtil.pack(new File(compressFolder + "/" + chatId), new File(compressFolder + "/" + chatId + ".zip"));
    }

    @SneakyThrows
    public void downloadFiles(Long chatId, String filePath, String originalFileName) {
        URL url = new URL(apiUrl + filePath);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        File file = new File(compressFolder+"/"+chatId);
        file.mkdir();
        Path path = Paths.get(compressFolder + "/" + chatId + "/" + originalFileName);
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    }

    public InputFile getZip(Long chatId) {
        return new InputFile(new File(compressFolder + "/"+chatId+".zip"));
    }

    public void deleteFiles(Long chatId) {
        File file = new File(compressFolder + "/" + chatId);
        File[] files = file.listFiles();
        for (File file1 : files != null ? files : new File[0]) {
            file1.delete();
        }
        file.delete();
    }
}
