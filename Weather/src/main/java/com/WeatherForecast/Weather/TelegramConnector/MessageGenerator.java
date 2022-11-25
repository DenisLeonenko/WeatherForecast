package com.WeatherForecast.Weather.TelegramConnector;

import com.WeatherForecast.Weather.Services.BotConfigService;
import com.WeatherForecast.Weather.Services.WeatherService;
import com.WeatherForecast.Weather.Weather.WeatherNow;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageGenerator {
    @Autowired
    private BotConfigService botConfigService;
    @Autowired
    private WeatherService weatherService;

    private String message;

    String generateStartMessage(String name) {
        return EmojiParser.parseToUnicode("Привіт, " + name + " :wave: \nЩоб дізнатись, як користуватись ботом - введіть /help");
    }

    String generateHelpMessage() {
        message = "";
        message = ":sunny: Ось мої доступні команди :sunny:\n\n";
        botConfigService.getAllCommand()
                .forEach(command -> {
                    message = message + command.getName() + " - " + command.getDescription() + "\n";
                });
        return EmojiParser.parseToUnicode(message);
    }

    String generateSuccessCancel() {
        return EmojiParser.parseToUnicode(":white_check_mark: Активована команда була успішно відхилена");
    }

    String generateSuccessSetCity(String city) {
        return EmojiParser.parseToUnicode(":white_check_mark: Нове стандартне місто - " + city);
    }

    String generateErrorCity() {
        return EmojiParser.parseToUnicode(":x: Заданого міста не існує");
    }

    String generateSuccessGetCity(String city) {
        return EmojiParser.parseToUnicode(":cityscape: Задане місто - " + city);
    }

    String generateErrorGetCity(){
        return EmojiParser.parseToUnicode(":x: Задане місто не зазначене");
    }

    public String generateCurrentWeather(String city) throws Exception {
        WeatherNow weatherNow = weatherService.getCurrentWeather(city);
        return EmojiParser.parseToUnicode("Погода на цей час \n\n" +
                "У місті " + city + " " + weatherNow.getWeather().get(0).getDescription() + "\n"
        +":thermometer: Температура: " + weatherNow.getMain().getTemp() + "°C, відчувається як " + weatherNow.getMain().getFeelsLike() + "°C");
    }
}
