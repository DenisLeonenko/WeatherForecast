package com.WeatherForecast.Weather.TelegramConnector;

import com.WeatherForecast.Weather.Bot.BotState;
import com.WeatherForecast.Weather.Bot.KeyboardType;
import com.WeatherForecast.Weather.Bot.MainCommand;
import com.WeatherForecast.Weather.Services.ChatConfigService;
import com.WeatherForecast.Weather.Services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;


@Component
public class WeatherBotFacade {
    @Autowired
    private ChatConfigService chatConfigService;
    @Autowired
    private  MessageGenerator messageGenerator;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private KeyboardService keyboardService;
    @Autowired
    private WeatherBot weatherBot;
    @Autowired
    private CallbackAnswer callbackAnswer;

    public void handleUpdate (Update update) throws Exception {
        String messageText;
        Long chatId;
        String userFirstName = "";

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            messageText = update.getMessage().getText().toUpperCase(Locale.ROOT).replace("/", "");
            userFirstName = update.getMessage().getChat().getFirstName();
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
            messageText = update.getChannelPost().getText().toUpperCase(Locale.ROOT).replace("/", "");
            userFirstName = update.getChannelPost().getChat().getFirstName();
        } else if (update.hasCallbackQuery()) {
            callbackAnswer.callBackAnswer(update.getCallbackQuery().getId());

            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageText = update.getCallbackQuery().getData().toUpperCase(Locale.ROOT);
            sendMessage(update, update.getCallbackQuery().getData());

            if (messageText.equals(keyboardService.getChooseCityNowButtonData().toUpperCase(Locale.ROOT))){
                chatConfigService.setBotState(chatId, BotState.SEARCH_NOW);
                return;
            } else if (messageText.equals(keyboardService.getCurrentCityNowButton(chatConfigService.getCity(chatId)).toUpperCase(Locale.ROOT))) {
                chatConfigService.setBotState(chatId, BotState.NOW);
            }
        } else if (update.hasMyChatMember()) {
            if(update.getMyChatMember().getNewChatMember().getStatus().equals("kicked")) {
                chatConfigService.deleteChat(update.getMyChatMember().getChat().getId());
            }
            return;
        }else{
            return;
        }
        if(!chatConfigService.isChatInit(chatId)){
            chatConfigService.initChat(chatId);
            sendMessage(update, messageGenerator.generateStartMessage(userFirstName));
        }else {
            handleBotState(update, chatId, messageText, userFirstName);
        }
    }

    private Long setChatIdToMessageBuilder(Update update, SendMessage.SendMessageBuilder messageBuilder){
        Long chatId = null;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            messageBuilder.chatId(update.getMessage().getChatId().toString());
        } else if (update.hasChannelPost()) {
            chatId = update.getChannelPost().getChatId();
            messageBuilder.chatId(update.getChannelPost().getChatId().toString());
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageBuilder.chatId(update.getCallbackQuery().getMessage().getChatId().toString());
        }
        return chatId;
    }

    private void sendMessage(Update update, String messageText){
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();

        Long chatId = setChatIdToMessageBuilder(update, messageBuilder);

        messageBuilder.text(messageText);

        try{
            weatherBot.execute(messageBuilder.build());
        }catch (TelegramApiException telegramApiException){
            telegramApiException.printStackTrace();
        }
    }

    private void sendMessage(Update update, String messageText, KeyboardType keyboardType){
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder();

        Long chatId = setChatIdToMessageBuilder(update, messageBuilder);

        messageBuilder.text(messageText);

        switch (keyboardType){
            case CITY_CHOOSE: {
                messageBuilder.replyMarkup(keyboardService.setChooseCityKeyboard(chatId));
                break;
            }
        }
        try{
            weatherBot.execute(messageBuilder.build());
        }catch (TelegramApiException telegramApiException){
            telegramApiException.printStackTrace();
        }
    }

    private void handleBotState(Update update, Long chatId, String messageText, String userFirstName) throws Exception {
        BotState botState = chatConfigService.getBotState(chatId);

        if (messageText.equals(MainCommand.START.name())){
            chatConfigService.setBotState(chatId, BotState.DEFAULT);
            sendMessage(update, messageGenerator.generateStartMessage(userFirstName));
            return;
        }

        if (messageText.equals(MainCommand.CANCEL.name())){
            if (botState == BotState.DEFAULT){
                sendMessage(update, " Нема активних дій для відключення");
            }else {
                chatConfigService.setBotState(chatId, BotState.DEFAULT);
                sendMessage(update, messageGenerator.generateSuccessCancel());
                return;
            }
        }

        switch (botState){
            case DEFAULT: {

                if (messageText.equals(MainCommand.HELP.name())) {
                    sendMessage(update, messageGenerator.generateHelpMessage());
                } else if (messageText.equals(MainCommand.SETCITY.name())) {
                    chatConfigService.setBotState(chatId, BotState.SET_CITY);
                    sendMessage(update, "Введіть нове стандартне місто");
                }else if (messageText.equals(MainCommand.CITY.name())){
                    if (chatConfigService.getCity(chatId) != null && !chatConfigService.getCity(chatId).equals("")) sendMessage(update, messageGenerator.generateSuccessGetCity(chatConfigService.getCity(chatId)));
                    else sendMessage(update, messageGenerator.generateErrorGetCity());
                } else if (messageText.equals(MainCommand.NOW.name())) {
                    chatConfigService.setBotState(chatId, BotState.NOW);
                    sendMessage(update, "Оберіть місто", KeyboardType.CITY_CHOOSE);
                }
                break;
            }
            case SET_CITY: {

                if(weatherService.isCity(messageText.toLowerCase(Locale.ROOT))){
                    chatConfigService.setCity(chatId, messageText.charAt(0) + messageText.substring(1).toLowerCase(Locale.ROOT));
                    chatConfigService.setBotState(chatId, BotState.DEFAULT);
                    sendMessage(update, messageGenerator.generateSuccessSetCity(chatConfigService.getCity(chatId)));
                }
                 else sendMessage(update, messageGenerator.generateErrorCity());
                 break;
                }
            case NOW: {
                if(messageText.equals(keyboardService.getChooseCityNowButtonData().toUpperCase(Locale.ROOT))){
                    chatConfigService.setBotState(chatId, BotState.SEARCH_NOW);
                }else {
                    chatConfigService.setBotState(chatId, BotState.DEFAULT);
                    sendMessage(update, messageGenerator.generateCurrentWeather(chatConfigService.getCity(chatId)));
                }
                break;
            }
            case SEARCH_NOW: {
                if (!weatherService.isCity(messageText)){
                    sendMessage(update, messageGenerator.generateErrorCity());
                }else {
                    sendMessage(update, messageGenerator.generateCurrentWeather(messageText.charAt(0) + messageText.substring(1).toLowerCase(Locale.ROOT)));
                    chatConfigService.setBotState(chatId, BotState.DEFAULT);
                }
                break;
            }
        }
    }
}
