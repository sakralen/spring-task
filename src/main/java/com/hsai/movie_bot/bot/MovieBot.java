package com.hsai.movie_bot.bot;

import com.hsai.movie_bot.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MovieBot extends TelegramLongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MovieBot.class);
    private static final String START = "/start";
    private static final String RECOMMEND = "/recommend";
    private static final String HELP = "/help";

    private static final String COMMANDS = """
                You can use these commands:
                /recommend <genre> - recommend film with certain genre or random if genre was not mentioned.\040\040\040\040\040\040\040\040\040\040\040\040
                -----
                /help - get help
            """;
    private static final String CONNECTION_ERROR = "Can't connect to the server!";

    @Autowired
    private MovieService movieService;

    public MovieBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText().split(" ");
        var chatId = update.getMessage().getChatId();
        switch (message[0]) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case RECOMMEND -> recommendFilm(chatId, message);
            // case SHOW_PREFERENCES -> showPreferences(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    @Override
    public String getBotUsername() {
        return "hsai_2023_movie_bot";
    }

    private void startCommand(Long chatId, String userName) {
        try {
            movieService.getUserId(userName);
        } catch (Exception e) {
            LOG.error("Error of getting user name.", e);
            sendMessage(chatId, CONNECTION_ERROR);
        }
        var text = """
                Welcome to movie recommendation bot, %s!
                %s
                """;
        var formattedText = String.format(text, userName, COMMANDS);
        sendMessage(chatId, formattedText);
    }

    private void recommendFilm(Long chatId, String[] message) {
        String filmGenreName = null;
        var genre = "";
        try {
            if (message.length > 1) {
                genre = message[1];
            }
            filmGenreName = movieService.getRecommendedFilm(genre);
        } catch (Exception e) {
            LOG.error("Error of getting movie.", e);
            sendMessage(chatId, CONNECTION_ERROR);
        }
        assert filmGenreName != null;
        sendMessage(chatId, "We recommend you to watch %s in genre %s.".formatted(
                filmGenreName.split("~")[1], filmGenreName.split("~")[0]));
    }

    private void helpCommand(Long chatId) {
        var text = """
                Bot background information
                                
                %s
                """;
        sendMessage(chatId, text.formatted(COMMANDS));
    }

    private void unknownCommand(Long chatId) {
        var text = "Could not recognize the command!";
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Sending message error", e);
        }
    }
}