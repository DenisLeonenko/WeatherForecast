package com.WeatherForecast.Weather.Weather;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WeatherNow {

    private List<Weather> weather;
    private Main main;
}
