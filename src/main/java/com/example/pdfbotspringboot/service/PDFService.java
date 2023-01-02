package com.example.pdfbotspringboot.service;

import com.example.pdfbotspringboot.config.BotConfig;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PDFService {

    private final BotConfig botConfig;
    private StringBuilder apiUrl = new StringBuilder("https://api.telegram.org/file/bot");

    public void generate(List<String> paths, Long chatId){
        for (String path : paths) {
            downloadPhotos(chatId,path);
        }
    }

    @SneakyThrows
    public void downloadPhotos(Long chatId, String path) {
        apiUrl.append(botConfig.getToken()+"/");
        URL url = new URL(apiUrl + path);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();
        FileCopyUtils.copy(inputStream,new FileOutputStream(new File("rasm.jpg")));
    }

    @SneakyThrows
    public String getFilePath(String fileId){
        apiUrl.append(botConfig.getToken()+"/getFile?file_id="+fileId);
        URL url = new URL(apiUrl.toString());
        URLConnection urlConnection = url.openConnection();
        Gson gson = new Gson();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        org.telegram.telegrambots.meta.api.objects.File file = gson.fromJson(inputStreamReader, org.telegram.telegrambots.meta.api.objects.File.class);
        System.out.println(file.getFilePath());
        return file.getFilePath();
    }
}
