package com.WeatherForecast.Weather.Repo;

import com.WeatherForecast.Weather.Bot.BotConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

public interface BotConfigRepo extends MongoRepository<BotConfig, BigInteger> {
}
