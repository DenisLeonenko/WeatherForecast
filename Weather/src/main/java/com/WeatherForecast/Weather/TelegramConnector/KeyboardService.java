package com.WeatherForecast.Weather.TelegramConnector;

import com.WeatherForecast.Weather.Services.ChatConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class KeyboardService {
    @Autowired
    private ChatConfigService chatConfigService;

    private final InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    public InlineKeyboardMarkup setChooseCityKeyboard(Long chatId){
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();

        button1.setText(chatConfigService.getCity(chatId));

        button1.setCallbackData(getCurrentCityNowButton(chatConfigService.getCity(chatId)));

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Other");
        button2.setCallbackData(getChooseCityNowButtonData());

        keyboardRow.add(button1);
        keyboardRow.add(button2);
        keyboardMarkup.setKeyboard(Arrays.asList(keyboardRow));

        return keyboardMarkup;
    }

    String getChooseCityNowButtonData(){
        return "Введіть бажане місто";
    }

    String getCurrentCityNowButton(String city) {
        return "Зараз " + city;
    }
}
