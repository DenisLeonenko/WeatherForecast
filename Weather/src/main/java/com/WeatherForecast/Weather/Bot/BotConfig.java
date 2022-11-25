package com.WeatherForecast.Weather.Bot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collation = "bot_config")
public class BotConfig {

    @Id
    private BigDecimal id;

    private String name;


    private String accessToken;

    //for info :
    //https://api.telegram.org/bot{token}/answerCallbackQuery?callback_query_id={id}
    private String telegramCallbackAnswerTemp;

    //http://api.openweathermap.org/data/2.5/weather?q={city}&appid={key}&units=metric&lang=ua
    private String nowWeatherApiTemp;

    private List<Command> commands;
}
