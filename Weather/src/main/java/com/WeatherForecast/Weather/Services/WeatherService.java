package com.WeatherForecast.Weather.Services;

import com.WeatherForecast.Weather.Weather.WeatherNow;
import com.WeatherForecast.Weather.Weather.WeatherRestMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {
    @Autowired
    private WeatherRestMap weatherRestMap;

    public boolean isCity(String city) throws IOException{
        return weatherRestMap.isCity(city);
    }

    public WeatherNow getCurrentWeather(String city) throws Exception {
        return weatherRestMap.getNowWeather(city);
    }
}
