package com.WeatherForecast.Weather.Services;

import com.WeatherForecast.Weather.Bot.Command;
import com.WeatherForecast.Weather.Repo.BotConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotConfigService {
    @Autowired
    private BotConfigRepo botConfigRepo;

    public String getTelegramCallbackAnswerTemp(){
        return this.botConfigRepo.findAll().get(0).getTelegramCallbackAnswerTemp();
    }

    public String getNowApiTemp(){
        return this.botConfigRepo.findAll().get(0).getNowWeatherApiTemp();
    }

    public List<Command> getAllCommand(){
        return botConfigRepo.findAll().get(0).getCommands();
    }

    public String getBotUsername(){
        return botConfigRepo.findAll().get(0).getName();
    }

    public String getBotAccessToken(){
        return botConfigRepo.findAll().get(0).getAccessToken();
    }
}
