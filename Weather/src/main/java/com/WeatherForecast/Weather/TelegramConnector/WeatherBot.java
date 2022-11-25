package com.WeatherForecast.Weather.TelegramConnector;

import com.WeatherForecast.Weather.Services.BotConfigService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class WeatherBot extends TelegramLongPollingBot {
    @Autowired
    private BotConfigService botConfigService;

    @Autowired
    private WeatherBotFacade weatherBotFacade;

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken(){
        return botConfigService.getBotAccessToken();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update){
        weatherBotFacade.handleUpdate(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
