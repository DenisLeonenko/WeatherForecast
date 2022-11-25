package com.WeatherForecast.Weather.TelegramConnector;

import com.WeatherForecast.Weather.Services.BotConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CallbackAnswer {
    @Autowired
    private BotConfigService botConfigService;

    public void callBackAnswer(String callbackId) throws IOException, InterruptedException{
        HttpClient telegramApiClient = HttpClient.newHttpClient();
        HttpRequest telegramCallBackanswerReq = HttpRequest.newBuilder(URI.create(
                botConfigService.getTelegramCallbackAnswerTemp()
                        .replace("{token}", botConfigService.getBotAccessToken())
                        .replace("{id}", callbackId)
        )).GET().build();

        telegramApiClient.send(telegramCallBackanswerReq, HttpResponse.BodyHandlers.ofString());
    }
}